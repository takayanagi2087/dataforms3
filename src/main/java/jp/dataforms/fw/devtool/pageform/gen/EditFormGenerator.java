package jp.dataforms.fw.devtool.pageform.gen;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jp.dataforms.fw.devtool.field.PagePatternSelectField;
import jp.dataforms.fw.devtool.pageform.page.DaoAndPageGeneratorEditForm;
import jp.dataforms.fw.devtool.query.page.SelectFieldHtmlTable;
import jp.dataforms.fw.util.JsonUtil;

/**
 * 検索結果フォームのJavaソースジェネレータ。
 */
public class EditFormGenerator extends FormSrcGenerator {

	/**
	 * 問合せ結果フォームのテンプレートを取得します。
	 */
	@Override
	protected Template getTemplate() throws Exception {
		Template tmp = new Template(this.getClass(), "template/EditForm.java.template");
		return tmp;
	}


	/**
	 * 問合せ結果フォームクラス名を取得します。
	 */
	@Override
	protected String getFormClassName(Map<String, Object> data) {
		String formClassName = (String) data.get(DaoAndPageGeneratorEditForm.ID_EDIT_FORM_CLASS_NAME);
		return formClassName;
	}

	/**
	 * フィールド設定コードを取得します。
	 * @param cls 問合せクラス。
	 * @param conf 問合せクラスのフィールド設定情報。
	 * @return フィールド設定コード。
	 */
	private String getFieldConfig(final String cls, final String conf) {
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> list = (List<Map<String, Object>>) JsonUtil.decode(conf, ArrayList.class);
		StringBuilder sb = new StringBuilder();
		for (Map<String, Object> m: list) {
			if (sb.length() > 0) {
				sb.append("\n");
			}
			String fieldId = (String) m.get(SelectFieldHtmlTable.ID_FIELD_ID);
			String fid = fieldId.substring(0, 1).toUpperCase() + fieldId.substring(1);
			String disp = (String) m.get(SelectFieldHtmlTable.ID_EDIT_FIELD_DISPLAY);
			String fcfg = "\t\tdao.get" + cls + "().get" + fid + "Field().setEditFormDisplay(Display." + disp + ");";
			sb.append(fcfg);
		}
		return sb.toString();
	}

	/**
	 * 複数レコード編集モードのキーフィールドの設定。
	 * @param data POSTされたデータ。
	 * @return フィールド設定コード。
	 */
	private String getMultiRecordFieldConfig(final Map<String, Object> data) {
		StringBuilder sb = new StringBuilder();
		String pagePattern = (String) data.get(DaoAndPageGeneratorEditForm.ID_PAGE_PATTERN);
		String ef = PagePatternSelectField.getEditFormFlag(pagePattern);
		if ("2".equals(ef)) {
			String conf = (String) data.get(DaoAndPageGeneratorEditForm.ID_EDIT_QUERY_CONFIG);
			@SuppressWarnings("unchecked")
			List<Map<String, Object>> list = (List<Map<String, Object>>) JsonUtil.decode(conf, ArrayList.class);
			for (Map<String, Object> m: list) {
				String editKey = (String) m.get(SelectFieldHtmlTable.ID_EDIT_KEY);
				if ("1".equals(editKey)) {
					if (sb.length() > 0) {
						sb.append("\n");
					}
					String fieldId = (String) m.get(SelectFieldHtmlTable.ID_FIELD_ID);
					String fid = fieldId.substring(0, 1).toUpperCase() + fieldId.substring(1);
					String disp = (String) m.get(SelectFieldHtmlTable.ID_EDIT_FIELD_DISPLAY);
					String fcfg = "\t\tdao.get" + fid + "Field().setEditFormDisplay(Display." + disp + ");";
					sb.append(fcfg);
				}
			}
		}
		return sb.toString();
	}

	/**
	 * フィールド設定コードを生成します。
	 * @param data POSTされたデータ。
	 * @return フィールド設定コード。
	 */
	private String getFieldConfig(final Map<String, Object> data) {
		StringBuilder sb = new StringBuilder();
		String cls = (String) data.get(DaoAndPageGeneratorEditForm.ID_EDIT_QUERY_CLASS_NAME);
		String conf = (String) data.get(DaoAndPageGeneratorEditForm.ID_EDIT_QUERY_CONFIG);
		sb.append(this.getMultiRecordFieldConfig(data));
		if (sb.length() > 0) {
			sb.append("\n");
		}
		sb.append(this.getFieldConfig(cls, conf));
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> list = (List<Map<String, Object>>) data.get(DaoAndPageGeneratorEditForm.ID_MULTI_RECORD_QUERY_LIST);
		for (Map<String, Object> m: list) {
			String qcls = (String) m.get(DaoAndPageGeneratorEditForm.ID_QUERY_CLASS_NAME);
			String qconf = (String) m.get(DaoAndPageGeneratorEditForm.ID_QUERY_CONFIG);
			if (sb.length() > 0) {
				sb.append("\n\t\t//\n");
			}
			sb.append(this.getFieldConfig(qcls, qconf));
		}
		return sb.toString();
	}


	@Override
	protected void setFormComponent(Template tmp, String formClassName, Map<String, Object> data) throws Exception {
		tmp.replace(DaoAndPageGeneratorEditForm.ID_EDIT_FORM_CLASS_NAME, formClassName);
		tmp.replace("fieldConfig", this.getFieldConfig(data));
	}

}
