package jp.dataforms.fw.app.user.page;

import jp.dataforms.fw.app.base.page.UserPage;
import jp.dataforms.fw.dao.Dao;


/**
 * 多要素認証設定ページクラス。
 */
public class MfaPage extends UserPage {
	/**
	 * コンストラクタ。
	 */
	public MfaPage() {
		this.addForm(new PasswordCheckForm());
		this.addForm(new MfaForm());
	}

	/**
	 * Pageのタイトルを返します。
	 *
	 * @return Pageのタイトル。
	 */
    @Override
	public String getPageName() {
		return "生体情報登録";
	}

	/**
	 * Pageの説明を返します。
	 *
	 * @return Pageの説明。
	 */
    @Override
	public String getPageDescription() {
		return "利用している端末をパスキーとして登録します。";
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
