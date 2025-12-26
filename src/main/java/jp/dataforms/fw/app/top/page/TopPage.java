package jp.dataforms.fw.app.top.page;

import java.util.Map;

import jp.dataforms.fw.annotation.WebMethod;
import jp.dataforms.fw.app.base.page.BasePage;
import jp.dataforms.fw.devtool.db.dao.TableManagerDao;
import jp.dataforms.fw.response.RedirectResponse;
import jp.dataforms.fw.response.Response;
import jp.dataforms.fw.servlet.DataFormsServlet;

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
	 * SAMLモジュールの存在チェック。
	 * 
	 * @return SAMLモジュールが存在する場合true。
	 */
	protected boolean existsSamlModule() {
		boolean ret = false;
		try {
			Class<?> pageClass = Class.forName("jp.dataforms.fw.saml.page.SamlLoginPage");
			if (pageClass != null) {
				ret = true;
			}
		} catch (Exception ex) {
			;
		}
		return ret;
	}

	/**
	 * SAMLログインすべきかどうかを鑑定します。
	 * <pre>
	 * デフォルトではSAMLモジュールをリンクした場合SAMLログインを使用するようになっています。
	 * 条件によってSAMLログインを使用する場合、このメソッドをオーバーライドしてください。
	 * </pre>
	 * @return SAMLログインすべき場合true。
	 */
	protected boolean isSamlLogin() {
		return this.existsSamlModule();
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
		Boolean init = DataFormsServlet.getConf().getDevelopmentTool().getInitialized();
		if (!init) {
			// プロジェクト初期化前の場合、プロジェクト初期化ページを表示。
			return new RedirectResponse(context + "/dataforms/devtool/init/page/InitDevelopmentToolPage." + this.getPageExt());
		}
		TableManagerDao dao = new TableManagerDao(this);
		if (dao.isDatabaseInitialized()) {
			// データベースの初期化済。
			if (this.getUserInfo() == null) {
				return toLoginPage(context);
			} else {
				// ログイン済の場合サイトマップを表示。
				return new RedirectResponse(context + "/dataforms/app/menu/page/SiteMapPage." + this.getPageExt());
			}
		} else {
			// データベース初期化前の場合、データベースの初期化ページを表示。
			return new RedirectResponse(context + "/dataforms/devtool/db/page/InitializeDatabasePage." + this.getPageExt());
		}
	}

	/**
	 * ログインページへリダイレクトします。
	 * @param context コンテキストパス。
	 * @return ログインページへのリダイレクトレスポンス。
	 */
	protected Response toLoginPage(String context) {
		// ログインしていない場合。
		if (this.isSamlLogin()) {
			// SAML認証を表示。
			return new RedirectResponse(context + "/dataforms/saml/page/SamlLoginPage." + this.getPageExt());
		} else {
			// dataforms認証を表示。
			return new RedirectResponse(context + "/dataforms/app/login/page/LoginPage." + this.getPageExt());
		}
	}
}
