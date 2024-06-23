package jp.dataforms.fw.util;

import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Hex;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jakarta.servlet.ServletContext;
import jp.dataforms.fw.servlet.DataFormsServlet;

/**
 * 暗号化ユーティリティクラス。
 *
 * <pre>
 * web.xmlのコンテキストパラメータcrypt-passwordで暗号化のパスワードを設定することができます。
 * </pre>
 *
 */
public final class CryptUtil {

	/**
	 * Logger.
	 */
	private static Logger logger = LogManager.getLogger(CryptUtil.class);

	/**
	 * バイト列変更時の文字コード。
	 */
	private static final String ENCODING = "utf-8";

	/**
	 * DESアルゴリズムを示す識別子。
	 */
	public static final String DES_ALGORITHM = "des";

	/**
	 * AESアルゴリズムを示す識別子。
	 */
	public static final String AES_ALGORITHM = "aes";

	/**
	 * アルゴリズムを示す識別子。
	 */
	public static final String ALGORITHM = "algorithm";

	/**
	 * AESの初期化ベクトル。
	 */
	public static final String AES_INITIAL_VECTOR = "aesInitialVector";

	/**
	 * デフォルトパスワード。
	 */
	public static final String DEFAULT_PASSWORD = "defaultPassword";

	/**
	 * QueryString暗号化パスワード。
	 */
	public static final String QUERY_STRING_CRYPT_PASSWORD = "queryStringCryptPassword";

	/**
	 * CSRF対策パスワード。
	 */
	public static final String CSRF_SESSIONID_CRYPT_PASSWORD = "csrfSessionidCryptPassword";

	/**
	 * デフォルトのDESパスワードまたはAES KEY。
	 */
	public static final String DES_PASSWORD_OR_AES_KEY = "d@d@f0ms";


	/**
	 * コンストラクタ。
	 */
	private CryptUtil() {

	}


	/**
	 * 暗号化アルゴリズム。
	 */
	private static String algorithm = DES_ALGORITHM;

	/**
	 * 暗号化アルゴリズムを取得します。
	 * @return 暗号化アルゴリズム。
	 */
	public static String getAlgorithm() {
		return algorithm;
	}

	/**
	 * 暗号化アルゴリズムを設定します。
	 * @param algorithm 暗号化アルゴリズム。
	 */
	public static void setAlgorithm(final String algorithm) {
		CryptUtil.algorithm = algorithm;
	}

	/**
	 * AESの初期化ベクトル。
	 */
	private static String aesInitialVector = "1234567890123456";

	/**
	 * AESの初期化ベクトルを取得します。
	 * @return AESの初期化ベクトル。
	 */
	public static String getAesInitialVector() {
		return aesInitialVector;
	}

	/**
	 * AESの初期化ベクトルを設定します。
	 * @param initialVector AESの初期化ベクトル。
	 */
	public static void setAesInitialVector(final String initialVector) {
		CryptUtil.aesInitialVector = initialVector;
	}

	/**
	 * 暗号化のためのパスワード。
	 */
	private static String cryptPassword = "P@ssw0rd";

	/**
	 *
	 * 暗号用パスワードを設定します。
	 *
	 * @param cryptPassword 暗号用パスワード。
	 */
	public static void setCryptPassword(final String cryptPassword) {
		CryptUtil.cryptPassword = cryptPassword;
	}

	/**
	 *
	 * キーを取得します。
	 *
	 * @param pass パスワード。
	 * @return キー。
	 * @throws Exception 例外。
	 */
	private static byte[] getKey(final String pass) throws Exception {
        byte[] password = pass.getBytes();
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(password);
        Arrays.fill(password, (byte) 0x00);
        byte[] pssKey = md.digest();
        return pssKey;
	}

	/**
	 *
	 * 秘密キーを取得します。
	 *
	 * @param pssKey パスワード。
	 * @return 秘密キー。
	 * @throws Exception 例外。
	 */
	private static SecretKey getSecretKey(final byte[] pssKey) throws Exception {
		DESKeySpec desKeySpec = new DESKeySpec(pssKey);
		Arrays.fill(pssKey, (byte) 0x00);
		SecretKeyFactory desKeyFac = SecretKeyFactory.getInstance("DES");
		SecretKey desKey = desKeyFac.generateSecret(desKeySpec);
		return desKey;
	}

