package jp.dataforms.fw.app.enumtype.page;

import java.util.Map;

import jp.dataforms.fw.app.enumtype.dao.EnumDao;
import jp.dataforms.fw.app.enumtype.dao.EnumTable;
import jp.dataforms.fw.controller.DataForms;

/**
 * 特定の列挙型の選択肢を編集するフォーム。
 *
 */
public class EnumMasterEditForm extends EnumEditForm {

	/**
	 * 列挙型コード。
	 */
	private String enumCode = null;

	/**
	 * 多言語対応。
	 */
	private Boolean multiLanguage = false;

	/**
	 * コンストラクタ。
	 * @param enumCode 列挙型コード。
	 */
	public EnumMasterEditForm(final String enumCode) {
		this(DataForms.ID_EDIT_FORM, enumCode, false);
	}

	/**
	 * コンストラクタ。
	 * @param enumCode 列挙型コード。
	 * @param multiLang 多言語フラグ。
	 */
	public EnumMasterEditForm(final String enumCode,  final boolean multiLang) {
		this(DataForms.ID_EDIT_FORM, enumCode, multiLang);
	}

	/**
	 * コンストラクタ。
	 * @param id フォームID。
	 * @param enumCode 列挙型コード。
	 * @param multiLang 多言語フラグ。
	 */
	public EnumMasterEditForm(final String id, final String enumCode, final boolean multiLang) {
		super(id);
		this.enumCode = enumCode;
		this.multiLanguage = multiLang;
	}


	@Override
	protected Class<?> getHtmlFormClass() {
		return EnumEditForm.class;
	}

	/**
	 * 列挙型タイプコードを取得します。
	 * @return 列挙型タイプコード。
	 */
	public String getEnumCode() {
		return enumCode;
	}

	/**
	 * 列挙型タイプコードを設定します。
	 * @param enumCode 列挙型タイプコード。
	 */
	public void setEnumCode(final String enumCode) {
		this.enumCode = enumCode;
	}

	@Override
	public void init() throws Exception {
		super.init();
		EnumDao dao = new EnumDao(this);
		Long enumId = dao.queryEnumId(this.enumCode);
		EnumTable.Entity e = new EnumTable.Entity();
		e.setEnumId(enumId);
		Map<String, Object> data = this.queryData(e.getMap());
		this.setFormDataMap(data);
	}

	@Override
	public Map<String, Object> getProperties() throws Exception {
		Map<String, Object> prop = super.getProperties();
		prop.put("multiLanguage", this.multiLanguage);
		return prop;
	}
}
