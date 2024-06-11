package jp.dataforms.fw.devtool.init.page;

import jp.dataforms.fw.app.base.page.BasePage;
import jp.dataforms.fw.dao.Dao;


/**
 * 開発ツール初期化 ページクラス。
 */
public class InitDevelopmentToolPage extends BasePage {
	/**
	 * コンストラクタ。
	 */
	public InitDevelopmentToolPage() {
		this.addForm(new InitDevelopmentToolForm());
	}

	/**
	 * Pageが配置されるパスを返します。
	 *
	 * @return Pageが配置されるパス。
	 */
	public String getFunctionPath() {
		return "/dataforms/app";
	}
	
	/**
	 * Pageのタイトルを返します。
	 *
	 * @return Pageのタイトル。
	 */
    @Override
	public String getPageName() {
		return "開発ツール初期化";
	}

	/**
	 * Pageの説明を返します。
	 *
	 * @return Pageの説明。
	 */
    @Override
	public String getPageDescription() {
		return "開発ツールの初期設定";
	}

	/**
	 * テーブルを操作するためのDaoクラスを取得します。
	 * <pre>
	 * ページjavaクラス作成用のメソッドです。
	 * </pre>
	 * @return Daoクラス。
	 */
	public Class<? extends Dao> getDaoClass() {
		return null;
	}
}
