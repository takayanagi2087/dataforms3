package jp.dataforms.fw.field.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jp.dataforms.fw.dao.Entity;
import jp.dataforms.fw.field.base.Field;

/**
 * 選択肢フィールドクラス。
 * <pre>
 * 各種選択肢フィールドクラスの基本クラスです。
 * </pre>
 *
 * @param <TYPE> 取り扱うタイプ。
 */
public abstract class SelectField<TYPE> extends Field<TYPE> {

	/**
	 * 選択肢のエンティティクラス。
	 *
	 */
	public static class OptionEntity extends Entity {
		/**
		 * 名前。
		 */
		public static final String ID_NAME = "name";
		/**
		 * 値。
		 */
		public static final String ID_VALUE = "value";

		/**
		 * コンストラクタ。
		 */
		public OptionEntity() {

		}

		/**
		 * コンストラクタ。
		 * @param m 編集対象マップ。
		 */
		public OptionEntity(final Map<String, Object> m) {
			super(m);
		}

		/**
		 * 値を取得します。
		 * @return 値。
		 */
		public Object getValue() {
			return (Object) this.getMap().get(ID_VALUE);
		}

		/**
		 * 値を設定します。
		 * @param value 値。
		 */
		public void setValue(final String value) {
			this.getMap().put(ID_VALUE, value);
		}

		/**
		 * 名称を設定します。
		 * @return 名称。
		 */
		public String getName() {
			return (String) this.getMap().get(ID_NAME);
		}

		/**
		 * 名称を設定します。
		 * @param name 名称。
		 */
		public void setName(final String name) {
			this.getMap().put(ID_NAME, name);
		}
	}

	/**
	 * 選択肢。
	 */
	private List<Map<String, Object>> optionList = null;

	/**
	 * 空白選択肢追加フラグ。
	 */
	private boolean blankOption = false;

	/**
	 * 空白選択肢追加フラグを取得します。
	 * @return 空白選択肢追加フラグ。
	 */
	public boolean isBlankOption() {
		return blankOption;
	}

	/**
	 * 空白選択肢追加フラグを設定します。
	 * @param blankOption 空白選択肢追加フラグ。
	 */
	public void setBlankOption(final boolean blankOption) {
		this.blankOption = blankOption;
	}

	/**
	 * コンストラクタ。
	 * @param id フィールドID。
	 */
	public SelectField(final String id) {
		super(id);
	}

	/**
	 * コンストラクタ。
	 * @param id フィールドID。
	 * @param len 長さ。
	 */
	public SelectField(final String id, final int len) {
		super(id);
		this.setLength(len);
	}

	/**
	 * 選択肢のリストを取得します。
	 * @return 選択肢のリスト。
	 */
	public List<Map<String, Object>> getOptionList() {
		return optionList;
	}

	/**
	 * 選択肢のリストを設定します。
	 * @param optionList 選択肢のリスト。
	 * @return 設定したフィールド。
	 */
	public SelectField<TYPE> setOptionList(final List<Map<String, Object>> optionList) {
		this.optionList = optionList;
		return this;
	}

	@Override
	public Map<String, Object> getProperties() throws Exception {
		Map<String, Object> ret = super.getProperties();
		ArrayList<Map<String, Object>> olist = null;
		if (this.optionList != null) {
			olist = new ArrayList<Map<String, Object>>();
			olist.addAll(this.optionList);
		}
		ret.put("optionList", olist);
		ret.put("blankOption", this.blankOption);
		return ret;
	}


}
