package jp.dataforms.fw.app.user.dao;

import java.util.Map;
import jp.dataforms.fw.dao.Table;
import jp.dataforms.fw.field.common.DeleteFlagField;
import jp.dataforms.fw.app.user.field.MfaRequiredFlagField;
import jp.dataforms.fw.app.user.field.TotpSecretField;
import jp.dataforms.fw.util.NumberUtil;
import jp.dataforms.fw.app.user.field.LoginIdField;
import jp.dataforms.fw.app.user.field.UserIdField;
import jp.dataforms.fw.app.user.field.UserNameField;
import jp.dataforms.fw.app.user.field.PasswordRequiredFlagField;
import jp.dataforms.fw.app.user.field.PasswordField;
import jp.dataforms.fw.app.user.field.MailAddressField;
import jp.dataforms.fw.app.user.field.ExternalUserFlagField;
import jp.dataforms.fw.app.user.field.EnabledFlagField;


/**
 * ユーザ情報テーブルクラス。
 *
 */
public class UserInfoTable extends Table {
	/**
	 * コンストラクタ。
	 */
	public UserInfoTable() {
		this.setAutoIncrementId(true);
		this.setComment("ユーザ情報テーブル");
		this.addPkField(new UserIdField()).setNotNull(true); //ユーザを示すID
		this.addField(new LoginIdField()); //ログインID
		this.addField(new PasswordField()); //パスワード
		this.addField(new UserNameField()); //氏名
		this.addField(new MailAddressField()); //メールアドレス
		this.addField(new ExternalUserFlagField()); //外部ユーザフラグ
		this.addField(new EnabledFlagField()); //ユーザ有効フラグ
		this.addField(new PasswordRequiredFlagField()); //パスワード必須フラグ
		this.addField(new MfaRequiredFlagField()); //PassKey必須フラグ
		this.addField(new TotpSecretField()); //TOTPのSecret
		this.addField(new DeleteFlagField()); //削除フラグ
		this.addUpdateInfoFields();
	}

	@Override
	public String getJoinCondition(final Table joinTable, final String alias) {
		UserInfoTableRelation r = new UserInfoTableRelation(this);
		return r.getJoinCondition(joinTable, alias);
	}

	/**
	 * Entity操作クラスです。
	 */
	public static class Entity extends jp.dataforms.fw.dao.Entity {
		/** ユーザを示すIDのフィールドID。 */
		public static final String ID_USER_ID = "userId";
		/** ログインIDのフィールドID。 */
		public static final String ID_LOGIN_ID = "loginId";
		/** パスワードのフィールドID。 */
		public static final String ID_PASSWORD = "password";
		/** 氏名のフィールドID。 */
		public static final String ID_USER_NAME = "userName";
		/** メールアドレスのフィールドID。 */
		public static final String ID_MAIL_ADDRESS = "mailAddress";
		/** 外部ユーザフラグのフィールドID。 */
		public static final String ID_EXTERNAL_USER_FLAG = "externalUserFlag";
		/** ユーザ有効フラグのフィールドID。 */
		public static final String ID_ENABLED_FLAG = "enabledFlag";
		/** パスワード必須フラグのフィールドID。 */
		public static final String ID_PASSWORD_REQUIRED_FLAG = "passwordRequiredFlag";
		/** PassKey必須フラグのフィールドID。 */
		public static final String ID_MFA_REQUIRED_FLAG = "mfaRequiredFlag";
		/** TOTPのSecretのフィールドID。 */
		public static final String ID_TOTP_SECRET = "totpSecret";
		/** 削除フラグのフィールドID。 */
		public static final String ID_DELETE_FLAG = "deleteFlag";

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
		 * ユーザを示すIDを取得します。
		 * @return ユーザを示すID。
		 */
		public java.lang.Long getUserId() {
			return NumberUtil.longValueObject(this.getMap().get(Entity.ID_USER_ID));
		}

		/**
		 * ユーザを示すIDを設定します。
		 * @param userId ユーザを示すID。
		 */
		public void setUserId(final java.lang.Long userId) {
			this.getMap().put(Entity.ID_USER_ID, userId);
		}

