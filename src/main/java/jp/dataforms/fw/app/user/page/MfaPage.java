package jp.dataforms.fw.app.user.page;

import jp.dataforms.fw.app.base.page.UserPage;
import jp.dataforms.fw.dao.Dao;


/**
 * 多要素認証設定ページクラス。
 */
public class MfaPage extends UserPage {

	/**
	 * パスワードチェックOKの時に実行するスクリプト。
	 */
	private static final String ON_OK_SCRIPT = """
		this.parent.get("mfaForm").show();
		let mfaForm = this.parent.getComponent("mfaForm");
		mfaForm.getMfaInfo();
		this.get().hide();
	""";
	
	/**
	 * コンストラクタ。
	 */
	public MfaPage() {
		this.addForm(new PasswordCheckForm(ON_OK_SCRIPT));
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
