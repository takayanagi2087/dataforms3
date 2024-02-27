package jp.dataforms.fw.app.enumtype.dao;

import java.util.Map;

import jp.dataforms.fw.app.enumtype.field.EnumIdField;
import jp.dataforms.fw.app.enumtype.field.EnumNameField;
import jp.dataforms.fw.dao.Table;
import jp.dataforms.fw.field.common.LangCodeField;


/**
 * 列挙型名称テーブルクラス。
 *
 */
public class EnumNameTable extends Table {
	/**
	 * コンストラクタ。
	 */
	public EnumNameTable() {
		this.setComment("列挙型名称テーブル");
		this.addPkField(new EnumIdField()); //列挙型ID
		this.addPkField(new LangCodeField()); //言語コード
		this.addField(new EnumNameField()); //列挙型名称
		this.addUpdateInfoFields();
	}

	@Override
	public String getJoinCondition(final Table joinTable, final String alias) {
		EnumNameTableRelation r = new EnumNameTableRelation(this);
		return r.getJoinCondition(joinTable, alias);
	}

	/**
	 * Entity操作クラスです。
	 */
	public static class Entity extends jp.dataforms.fw.dao.Entity {
		/** 列挙型IDのフィールドID。 */
		public static final String ID_ENUM_ID = "enumId";
		/** 言語コードのフィールドID。 */
		public static final String ID_LANG_CODE = "langCode";
		/** 列挙型名称のフィールドID。 */
		public static final String ID_ENUM_NAME = "enumName";

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
		 * 言語コードを取得します。
		 * @return 言語コード。
		 */
		public java.lang.String getLangCode() {
			return (java.lang.String) this.getMap().get(Entity.ID_LANG_CODE);
		}

		/**
		 * 言語コードを設定します。
		 * @param langCode 言語コード。
		 */
		public void setLangCode(final java.lang.String langCode) {
			this.getMap().put(Entity.ID_LANG_CODE, langCode);
		}

		/**
		 * 列挙型名称を取得します。
		 * @return 列挙型名称。
		 */
		public java.lang.String getEnumName() {
			return (java.lang.String) this.getMap().get(Entity.ID_ENUM_NAME);
		}

		/**
		 * 列挙型名称を設定します。
		 * @param enumName 列挙型名称。
		 */
		public void setEnumName(final java.lang.String enumName) {
			this.getMap().put(Entity.ID_ENUM_NAME, enumName);
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
	 * 言語コードフィールドを取得します。
	 * @return 言語コードフィールド。
	 */
	public LangCodeField getLangCodeField() {
		return (LangCodeField) this.getField(Entity.ID_LANG_CODE);
	}

	/**
	 * 列挙型名称フィールドを取得します。
	 * @return 列挙型名称フィールド。
	 */
	public EnumNameField getEnumNameField() {
		return (EnumNameField) this.getField(Entity.ID_ENUM_NAME);
	}


}
