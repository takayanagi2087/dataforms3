package jp.dataforms.fw.devtool.field;

import java.util.List;
import java.util.Map;

import org.apache.poi.util.StringUtil;

import jp.dataforms.fw.controller.Page;
import jp.dataforms.fw.field.common.PropertiesSingleSelectField;
import jp.dataforms.fw.field.common.SelectField;
import jp.dataforms.fw.util.MessagesUtil;

/**
 * ページパターン選択フィールド。
 */
public class PagePatternSelectField extends PropertiesSingleSelectField {
	/**
	 * プロパティのキー。
	 */
	private static final String KEY = "pagepattern";

	/**
	 * コンストラクタ。
	 * @param id フィールドID。
	 */
	public PagePatternSelectField(final String id) {
		super(id, 10, KEY);
	}

	/**
	 * ページパターンの値を取得します。
	 * @param page ページ。
	 * @param qf QueryFormフラグ。
	 * @param qrf QueryResultFormフラグ。
	 * @param ef EditFormフラグ。
	 * @return ページパターン。
	 * @throws Exception 例外。
	 */
	public static String getPagePattern(final Page page, final String qf, final String qrf, final String ef) throws Exception  {
		String ret = null;
		List<Map<String, Object>> options = MessagesUtil.getSelectFieldOption(page, KEY);
		for (Map<String, Object> m : options) {
			SelectField.OptionEntity e = new SelectField.OptionEntity(m);
			String v = e.getValue();
			if (v.length() == 5) {
				if (qf.charAt(0) == v.charAt(2)
					&& qrf.charAt(0) == v.charAt(3)
					&& ef.charAt(0) == v.charAt(4)) {
					ret = v;
					break;
				}
			}
		}
		return ret;
	}

	/**
	 * フォームフラグを取得します。
	 * @param val 値。
	 * @param idx フラグのインデックス。
	 * @return フォームフラグ。
	 */
	private static String getFormFlag(final String val, final int idx) {
		String ret = null;
		if (!StringUtil.isBlank(val)) {
			if (val.length() >= 5) {
				ret = val.substring(idx, idx + 1);
			}
		}
		return ret;
	}

	/**
	 * 問合せフォームフラグを取得します。
	 * @param val 値。
	 * @return 問合せフォームフラグ。
	 */
	public static String getQueryFormFlag(final String val) {
		return PagePatternSelectField.getFormFlag(val, 2);
	}


	/**
	 * 問合せ結果フォームフラグを取得します。
	 * @param val 値。
	 * @return 問合結果せフォームフラグ。
	 */
	public static String getQueryResultFormFlag(final String val) {
		return PagePatternSelectField.getFormFlag(val, 3);
	}

	/**
	 * 編集フォームフラグを取得します。
	 * @param val 値。
	 * @return 編集フォームフラグ。
	 */
	public static String getEditFormFlag(final String val) {
		return PagePatternSelectField.getFormFlag(val, 4);
	}

}
