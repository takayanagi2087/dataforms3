package jp.dataforms.fw.app.user.dao;

import java.util.Map;
import jp.dataforms.fw.dao.Table;
import jp.dataforms.fw.app.user.field.WebAuthnIdField;
import jp.dataforms.fw.app.user.field.AttestationObjectField;
import jp.dataforms.fw.util.NumberUtil;
import jp.dataforms.fw.app.user.field.UserIdField;
import jp.dataforms.fw.app.user.field.AuthIdField;
import jp.dataforms.fw.app.user.field.AuthenticatorAttachmentField;
import jp.dataforms.fw.app.user.field.PlatformField;
import jp.dataforms.fw.app.user.field.AuthenticatorNameField;
import jp.dataforms.fw.app.user.field.AuthTypeField;
import jp.dataforms.fw.app.user.field.CollectedClientDataField;


/**
 * WebAuthenテーブルクラス。
 *
 */
public class WebAuthnTable extends Table {
	/**
	 * コンストラクタ。
	 */
	public WebAuthnTable() {
		this.setAutoIncrementId(true);
		this.setComment("WebAuthenテーブル");
		this.addPkField(new WebAuthnIdField()).setNotNull(true); //WebAuth情報のID
		this.addField(new AuthenticatorNameField()); //認証機器の名称
		this.addField(new UserIdField()); //ユーザを示すID。
		this.addField(new PlatformField()); //プラットフォーム
		this.addField(new AuthIdField()); //認証情報ID
		this.addField(new AuthTypeField()); //認証タイプ
		this.addField(new AuthenticatorAttachmentField()); //AuthenticatorAttachment
		this.addField(new AttestationObjectField()); //AttestationObject
		this.addField(new CollectedClientDataField()); //CollectedClientData
		this.addUpdateInfoFields();
	}

	@Override
	public String getJoinCondition(final Table joinTable, final String alias) {
		WebAuthnTableRelation r = new WebAuthnTableRelation(this);
		return r.getJoinCondition(joinTable, alias);
	}

	/**
	 * Entity操作クラスです。
	 */
	public static class Entity extends jp.dataforms.fw.dao.Entity {
		/** WebAuth情報のIDのフィールドID。 */
		public static final String ID_WEB_AUTHN_ID = "webAuthnId";
		/** 認証機器の名称のフィールドID。 */
		public static final String ID_AUTHENTICATOR_NAME = "authenticatorName";
		/** ユーザを示すID。のフィールドID。 */
		public static final String ID_USER_ID = "userId";
		/** プラットフォームのフィールドID。 */
		public static final String ID_PLATFORM = "platform";
		/** 認証情報IDのフィールドID。 */
		public static final String ID_AUTH_ID = "authId";
		/** 認証タイプのフィールドID。 */
		public static final String ID_AUTH_TYPE = "authType";
		/** AuthenticatorAttachmentのフィールドID。 */
		public static final String ID_AUTHENTICATOR_ATTACHMENT = "authenticatorAttachment";
		/** AttestationObjectのフィールドID。 */
		public static final String ID_ATTESTATION_OBJECT = "attestationObject";
		/** CollectedClientDataのフィールドID。 */
		public static final String ID_COLLECTED_CLIENT_DATA = "collectedClientData";

		/**
		 * コンストラクタ。
		 */
		public Entity() {

		}
		/**
		 * コンストラクタ。
		 * @param map 操作対象マップ。
		 */
		public Entity(final Map<String, Object> map) {
			super(map);
		}
		/**
		 * WebAuth情報のIDを取得します。
		 * @return WebAuth情報のID。
		 */
		public java.lang.Long getWebAuthnId() {
			return NumberUtil.longValueObject(this.getMap().get(Entity.ID_WEB_AUTHN_ID));
		}

		/**
		 * WebAuth情報のIDを設定します。
		 * @param webAuthnId WebAuth情報のID。
		 */
		public void setWebAuthnId(final java.lang.Long webAuthnId) {
			this.getMap().put(Entity.ID_WEB_AUTHN_ID, webAuthnId);
		}

		/**
		 * 認証機器の名称を取得します。
		 * @return 認証機器の名称。
		 */
		public java.lang.String getAuthenticatorName() {
			return (java.lang.String) this.getMap().get(Entity.ID_AUTHENTICATOR_NAME);
		}

		/**
		 * 認証機器の名称を設定します。
		 * @param authenticatorName 認証機器の名称。
		 */
		public void setAuthenticatorName(final java.lang.String authenticatorName) {
			this.getMap().put(Entity.ID_AUTHENTICATOR_NAME, authenticatorName);
		}

		/**
		 * ユーザを示すID。を取得します。
		 * @return ユーザを示すID。。
		 */
		public java.lang.Long getUserId() {
			return NumberUtil.longValueObject(this.getMap().get(Entity.ID_USER_ID));
		}

		/**
		 * ユーザを示すID。を設定します。
		 * @param userId ユーザを示すID。。
		 */
		public void setUserId(final java.lang.Long userId) {
			this.getMap().put(Entity.ID_USER_ID, userId);
		}

		/**
		 * プラットフォームを取得します。
		 * @return プラットフォーム。
		 */
		public java.lang.String getPlatform() {
			return (java.lang.String) this.getMap().get(Entity.ID_PLATFORM);
		}