		/**
		 * ログインIDを取得します。
		 * @return ログインID。
		 */
		public java.lang.String getLoginId() {
			return (java.lang.String) this.getMap().get(Entity.ID_LOGIN_ID);
		}

		/**
		 * ログインIDを設定します。
		 * @param loginId ログインID。
		 */
		public void setLoginId(final java.lang.String loginId) {
			this.getMap().put(Entity.ID_LOGIN_ID, loginId);
		}

		/**
		 * パスワードを取得します。
		 * @return パスワード。
		 */
		public java.lang.String getPassword() {
			return (java.lang.String) this.getMap().get(Entity.ID_PASSWORD);
		}

		/**
		 * パスワードを設定します。
		 * @param password パスワード。
		 */
		public void setPassword(final java.lang.String password) {
			this.getMap().put(Entity.ID_PASSWORD, password);
		}

		/**
		 * 氏名を取得します。
		 * @return 氏名。
		 */
		public java.lang.String getUserName() {
			return (java.lang.String) this.getMap().get(Entity.ID_USER_NAME);
		}

		/**
		 * 氏名を設定します。
		 * @param userName 氏名。
		 */
		public void setUserName(final java.lang.String userName) {
			this.getMap().put(Entity.ID_USER_NAME, userName);
		}

		/**
		 * メールアドレスを取得します。
		 * @return メールアドレス。
		 */
		public java.lang.String getMailAddress() {
			return (java.lang.String) this.getMap().get(Entity.ID_MAIL_ADDRESS);
		}

		/**
		 * メールアドレスを設定します。
		 * @param mailAddress メールアドレス。
		 */
		public void setMailAddress(final java.lang.String mailAddress) {
			this.getMap().put(Entity.ID_MAIL_ADDRESS, mailAddress);
		}

		/**
		 * 外部ユーザフラグを取得します。
		 * @return 外部ユーザフラグ。
		 */
		public java.lang.String getExternalUserFlag() {
			return (java.lang.String) this.getMap().get(Entity.ID_EXTERNAL_USER_FLAG);
		}

		/**
		 * 外部ユーザフラグを設定します。
		 * @param externalUserFlag 外部ユーザフラグ。
		 */
		public void setExternalUserFlag(final java.lang.String externalUserFlag) {
			this.getMap().put(Entity.ID_EXTERNAL_USER_FLAG, externalUserFlag);
		}

		/**
		 * ユーザ有効フラグを取得します。
		 * @return ユーザ有効フラグ。
		 */
		public java.lang.String getEnabledFlag() {
			return (java.lang.String) this.getMap().get(Entity.ID_ENABLED_FLAG);
		}

		/**
		 * ユーザ有効フラグを設定します。
		 * @param enabledFlag ユーザ有効フラグ。
		 */
		public void setEnabledFlag(final java.lang.String enabledFlag) {
			this.getMap().put(Entity.ID_ENABLED_FLAG, enabledFlag);
		}

		/**
		 * パスワード必須フラグを取得します。
		 * @return パスワード必須フラグ。
		 */
		public java.lang.String getPasswordRequiredFlag() {
			return (java.lang.String) this.getMap().get(Entity.ID_PASSWORD_REQUIRED_FLAG);
		}

		/**
		 * パスワード必須フラグを設定します。
		 * @param passwordRequiredFlag パスワード必須フラグ。
		 */
		public void setPasswordRequiredFlag(final java.lang.String passwordRequiredFlag) {
			this.getMap().put(Entity.ID_PASSWORD_REQUIRED_FLAG, passwordRequiredFlag);
		}

		/**
		 * PassKey必須フラグを取得します。
		 * @return PassKey必須フラグ。
		 */
		public java.lang.String getMfaRequiredFlag() {
			return (java.lang.String) this.getMap().get(Entity.ID_MFA_REQUIRED_FLAG);
		}

		/**
		 * PassKey必須フラグを設定します。
		 * @param mfaRequiredFlag PassKey必須フラグ。
		 */
		public void setMfaRequiredFlag(final java.lang.String mfaRequiredFlag) {
			this.getMap().put(Entity.ID_MFA_REQUIRED_FLAG, mfaRequiredFlag);
		}

