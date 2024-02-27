package jp.dataforms.fw.app.enumtype.page;

import java.util.Map;

import jp.dataforms.fw.app.enumtype.dao.EnumDao;
import jp.dataforms.fw.app.enumtype.dao.EnumTable;
import jp.dataforms.fw.controller.Page;
import jp.dataforms.fw.controller.QueryResultForm;
import jp.dataforms.fw.field.base.Field.SortOrder;
import jp.dataforms.fw.field.base.FieldList;
import jp.dataforms.fw.htmltable.PageScrollHtmlTable;


/**
 * 問い合わせ結果フォームクラス。
 */
public class EnumQueryResultForm extends QueryResultForm {
	/**
	 * コンストラクタ。
	 */
	public EnumQueryResultForm() {
		EnumTable table = new EnumTable();
		this.addPkFieldList(table.getPkFieldList());
		PageScrollHtmlTable htmltable = new PageScrollHtmlTable(Page.ID_QUERY_RESULT, EnumDao.getQueryResultFieldList());
		htmltable.getFieldList().get(EnumTable.Entity.ID_ENUM_GROUP_CODE).setSortable(true, SortOrder.ASC);
		htmltable.getFieldList().get(EnumTable.Entity.ID_ENUM_CODE).setSortable(true);
		htmltable.getFieldList().get(EnumTable.Entity.ID_MEMO).setSortable(true);

		this.addHtmlTable(htmltable);
	}

	/**
	 * 問い合わせを行い、1ページ分の問い合わせ結果を返します。
	 *
	 * @param data 問い合わせフォームの入力データ。
	 * @param queryFormFieldList 問い合わせフォームのフィールドリスト。
	 * @return 問い合わせ結果。
	 *
	 */
	@Override
	protected Map<String, Object> queryPage(final Map<String, Object> data, final FieldList queryFormFieldList) throws Exception {
		EnumDao dao = new EnumDao(this);
		return dao.queryPage(data, queryFormFieldList);
	}

	/**
	 * 問い合わせ結果リストのデータを削除します。
	 * <pre>
	 * 問い合わせ結果リストからの削除が不要な場合、HTMLから削除ボタンを削除し、
	 * このメソッドを何もしないメソッドにしてください。
	 * </pre>
	 * @param data 選択したデータのPKの値を記録したマップ。
	 */
	@Override
	protected void deleteData(final Map<String, Object> data) throws Exception {
		EnumDao dao = new EnumDao(this);
		this.setUserInfo(data); // 更新を行うユーザIDを設定する.
		dao.delete(data);
	}
}