		/**
		 * プラットフォームを設定します。
		 * @param platform プラットフォーム。
		 */
		public void setPlatform(final java.lang.String platform) {
			this.getMap().put(Entity.ID_PLATFORM, platform);
		}

		/**
		 * 認証情報IDを取得します。
		 * @return 認証情報ID。
		 */
		public java.lang.String getAuthId() {
			return (java.lang.String) this.getMap().get(Entity.ID_AUTH_ID);
		}

		/**
		 * 認証情報IDを設定します。
		 * @param authId 認証情報ID。
		 */
		public void setAuthId(final java.lang.String authId) {
			this.getMap().put(Entity.ID_AUTH_ID, authId);
		}

		/**
		 * 認証タイプを取得します。
		 * @return 認証タイプ。
		 */
		public java.lang.String getAuthType() {
			return (java.lang.String) this.getMap().get(Entity.ID_AUTH_TYPE);
		}

		/**
		 * 認証タイプを設定します。
		 * @param authType 認証タイプ。
		 */
		public void setAuthType(final java.lang.String authType) {
			this.getMap().put(Entity.ID_AUTH_TYPE, authType);
		}

		/**
		 * AuthenticatorAttachmentを取得します。
		 * @return AuthenticatorAttachment。
		 */
		public java.lang.String getAuthenticatorAttachment() {
			return (java.lang.String) this.getMap().get(Entity.ID_AUTHENTICATOR_ATTACHMENT);
		}

		/**
		 * AuthenticatorAttachmentを設定します。
		 * @param authenticatorAttachment AuthenticatorAttachment。
		 */
		public void setAuthenticatorAttachment(final java.lang.String authenticatorAttachment) {
			this.getMap().put(Entity.ID_AUTHENTICATOR_ATTACHMENT, authenticatorAttachment);
		}

		/**
		 * AttestationObjectを取得します。
		 * @return AttestationObject。
		 */
		public java.lang.String getAttestationObject() {
			return (java.lang.String) this.getMap().get(Entity.ID_ATTESTATION_OBJECT);
		}

		/**
		 * AttestationObjectを設定します。
		 * @param attestationObject AttestationObject。
		 */
		public void setAttestationObject(final java.lang.String attestationObject) {
			this.getMap().put(Entity.ID_ATTESTATION_OBJECT, attestationObject);
		}

		/**
		 * CollectedClientDataを取得します。
		 * @return CollectedClientData。
		 */
		public java.lang.String getCollectedClientData() {
			return (java.lang.String) this.getMap().get(Entity.ID_COLLECTED_CLIENT_DATA);
		}

		/**
		 * CollectedClientDataを設定します。
		 * @param collectedClientData CollectedClientData。
		 */
		public void setCollectedClientData(final java.lang.String collectedClientData) {
			this.getMap().put(Entity.ID_COLLECTED_CLIENT_DATA, collectedClientData);
		}


	}

	/**
	 * WebAuth情報のIDフィールドを取得します。
	 * @return WebAuth情報のIDフィールド。
	 */
	public WebAuthnIdField getWebAuthnIdField() {
		return (WebAuthnIdField) this.getField(Entity.ID_WEB_AUTHN_ID);
	}

	/**
	 * 認証機器の名称フィールドを取得します。
	 * @return 認証機器の名称フィールド。
	 */
	public AuthenticatorNameField getAuthenticatorNameField() {
		return (AuthenticatorNameField) this.getField(Entity.ID_AUTHENTICATOR_NAME);
	}

	/**
	 * ユーザを示すID。フィールドを取得します。
	 * @return ユーザを示すID。フィールド。
	 */
	public UserIdField getUserIdField() {
		return (UserIdField) this.getField(Entity.ID_USER_ID);
	}

	/**
	 * プラットフォームフィールドを取得します。
	 * @return プラットフォームフィールド。
	 */
	public PlatformField getPlatformField() {
		return (PlatformField) this.getField(Entity.ID_PLATFORM);
	}

	/**
	 * 認証情報IDフィールドを取得します。
	 * @return 認証情報IDフィールド。
	 */
	public AuthIdField getAuthIdField() {
		return (AuthIdField) this.getField(Entity.ID_AUTH_ID);
	}

	/**
	 * 認証タイプフィールドを取得します。
	 * @return 認証タイプフィールド。
	 */
	public AuthTypeField getAuthTypeField() {
		return (AuthTypeField) this.getField(Entity.ID_AUTH_TYPE);
	}

	/**
	 * AuthenticatorAttachmentフィールドを取得します。
	 * @return AuthenticatorAttachmentフィールド。
	 */
	public AuthenticatorAttachmentField getAuthenticatorAttachmentField() {
		return (AuthenticatorAttachmentField) this.getField(Entity.ID_AUTHENTICATOR_ATTACHMENT);
	}

	/**
	 * AttestationObjectフィールドを取得します。
	 * @return AttestationObjectフィールド。
	 */
	public AttestationObjectField getAttestationObjectField() {
		return (AttestationObjectField) this.getField(Entity.ID_ATTESTATION_OBJECT);
	}

	/**
	 * CollectedClientDataフィールドを取得します。
	 * @return CollectedClientDataフィールド。
	 */
	public CollectedClientDataField getCollectedClientDataField() {
		return (CollectedClientDataField) this.getField(Entity.ID_COLLECTED_CLIENT_DATA);
	}



}