		/**
		 * TOTPのSecretを取得します。
		 * @return TOTPのSecret。
		 */
		public java.lang.String getTotpSecret() {
			return (java.lang.String) this.getMap().get(Entity.ID_TOTP_SECRET);
		}

		/**
		 * TOTPのSecretを設定します。
		 * @param totpSecret TOTPのSecret。
		 */
		public void setTotpSecret(final java.lang.String totpSecret) {
			this.getMap().put(Entity.ID_TOTP_SECRET, totpSecret);
		}

		/**
		 * 削除フラグを取得します。
		 * @return 削除フラグ。
		 */
		public java.lang.String getDeleteFlag() {
			return (java.lang.String) this.getMap().get(Entity.ID_DELETE_FLAG);
		}

		/**
		 * 削除フラグを設定します。
		 * @param deleteFlag 削除フラグ。
		 */
		public void setDeleteFlag(final java.lang.String deleteFlag) {
			this.getMap().put(Entity.ID_DELETE_FLAG, deleteFlag);
		}


	}

	/**
	 * ユーザを示すIDフィールドを取得します。
	 * @return ユーザを示すIDフィールド。
	 */
	public UserIdField getUserIdField() {
		return (UserIdField) this.getField(Entity.ID_USER_ID);
	}

	/**
	 * ログインIDフィールドを取得します。
	 * @return ログインIDフィールド。
	 */
	public LoginIdField getLoginIdField() {
		return (LoginIdField) this.getField(Entity.ID_LOGIN_ID);
	}

	/**
	 * パスワードフィールドを取得します。
	 * @return パスワードフィールド。
	 */
	public PasswordField getPasswordField() {
		return (PasswordField) this.getField(Entity.ID_PASSWORD);
	}

	/**
	 * 氏名フィールドを取得します。
	 * @return 氏名フィールド。
	 */
	public UserNameField getUserNameField() {
		return (UserNameField) this.getField(Entity.ID_USER_NAME);
	}

	/**
	 * メールアドレスフィールドを取得します。
	 * @return メールアドレスフィールド。
	 */
	public MailAddressField getMailAddressField() {
		return (MailAddressField) this.getField(Entity.ID_MAIL_ADDRESS);
	}

	/**
	 * 外部ユーザフラグフィールドを取得します。
	 * @return 外部ユーザフラグフィールド。
	 */
	public ExternalUserFlagField getExternalUserFlagField() {
		return (ExternalUserFlagField) this.getField(Entity.ID_EXTERNAL_USER_FLAG);
	}

	/**
	 * ユーザ有効フラグフィールドを取得します。
	 * @return ユーザ有効フラグフィールド。
	 */
	public EnabledFlagField getEnabledFlagField() {
		return (EnabledFlagField) this.getField(Entity.ID_ENABLED_FLAG);
	}

	/**
	 * パスワード必須フラグフィールドを取得します。
	 * @return パスワード必須フラグフィールド。
	 */
	public PasswordRequiredFlagField getPasswordRequiredFlagField() {
		return (PasswordRequiredFlagField) this.getField(Entity.ID_PASSWORD_REQUIRED_FLAG);
	}

	/**
	 * PassKey必須フラグフィールドを取得します。
	 * @return PassKey必須フラグフィールド。
	 */
	public MfaRequiredFlagField getMfaRequiredFlagField() {
		return (MfaRequiredFlagField) this.getField(Entity.ID_MFA_REQUIRED_FLAG);
	}

	/**
	 * TOTPのSecretフィールドを取得します。
	 * @return TOTPのSecretフィールド。
	 */
	public TotpSecretField getTotpSecretField() {
		return (TotpSecretField) this.getField(Entity.ID_TOTP_SECRET);
	}

	/**
	 * 削除フラグフィールドを取得します。
	 * @return 削除フラグフィールド。
	 */
	public DeleteFlagField getDeleteFlagField() {
		return (DeleteFlagField) this.getField(Entity.ID_DELETE_FLAG);
	}



}
