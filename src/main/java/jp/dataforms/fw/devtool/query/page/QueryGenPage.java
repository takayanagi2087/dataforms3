package jp.dataforms.fw.devtool.query.page;

import jp.dataforms.fw.dao.Dao;
import jp.dataforms.fw.dao.Table;
import jp.dataforms.fw.devtool.base.page.SrcGenPage;
import jp.dataforms.fw.devtool.query.dialog.DateFunctionFieldDialog;


/**
 * ページクラス。
 */
public class QueryGenPage extends SrcGenPage {
	/**
	 * コンストラクタ。
	 */
	public QueryGenPage() {
		this.addForm(new QueryGeneratorQueryForm());
		this.addForm(new QueryGeneratorQueryResultForm());
		this.addForm(new QueryGenEditForm());
		this.addDialog(new DateFunctionFieldDialog());
	}

	/**
	 * Pageが配置されるパスを返します。
	 *
	 * @return Pageが配置されるパス。
	 */
	public String getFunctionPath() {
		return "/dataforms/devtool";
	}

	/**
	 * 操作対象テーブルクラスを取得します。
	 * <pre>
	 * ページjavaクラス作成で用のメソッドです。
	 * </pre>
	 * @return 操作対象テーブル。
	 */
	public Class<? extends  Table> getTableClass() {
		return Table.class;
	}

	/**
	 * テーブルを操作するためのDaoクラスを取得します。
	 * <pre>
	 * ページjavaクラス作成で用のメソッドです。
	 * </pre>
	 * @return Daoクラス。
	 */
	public Class<? extends Dao> getDaoClass() {
		return Dao.class;
	}

}
