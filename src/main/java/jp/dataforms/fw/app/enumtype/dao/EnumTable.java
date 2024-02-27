package jp.dataforms.fw.app.enumtype.dao;

import java.util.Map;

import jp.dataforms.fw.app.enumtype.field.EnumCodeField;
import jp.dataforms.fw.app.enumtype.field.EnumGroupCodeField;
import jp.dataforms.fw.app.enumtype.field.EnumIdField;
import jp.dataforms.fw.app.enumtype.field.MemoField;
import jp.dataforms.fw.app.enumtype.field.ParentIdField;
import jp.dataforms.fw.dao.Table;
import jp.dataforms.fw.field.common.SortOrderField;


/**
 * 列挙型テーブルクラス。
 *
 */
public class EnumTable extends Table {
	/**
	 * コンストラクタ。
	 */
	public EnumTable() {
		this.setAutoIncrementId(true);
		this.setComment("列挙型テーブル");
		this.addPkField(new EnumIdField()); //列挙型ID
		this.addField(new ParentIdField()); //親IDフィールド
		this.addField(new SortOrderField()); //ソート順
		this.addField(new EnumCodeField()); //列挙型コード
		this.addField(new EnumGroupCodeField()); //列挙型グループコード.
		this.addField(new MemoField()); //メモ
		this.addUpdateInfoFields();
	}

	@Override
	public String getJoinCondition(final Table joinTable, final String alias) {
		EnumTableRelation r = new EnumTableRelation(this);
		return r.getJoinCondition(joinTable, alias);
	}

	/**
	 * Entity操作クラスです。
	 */
	public static class Entity extends jp.dataforms.fw.dao.Entity {
		/** 列挙型IDのフィールドID。 */
		public static final String ID_ENUM_ID = "enumId";
		/** 親IDフィールドのフィールドID。 */
		public static final String ID_PARENT_ID = "parentId";
		/** ソート順のフィールドID。 */
		public static final String ID_SORT_ORDER = "sortOrder";
		/** 列挙型コードのフィールドID。 */
		public static final String ID_ENUM_CODE = "enumCode";
		/** 列挙型グループコード.のフィールドID。 */
		public static final String ID_ENUM_GROUP_CODE = "enumGroupCode";
		/** メモのフィールドID。 */
		public static final String ID_MEMO = "memo";

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
		 * 列挙型IDを取得します。
		 * @return 列挙型ID。
		 */
		public java.lang.Long getEnumId() {
			return (java.lang.Long) this.getMap().get(Entity.ID_ENUM_ID);
		}

		/**
		 * 列挙型IDを設定します。
		 * @param enumId 列挙型ID。
		 */
		public void setEnumId(final java.lang.Long enumId) {
			this.getMap().put(Entity.ID_ENUM_ID, enumId);
		}

		/**
		 * 親IDフィールドを取得します。
		 * @return 親IDフィールド。
		 */
		public java.lang.Long getParentId() {
			return (java.lang.Long) this.getMap().get(Entity.ID_PARENT_ID);
		}

		/**
		 * 親IDフィールドを設定します。
		 * @param parentId 親IDフィールド。
		 */
		public void setParentId(final java.lang.Long parentId) {
			this.getMap().put(Entity.ID_PARENT_ID, parentId);
		}

		/**
		 * ソート順を取得します。
		 * @return ソート順。
		 */
		public java.lang.Short getSortOrder() {
			return (java.lang.Short) this.getMap().get(Entity.ID_SORT_ORDER);
		}

		/**
		 * ソート順を設定します。
		 * @param sortOrder ソート順。
		 */
		public void setSortOrder(final java.lang.Short sortOrder) {
			this.getMap().put(Entity.ID_SORT_ORDER, sortOrder);
		}

		/**
		 * 列挙型コードを取得します。
		 * @return 列挙型コード。
		 */
		public java.lang.String getEnumCode() {
			return (java.lang.String) this.getMap().get(Entity.ID_ENUM_CODE);
		}

		/**
		 * 列挙型コードを設定します。
		 * @param enumCode 列挙型コード。
		 */
		public void setEnumCode(final java.lang.String enumCode) {
			this.getMap().put(Entity.ID_ENUM_CODE, enumCode);
		}

		/**
		 * 列挙型グループコード.を取得します。
		 * @return 列挙型グループコード.。
		 */
		public java.lang.String getEnumGroupCode() {
			return (java.lang.String) this.getMap().get(Entity.ID_ENUM_GROUP_CODE);
		}

		/**
		 * 列挙型グループコード.を設定します。
		 * @param enumGroupCode 列挙型グループコード.。
		 */
		public void setEnumGroupCode(final java.lang.String enumGroupCode) {
			this.getMap().put(Entity.ID_ENUM_GROUP_CODE, enumGroupCode);
		}

		/**
		 * メモを取得します。
		 * @return メモ。
		 */
		public java.lang.String getMemo() {
			return (java.lang.String) this.getMap().get(Entity.ID_MEMO);
		}

		/**
		 * メモを設定します。
		 * @param memo メモ。
		 */
		public void setMemo(final java.lang.String memo) {
			this.getMap().put(Entity.ID_MEMO, memo);
		}


	}
	/**
	 * 列挙型IDフィールドを取得します。
	 * @return 列挙型IDフィールド。
	 */
	public EnumIdField getEnumIdField() {
		return (EnumIdField) this.getField(Entity.ID_ENUM_ID);
	}

	/**
	 * 親IDフィールドフィールドを取得します。
	 * @return 親IDフィールドフィールド。
	 */
	public ParentIdField getParentIdField() {
		return (ParentIdField) this.getField(Entity.ID_PARENT_ID);
	}

	/**
	 * ソート順フィールドを取得します。
	 * @return ソート順フィールド。
	 */
	public SortOrderField getSortOrderField() {
		return (SortOrderField) this.getField(Entity.ID_SORT_ORDER);
	}

	/**
	 * 列挙型コードフィールドを取得します。
	 * @return 列挙型コードフィールド。
	 */
	public EnumCodeField getEnumCodeField() {
		return (EnumCodeField) this.getField(Entity.ID_ENUM_CODE);
	}

	/**
	 * 列挙型グループコード.フィールドを取得します。
	 * @return 列挙型グループコード.フィールド。
	 */
	public EnumGroupCodeField getEnumGroupCodeField() {
		return (EnumGroupCodeField) this.getField(Entity.ID_ENUM_GROUP_CODE);
	}

	/**
	 * メモフィールドを取得します。
	 * @return メモフィールド。
	 */
	public MemoField getMemoField() {
		return (MemoField) this.getField(Entity.ID_MEMO);
	}


}