	/**
	 *
	 * 暗号化を行ないます。
	 *
	 * @param text 暗号化するテキスト。
	 * @return 暗号化されたテキスト。
	 * @throws Exception 例外。
	 */
	public static String encrypt(final String text) throws Exception {
		return CryptUtil.encrypt(text, CryptUtil.cryptPassword);
	}

	/**
	 *
	 * DESで暗号化します。
	 *
	 * @param text 暗号化するテキスト。
	 * @param password パスワード。
	 * @return DESで暗号化します。
	 * @throws Exception 例外。
	 */
	public static String desEncrypt(final String text, final String password) throws Exception {
		SecretKey desKey = getSecretKey(getKey(password));
		Cipher c = Cipher.getInstance("DES");
		c.init(Cipher.ENCRYPT_MODE, desKey);
		byte[] input = text.getBytes(ENCODING);
		byte[] encrypted = c.doFinal(input);
		return Base64.getEncoder().encodeToString(encrypted);
	}

	/**
	 * 文字列を16Byteに調整します。
	 * @param text テキスト。
	 * @return 16Byteに保管したテキスト。
	 */
	private static String get16BytesString(final String text) {
		String text16 = text + "1234567890123456";
		return text16.substring(0, 16);
	}


	/**
	 *
	 * DESで暗号化します。
	 *
	 * @param text 暗号化するテキスト。
	 * @param password パスワード。
	 * @param initialVector 初期化ベクトル。
	 * @return 暗号化テキスト。
	 * @throws Exception 例外。
	 */
	public static String aesEncrypt(final String text, final String password, final String initialVector) throws Exception {
		final IvParameterSpec iv = new IvParameterSpec(CryptUtil.get16BytesString(initialVector).getBytes(ENCODING));
		String aeskey = CryptUtil.get16BytesString(password);
		final SecretKeySpec key = new SecretKeySpec(aeskey.getBytes(ENCODING), "AES");
        // Cipherオブジェクト生成
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        // Cipherオブジェクトの初期化
        cipher.init(Cipher.ENCRYPT_MODE, key, iv);
        // 暗号化の結果格納
        byte[] encrypted = cipher.doFinal(text.getBytes(ENCODING));
        return Base64.getEncoder().encodeToString(encrypted);
	}

	/**
	 *
	 * 暗号化を行ないます。
	 *
	 * @param text 暗号化するテキスト。
	 * @param password パスワード。
	 * @return 暗号化されたテキスト。
	 * @throws Exception 例外。
	 */
	public static String encrypt(final String text, final String password) throws Exception {
		if (!StringUtil.isBlank(text)) {
			if (DES_ALGORITHM.equals(CryptUtil.algorithm)) {
				return CryptUtil.desEncrypt(text, password);
			} else {
				return CryptUtil.aesEncrypt(text, password, CryptUtil.aesInitialVector);
			}
		} else {
			return null;
		}
	}

	/**
	 *
	 * 複合を行ないます。
	 *
	 * @param text 暗号化されたテキスト。
	 * @return 複合されたテキスト。
	 * @throws Exception 例外。
	 */
	public static String decrypt(final String text) throws Exception {
		return CryptUtil.decrypt(text, CryptUtil.cryptPassword);
	}

	/**
	 *
	 * DESで複合します。
	 *
	 * @param text 暗号化テキスト。
	 * @param password パスワード。
	 * @return 複合化テキスト。
	 * @throws Exception 例外。
	 */
	public static String desDecrypt(final String text, final String password) throws Exception {
		SecretKey desKey = getSecretKey(getKey(password));
		Cipher c = Cipher.getInstance("DES");
		c.init(Cipher.DECRYPT_MODE, desKey);
		byte[] input =  Base64.getDecoder().decode(text);
		byte[] decrypted = c.doFinal(input);
		return new String(decrypted, ENCODING);
	}

