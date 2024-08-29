package jp.dataforms.fw.app.user.dao;

import java.util.Map;
import jp.dataforms.fw.dao.Table;
import jp.dataforms.fw.util.NumberUtil;
import jp.dataforms.fw.app.user.field.UserIdField;
import jp.dataforms.fw.app.user.field.RecoveryCodeIdField;
import jp.dataforms.fw.app.user.field.RecoveryCodeField;


/**
 * クラス。
 *
 */
public class RecoveryCodeTable extends Table {
	/**
	 * コンストラクタ。
	 */
	public RecoveryCodeTable() {
		this.setAutoIncrementId(true);
		this.setComment("");
		this.addPkField(new RecoveryCodeIdField()).setNotNull(true); //リカバリーコードID
		this.addField(new UserIdField()).setNotNull(true); //ユーザを示すID
		this.addField(new RecoveryCodeField()).setNotNull(true); //リカバリーコード
		this.addUpdateInfoFields();
	}

	@Override
	public String getJoinCondition(final Table joinTable, final String alias) {
		RecoveryCodeTableRelation r = new RecoveryCodeTableRelation(this);
		return r.getJoinCondition(joinTable, alias);
	}

	/**
	 * Entity操作クラスです。
	 */
	public static class Entity extends jp.dataforms.fw.dao.Entity {
		/** リカバリーコードIDのフィールドID。 */
		public static final String ID_RECOVERY_CODE_ID = "recoveryCodeId";
		/** ユーザを示すIDのフィールドID。 */
		public static final String ID_USER_ID = "userId";
		/** リカバリーコードのフィールドID。 */
		public static final String ID_RECOVERY_CODE = "recoveryCode";

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
		 * リカバリーコードIDを取得します。
		 * @return リカバリーコードID。
		 */
		public java.lang.Long getRecoveryCodeId() {
			return NumberUtil.longValueObject(this.getMap().get(Entity.ID_RECOVERY_CODE_ID));
		}

		/**
		 * リカバリーコードIDを設定します。
		 * @param recoveryCodeId リカバリーコードID。
		 */
		public void setRecoveryCodeId(final java.lang.Long recoveryCodeId) {
			this.getMap().put(Entity.ID_RECOVERY_CODE_ID, recoveryCodeId);
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
		 * リカバリーコードを取得します。
		 * @return リカバリーコード。
		 */
		public java.lang.String getRecoveryCode() {
			return (java.lang.String) this.getMap().get(Entity.ID_RECOVERY_CODE);
		}

		/**
		 * リカバリーコードを設定します。
		 * @param recoveryCode リカバリーコード。
		 */
		public void setRecoveryCode(final java.lang.String recoveryCode) {
			this.getMap().put(Entity.ID_RECOVERY_CODE, recoveryCode);
		}


	}

	/**
	 * リカバリーコードIDフィールドを取得します。
	 * @return リカバリーコードIDフィールド。
	 */
	public RecoveryCodeIdField getRecoveryCodeIdField() {
		return (RecoveryCodeIdField) this.getField(Entity.ID_RECOVERY_CODE_ID);
	}

	/**
	 * ユーザを示すIDフィールドを取得します。
	 * @return ユーザを示すIDフィールド。
	 */
	public UserIdField getUserIdField() {
		return (UserIdField) this.getField(Entity.ID_USER_ID);
	}

	/**
	 * リカバリーコードフィールドを取得します。
	 * @return リカバリーコードフィールド。
	 */
	public RecoveryCodeField getRecoveryCodeField() {
		return (RecoveryCodeField) this.getField(Entity.ID_RECOVERY_CODE);
	}



}
