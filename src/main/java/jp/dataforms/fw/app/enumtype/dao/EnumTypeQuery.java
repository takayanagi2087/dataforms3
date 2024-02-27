package jp.dataforms.fw.app.enumtype.dao;

/**
 * 列挙型の問合せ。
 *
 */
public class EnumTypeQuery extends EnumQuery {
	/**
	 * コンストラクタ。
	 */
	public EnumTypeQuery() {
		this.setCondition("e.parent_id is null");
	}

	/**
	 * コンストラクタ。
	 * @param enumCode 列挙型コード。
	 * @param langCode 言語コード。
	 */
	public EnumTypeQuery(final String enumCode, final String langCode) {
		this.setCondition("e.parent_id is null and e.enum_code=:enum_code and en.lang_code=:lang_code");
		EnumTable.Entity e = new EnumTable.Entity();
		e.setEnumCode(enumCode);
		EnumNameTable.Entity en = new EnumNameTable.Entity(e.getMap());
		en.setLangCode(langCode);
		this.setConditionData(e.getMap());
	}
}
