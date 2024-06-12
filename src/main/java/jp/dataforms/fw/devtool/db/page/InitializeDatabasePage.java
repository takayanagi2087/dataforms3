package jp.dataforms.fw.devtool.db.page;

import java.util.Map;

import jp.dataforms.fw.devtool.base.page.DeveloperPage;
import jp.dataforms.fw.devtool.db.dao.TableManagerDao;


/**
 * データベース初期化ページクラス。
 * <pre>
 * 初期状態のデータベースに接続した状態でアクセスすると、このページが表示されます。
 * 動作するため必要な最小限のテーブルを作成し開発者ユーザを登録するためのフォームです。
 * </pre>
 */
public class InitializeDatabasePage extends DeveloperPage {
	/**
	 * コンストラクタ.
	 */
	public InitializeDatabasePage() {
		this.addForm(new DatabaseInfoForm());
		this.addForm(new DeveloperEditForm());
		this.setMenuItem(false);
	}

	/**
	 * {@inheritDoc}
	 * DBが初期化されている場合は表示不可のページ。
	 */
	@Override
	public boolean isAuthenticated(final Map<String, Object> params) throws Exception {
		if (this.getConnection() != null) {
			TableManagerDao dao = new TableManagerDao(this);
			if (dao.isDatabaseInitialized()) {
				//return super.isAuthenticated(params);
				return false;
			} else {
				return true;
			}
		} else {
			// DB未接続時は判定できないのでtrueを返しておかないと動作しない。
			return true;
		}
	}
}
