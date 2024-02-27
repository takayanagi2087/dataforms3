package jp.dataforms.fw.app.enumtype.dao;

import jp.dataforms.fw.field.base.FieldList;

/**
 * 列挙型選択肢の問合せ。
 *
 */
public class EnumOptionQuery extends EnumQuery {
	/**
	 * コンストラクタ。
	 * @param enumId 列挙型ID。
	 * @param langCode 言語コード。
	 */
	public EnumOptionQuery(final Long enumId, final String langCode) {
		super();
		this.setCondition("e.parent_id=" + enumId + " and en.lang_code='" + langCode + "'");
	}

	/**
	 * コンストラクタ。
	 * @param enumCode 列挙型コード。
	 * @param langCode 言語コード。
	 */
	public EnumOptionQuery(final String enumCode, final String langCode) {
		super();
		this.setCondition("e.parent_id in (select enum_id from enum where enum_code='" + enumCode +"') and en.lang_code='" + langCode + "'");
		this.setOrderByFieldList(new FieldList(this.getField(EnumTable.Entity.ID_SORT_ORDER)));
	}


	/**
	 * コンストラクタ。
	 * @param enumCode 列挙型コード。
	 * @param optionCode 選択肢コード。
	 * @param langCode 言語コード。
	 */
	public EnumOptionQuery(final String enumCode, final String optionCode, final String langCode) {
		super();
		this.setCondition("e.enum_code='" + optionCode + "' and e.parent_id in (select enum_id from enum where enum_code='" + enumCode + "') and en.lang_code='" + langCode +"'");
	}

}
