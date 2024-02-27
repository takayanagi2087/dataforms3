package jp.dataforms.fw.app.top.page;

import java.util.Map;

import jp.dataforms.fw.annotation.WebMethod;
import jp.dataforms.fw.app.base.page.BasePage;
import jp.dataforms.fw.devtool.db.dao.TableManagerDao;
import jp.dataforms.fw.response.RedirectResponse;
import jp.dataforms.fw.response.Response;

/**
 * トップページクラス。
 * <pre>
 * アプリケーション、ユーザの状態に応じて、適切なページにリダイレクトします。
 * </pre>
 *
 */
public class TopPage extends BasePage {

    /**
     * Logger.
     */
//    private static Logger log = Logger.getLogger(TopPage.class.getName());
	/**
	 * コンストラクタ。
	 */
	public TopPage() {
		this.setMenuItem(false);
		//this.addForm(new LoginForm());
	}


	/**
	 * {@inheritDoc}
	 * <pre>
	 * ページは表示せず、状態に応じてリダイレクションを行ないます。
	 * DBの初期化前	... DB初期化ページ。
	 * ログイン前 ... ログインページ。
	 * ログイン後 ... サイトマップページ。
	 * </pre>
	 */
	@WebMethod
	@Override
	public Response getHtml(final Map<String, Object> params) throws Exception {
		String context = this.getRequest().getContextPath();
		TableManagerDao dao = new TableManagerDao(this);
		if (dao.isDatabaseInitialized()) {
			if (this.getUserInfo() == null) {
				return new RedirectResponse(context + "/dataforms/app/login/page/LoginPage." + this.getPageExt());
			} else {
				return new RedirectResponse(context + "/dataforms/app/menu/page/SiteMapPage." + this.getPageExt());
			}
		} else {
			return new RedirectResponse(context + "/dataforms/devtool/db/page/InitializeDatabasePage." + this.getPageExt());
		}
	}
}