	/**
	 *
	 * AESで複合します。
	 *
	 * @param text 暗号化テキスト。
	 * @param textKey KEY。
	 * @param initalVector 初期化ベクトル。
	 * @return 複合化テキスト。
	 * @throws Exception 例外。
	 */
	public static String aesDecrypt(final String text, final String textKey, final String initalVector) throws Exception {
		final IvParameterSpec iv = new IvParameterSpec(CryptUtil.get16BytesString(initalVector).getBytes(ENCODING));
		String aeskey = CryptUtil.get16BytesString(textKey);
		final SecretKeySpec key = new SecretKeySpec(aeskey.getBytes(ENCODING), "AES");
        // Cipherオブジェクト生成
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        // Cipherオブジェクトの初期化
        cipher.init(Cipher.DECRYPT_MODE, key, iv);
		byte[] input =  Base64.getDecoder().decode(text);
        // 復号化の結果格納
        byte[] decrypted = cipher.doFinal(input);
		return new String(decrypted, ENCODING);
	}

	/**
	 *
	 * 複合を行ないます。
	 *
	 * @param text 暗号化されたテキスト。
	 * @param password パスワード。
	 * @return 複合されたテキスト。
	 * @throws Exception 例外。
	 */
	public static String decrypt(final String text, final String password) throws Exception {
		if (!StringUtil.isBlank(text)) {
			if (DES_ALGORITHM.equals(CryptUtil.algorithm)) {
				return CryptUtil.desDecrypt(text, password);
			} else {
				return CryptUtil.aesDecrypt(text, password, CryptUtil.aesInitialVector);
			}
		} else {
			return null;
		}
	}


	/**
	 * ハッシュ値。
	 * @param algorithm アルゴリズム(SHA-1, SHA-256, SHA-384, SHA-512)。
	 * @param text テキスト。
	 * @return ハッシュ値。
	 * @throws Exception 例外。
	 */
	public static String getHash(final String algorithm, final String text) throws Exception {
		MessageDigest digest = MessageDigest.getInstance(algorithm);
		byte[] ret = digest.digest(text.getBytes());
		return Hex.encodeHexString(ret);
	}

	/**
	 * ユーザパスワードタイプ。
	 */
	public static enum UserPasswordType {
		/**
		 * 可逆パスワード。
		 */
		REVERSIBLE_PASSWORD,
		/**
		 * 不可逆パスワード。
		 */
		IRREVERSIBLE_PASSWORD
	};

	/**
	 * パスワードタイプ。
	 */
	private static UserPasswordType userPasswordType = UserPasswordType.REVERSIBLE_PASSWORD;

	/**
	 * パスワードタイプを取得します。。
	 * @return パスワードタイプ。
	 */
	public static UserPasswordType getUserPasswordType() {
		return userPasswordType;
	}

	/**
	 * ハッシュアルゴリズム。
	 */
	private static String hashAlgorithm = "SHA-512";

	/**
	 * ハッシュアルゴリズムを取得します。
	 * @return ハッシュアルゴリズム。
	 */
	public static String getHashAlgorithm() {
		return hashAlgorithm;
	}

	/**
	 * パスワードタイプを取得する。
	 * @param context ServletContext。
	 */
	public static void initPasswordType(final ServletContext context) {
		String passwordType = DataFormsServlet.getConf().getApplication().getPasswordType();
		if (passwordType != null) {
			CryptUtil.userPasswordType = UserPasswordType.valueOf(passwordType);
		}
		String hash = DataFormsServlet.getConf().getApplication().getHashAlgorithm();
		if (hash != null) {
			CryptUtil.hashAlgorithm = hash;
		}
		logger.info("passwordType=" + CryptUtil.userPasswordType + ", hashAlgorithm=" + CryptUtil.hashAlgorithm);
	}

	/**
	 * ユーザパスワードを暗号化します。
	 * @param password パスワード。
	 * @return 暗号化パスワード。
	 * @throws Exception 例外。
	 */
	public static String encryptUserPassword(final String password) throws Exception {
		if (CryptUtil.userPasswordType == UserPasswordType.REVERSIBLE_PASSWORD) {
			return CryptUtil.encrypt(password);
		} else {
			return CryptUtil.getHash(CryptUtil.hashAlgorithm, password);
		}
	}

	/**
	 * ユーザパスワードを複合します。
	 * @param password 暗号化されたパスワード。
	 * @return 複合されたパスワード。
	 * @throws Exception 例外。
	 */
	public static String decryptUserPassword(final String password) throws Exception {
		if (CryptUtil.userPasswordType == UserPasswordType.REVERSIBLE_PASSWORD) {
			return CryptUtil.decrypt(password);
		} else {
			return null;
		}
	}

}
