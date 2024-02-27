package jp.dataforms.fw.htmltable;

import java.util.Map;

import jp.dataforms.fw.field.base.Field;
import jp.dataforms.fw.field.base.FieldList;

/**
 * 編集可能なHTMLテーブルクラス。
 * <pre>
 * <a href="../../../jsdoc/EditableHtmlTable.html" target="_blank">jsdocを参照</a>
 * </pre>
 */
public class EditableHtmlTable extends HtmlTable {

	/**
	 * 読み取り専用モード。
	 */
	private boolean readonly = false;

	/**
	 * ソート可能フラグ。
	 */
	private boolean sortable = true;

	/**
	 * Sortableの切り替えチェックボックスを表示するかどうかのフラグ。
	 */
	private boolean sortableSwitching = false;

	/**
	 * コンストラクタ。
	 * @param id テーブルID。
	 */
	public EditableHtmlTable(final String id) {
		super(id);
	}


	/**
	 * コンストラクタ。
	 * @param id テーブルID。
	 * @param flist フィールドリスト。
	 */
	public EditableHtmlTable(final String id, final FieldList flist) {
		super(id, flist);
	}

	/**
	 * コンストラクタ。
	 * @param id ID。
	 * @param flist フィールドリスト。
	 */
	public EditableHtmlTable(final String id, final Field<?>... flist) {
		super(id, flist);
	}

	/**
	 * 読み取り専用フラグを取得します。
	 * @return 読み取り専用フラグ。
	 */
	public boolean isReadonly() {
		return readonly;
	}

	/**
	 * 読み取り専用フラグを設定します。
	 * @param readonly 読み取り専用フラグ。
	 */
	public void setReadonly(final boolean readonly) {
		this.readonly = readonly;
	}


	/**
	 * ソート可能フラグを取得します。
	 * @return ソート可能フラグ。
	 */
	public boolean isSortable() {
		return sortable;
	}


	/**
	 * ソート可能フラグを設定します。
	 * <pre>
	 * trueを指定するとテーブルの行のドラッグで、テーブルのデータ順の変更が可能になります。
	 * </pre>
	 * @param sortable ソート可能フラグ。
	 */
	public void setSortable(final boolean sortable) {
		this.sortable = sortable;
	}

	/**
	 * 行入替チェックボックス表示フラグを返します。
	 *
	 * @return 行入替チェックボックスを表示する場合true。
	 */
	public boolean isSortableSwitching() {
		return sortableSwitching;
	}

	/**
	 * 行入替チェックボックス表示フラグを設定します。
	 * @param sortableSwitching 行入替チェックボックス表示フラグ。
	 */
	public void setSortableSwitching(final boolean sortableSwitching) {
		this.sortableSwitching = sortableSwitching;
	}


	@Override
	public Map<String, Object> getProperties() throws Exception {
		Map<String, Object> ret = super.getProperties();
		ret.put("readonly", this.isReadonly());
		ret.put("sortable", this.isSortable());
		ret.put("sortableSwitching", this.isSortableSwitching());
		return ret;
	}

}
