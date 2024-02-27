package jp.dataforms.fw.app.user.field;

import jp.dataforms.fw.app.enumtype.dao.EnumDao;
import jp.dataforms.fw.field.common.EnumTypeSingleSelectField;

/**
 * ユーザ属性フィールドクラス。
 *
 */
public class UserAttributeTypeField extends EnumTypeSingleSelectField {
	/**
	 * コンストラクタ。
	 */
	public UserAttributeTypeField() {
		super(null, "userAttribute");
		this.setComment("ユーザ属性");
		this.setBlankOption(true);
	}


	/**
	 * コンストラクタ。
	 * @param id フィールドID。
	 */
	public UserAttributeTypeField(final String id) {
		super(id, "userAttribute");
		this.setComment("ユーザ属性");
	}

	@Override
	public void init() throws Exception {
		super.init();
		EnumDao dao = new EnumDao(this);
		String lang = this.getPage().getCurrentLanguage();
		this.setOptionList(dao.getTypeList(this.getEnumGroupCode(), lang));
	}

}
