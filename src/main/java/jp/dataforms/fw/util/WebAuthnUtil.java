package jp.dataforms.fw.util;

import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.webauthn4j.WebAuthnManager;
import com.webauthn4j.converter.AttestationObjectConverter;
import com.webauthn4j.converter.CollectedClientDataConverter;
import com.webauthn4j.converter.exception.DataConversionException;
import com.webauthn4j.converter.util.ObjectConverter;
import com.webauthn4j.credential.CredentialRecord;
import com.webauthn4j.credential.CredentialRecordImpl;
import com.webauthn4j.data.AuthenticationData;
import com.webauthn4j.data.AuthenticationParameters;
import com.webauthn4j.data.AuthenticationRequest;
import com.webauthn4j.data.RegistrationData;
import com.webauthn4j.data.RegistrationParameters;
import com.webauthn4j.data.RegistrationRequest;
import com.webauthn4j.data.attestation.AttestationObject;
import com.webauthn4j.data.client.CollectedClientData;
import com.webauthn4j.server.ServerProperty;

import jp.dataforms.fw.app.user.dao.WebAuthnTable;

/**
 * WebAuthn関連ユーティリティクラス。
 */
public final class WebAuthnUtil {
	/**
	 * Logger.
	 */
	private static Logger logger = LogManager.getLogger(WebAuthnUtil.class);
	
	/**
	 * コンストラクタ。
	 */
	private WebAuthnUtil() {
		
	}
	
	
	/**
	 * URL安全なBASE64に変換.
	 *
	 * @param base64
	 * @return base64url
	 */
	public String convertBase64SafeUrl(final String base64) {
	    if(base64 == null) {
	        return null;
	    } else {
	        String ret = base64;
	        ret = ret.replaceAll("\\+", "-");
	        ret = ret.replaceAll("\\/", "_");
	        ret = ret.replaceAll("=+$", "");
	        return ret;
	    }
	}

	/**
	 * 通常 (URL非安全) のBASE64に変換.
	 *
	 * @param base64url
	 * @return base64
	 */
	public static String convertBase64UnSafeUrl(final String base64url) {
	    if(base64url == null) {
	        return null;
	    } else {
	        String ret = base64url;
	        ret = ret.replaceAll("-", "+");
	        ret = ret.replaceAll("_", "/");
	        ret += "===";
	        ret = ret.substring(0, ((ret.length() / 4) * 4));
	        return ret;
	    }
	}

	
	/**
	 * ランダムなchallengeを生成する。
	 * @return ランダムなchallenge。
	 */
	public static String generateChallenge() {
		long seed = (new java.util.Date()).getTime();
		Random r = new Random(seed);
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 16; i++) {
			int v = r.nextInt(10);
			sb.append(Integer.toString(v));
		}
		return sb.toString();
	}

	/**
	 * WebAuthnTableに保存可能な認証機の登録情報を取得します。
	 * @param p POSTされたパラメータ。
	 * @param serverProperty サーバープロパティ。
	 * @return 認証機の登録情報。
	 * @throws Exception 例外。
	 */
	public static Map<String, Object> getRegistDataMap(final Map<String, Object> p, final ServerProperty serverProperty) throws Exception {
		String id = (String) p.get("id");
		String authenticatorAttachment = (String) p.get("authenticatorAttachment");
		String type = (String) p.get("type");
		String attestationObject = (String) p.get("attestationObject");
		String clientDataJSON = (String) p.get("clientDataJSON");
		logger.debug("id=" + id);
		logger.debug("authenticatorAttachment=" + authenticatorAttachment);
		logger.debug("type=" + type);
		logger.debug("attestationObject=" + attestationObject);
		logger.debug("clientDataJSON=" + clientDataJSON);
		byte[] ao = Base64.getDecoder().decode(WebAuthnUtil.convertBase64UnSafeUrl(attestationObject));
		logger.debug("ao.length=" + ao.length);
		byte[] cdj = Base64.getDecoder().decode(WebAuthnUtil.convertBase64UnSafeUrl(clientDataJSON));
		logger.debug("cdj.length=" + cdj.length);
		
		WebAuthnManager webAuthnManager = WebAuthnManager.createNonStrictWebAuthnManager();		
		boolean userVerificationRequired = false;
		boolean userPresenceRequired = true;
		RegistrationRequest registrationRequest = new RegistrationRequest(ao, cdj);
		RegistrationParameters registrationParameters = new RegistrationParameters(serverProperty, null, userVerificationRequired, userPresenceRequired);
		RegistrationData registrationData = webAuthnManager.parse(registrationRequest);
		webAuthnManager.validate(registrationData, registrationParameters);
		logger.debug("AttestationObject:" + registrationData.getAttestationObject().toString());
		logger.debug("CollectedClientData:" + registrationData.getCollectedClientData().toString());
		AttestationObjectConverter attestationObjectConverter = new AttestationObjectConverter(new ObjectConverter());
		String attestationObjectBase64 = attestationObjectConverter.convertToBase64urlString(registrationData.getAttestationObject());
		CollectedClientDataConverter collectedClientDataConverter = new CollectedClientDataConverter(new ObjectConverter());
		String collectedClientDataBase64 = collectedClientDataConverter.convertToBase64UrlString(registrationData.getCollectedClientData());
	    logger.debug("getAttestationObjectBytes() = " + attestationObjectBase64);
	    logger.debug("getCollectedClientDataBytes() = " + collectedClientDataBase64);
	    logger.debug("getClientExtensions() = " + registrationData.getClientExtensions());
	    logger.debug("getTransports() = " + registrationData.getTransports());
	    WebAuthnTable.Entity e = new WebAuthnTable.Entity();
	    e.setAuthId(id);
	    e.setAuthType(type);
	    e.setAuthenticatorAttachment(authenticatorAttachment);
	    e.setAttestationObject(attestationObjectBase64);
	    e.setCollectedClientData(collectedClientDataBase64);
	    return e.getMap();
	}
	
	
	/**
	 * 認証データを確認します。
	 * @param p クライアントから送信された認証データ。
	 * @param credentialRecord 確認情報レコード。
	 * @param serverProperty サーバープロパティ。
	 * @return 認証結果。
	 */
	public static AuthenticationData checkAuthenticationData(final Map<String, Object> p, final CredentialRecord credentialRecord, final ServerProperty serverProperty)  {
		String id = (String) p.get("id");
		String authenticatorAttachment = (String) p.get("authenticatorAttachment");
		String type = (String) p.get("type");
		String authenticatorData = (String) p.get("authenticatorData");
		String clientDataJSON = (String) p.get("clientDataJSON");
		String signature = (String) p.get("signature");
		String userHandle = (String) p.get("userHandle");
		
		logger.debug("id=" + id);
		logger.debug("authenticatorAttachment=" + authenticatorAttachment);
		logger.debug("type=" + type);
		logger.debug("authenticatorData=" + authenticatorData);
		logger.debug("clientJSON=" + clientDataJSON);
		logger.debug("signature=" + signature);
		logger.debug("userHandle=" + userHandle);
		
		List<byte[]> allowCredentials = null;
		boolean userVerificationRequired = true;
		boolean userPresenceRequired = true;
		
		AuthenticationRequest authenticationRequest =
		        new AuthenticationRequest(
		        		Base64.getDecoder().decode(WebAuthnUtil.convertBase64UnSafeUrl(id)),
		        		Base64.getDecoder().decode(WebAuthnUtil.convertBase64UnSafeUrl(userHandle)),
		        		Base64.getDecoder().decode(WebAuthnUtil.convertBase64UnSafeUrl(authenticatorData)),
		        		Base64.getDecoder().decode(WebAuthnUtil.convertBase64UnSafeUrl(clientDataJSON)),
		                null,
		                Base64.getDecoder().decode(WebAuthnUtil.convertBase64UnSafeUrl(signature))
		        );
		AuthenticationParameters authenticationParameters =
		        new AuthenticationParameters(
		                serverProperty,
		                credentialRecord,
		                allowCredentials,
		                userVerificationRequired,
		                userPresenceRequired
		        );
	
		WebAuthnManager webAuthnManager = WebAuthnManager.createNonStrictWebAuthnManager();
		AuthenticationData authenticationData = null;
		try {
			authenticationData = webAuthnManager.parse(authenticationRequest);
		} catch (DataConversionException e) {
			throw e;
		}
		authenticationData = webAuthnManager.validate(authenticationData, authenticationParameters);
		long count = authenticationData.getAuthenticatorData().getSignCount();
		logger.debug("signCount=" + count);
		return authenticationData;

	}
	
	
	/**
	 * WebAuthenテーブルの情報から確認情報レコードを取得する。
	 * @param webAuthnInfo 認証情報。
	 * @return 確認情報レコード。
	 */
	public static CredentialRecord getCredentialRecord(final Map<String, Object> webAuthnInfo) {
		WebAuthnTable.Entity e = new WebAuthnTable.Entity(webAuthnInfo);
		//
		String attestationObjectBase64 = e.getAttestationObject();
		AttestationObjectConverter attestationObjectConverter = new AttestationObjectConverter(new ObjectConverter());
		AttestationObject ao = attestationObjectConverter.convert(attestationObjectBase64);
		logger.debug("ao=" + ao.toString());
		//
		String collectedClientDataBase64 = e.getCollectedClientData();
		CollectedClientDataConverter collectedClientDataConverter = new CollectedClientDataConverter(new ObjectConverter());
		CollectedClientData cd = collectedClientDataConverter.convert(collectedClientDataBase64);
		logger.debug("cd=" + cd.toString());
		CredentialRecord ret = new CredentialRecordImpl(ao, cd, null, null);
		return ret;
	}

	
	/**
	 * FLAGS中のBS BIT(バックアップ状態)。
	 */
	private static final int BS_FLAG = 0x10;
	

	/**
	 * FLAGS中のBE BIT(バックアップの可)。
	 */
	private static final int BE_FLAG = 0x08;

	/**
	 * Flagsを取得します。
	 * @param webAuthnInfo 認証情報。
	 * @return Flags。
	 */
	public static byte getFlags(final Map<String, Object> webAuthnInfo) {
		WebAuthnTable.Entity e = new WebAuthnTable.Entity(webAuthnInfo);
		//
		String attestationObjectBase64 = e.getAttestationObject();
		AttestationObjectConverter attestationObjectConverter = new AttestationObjectConverter(new ObjectConverter());
		AttestationObject ao = attestationObjectConverter.convert(attestationObjectBase64);
		return ao.getAuthenticatorData().getFlags();
	}
	
	
	/**
	 * クラウド共有されているパスキーかどうかを判定します。
	 * @param webAuthnInfo WebAuthnTableのレコード。
	 * @return クラウド共有されている場合true。
	 */
	public static Boolean shared(final Map<String, Object> webAuthnInfo) {
		byte flags = WebAuthnUtil.getFlags(webAuthnInfo);
		if ((flags & WebAuthnUtil.BE_FLAG) != 0 && (flags & WebAuthnUtil.BS_FLAG) != 0) {
			return true;
		} else {
			return false;
		}
		
	}
}
