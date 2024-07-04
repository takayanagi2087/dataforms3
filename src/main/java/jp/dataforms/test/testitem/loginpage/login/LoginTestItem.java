package jp.dataforms.test.testitem.loginpage.login;

import java.io.File;
import java.util.List;

import jp.dataforms.fw.controller.Page;
import jp.dataforms.test.app.login.LoginFormTestElement;
import jp.dataforms.test.app.login.LoginPageTestElement;
import jp.dataforms.test.app.menu.SiteMapFormTestElement;
import jp.dataforms.test.app.menu.SiteMapPageTestElement;
import jp.dataforms.test.component.ButtonTestElement;
import jp.dataforms.test.component.PageTestElement;
import jp.dataforms.test.selenium.Browser;
import jp.dataforms.test.tester.PageTester.Conf;
import jp.dataforms.test.tester.PageTester.TestUser;
import jp.dataforms.test.testitem.TestItem;
import jp.dataforms.test.testitem.loginpage.LoginFormTestItem;

/**
 * ログインテストの基本クラス。
 */
public abstract class LoginTestItem extends LoginFormTestItem {

	/**
	 * コンストラクタ。
	 * @param condition テスト条件。
	 * @param expected 期待値。
	 */
	public LoginTestItem(final String condition, final String expected) {
		super(condition, expected);
	}
	
	/**
	 * ログインIDを取得します。
	 * @return ログインID。
	 */
	protected abstract String getLoginId();

	
	/**
	 * サイトマップをチェックします。
	 * @param browser ブラウザ。
	 * @return サイトマップのチェック結果。
	 */
	protected abstract ResultType checkSiteMap(final Browser browser);
	
	
	@Override
	protected ResultType test(final Browser browser) throws Exception {
		LoginPageTestElement pageTestElement = this.getLoginPageTestElement(browser);
		Conf conf = TestItem.getConf();
		TestUser user = conf.getTestUser(this.getLoginId());
		LoginFormTestElement f = pageTestElement.getLoginForm();
		f.getLoginIdField().setValue(user.getLoginId());
		f.getPasswordField().setValue(user.getPassword());
		f.getLoginButton().click();
		Browser.sleep(2);
		ResultType ret = this.checkSiteMap(browser);
		return ret;
	}


	@Override
	protected String saveAttachFile(final Page page, final Browser browser, final ResultType result) throws Exception {
		String imageFile =  this.getTestItemPath() + "/" + this.getFileName() + ".png";
		String path = browser.saveScreenShot(imageFile);
		File f = new File(path);
		String ret = "<img src='" + f.getName() + "' width='1024'/>";
		return ret;
	}
	
	@Override
	protected void finish(final Browser browser) throws Exception {
		Browser.sleep(2);
		PageTestElement pageTestElement = browser.getPageTestElement();
		ButtonTestElement btn = pageTestElement.getLogoutButton();
		btn.click();
	}
	
	/**
	 * リンクの存在チェックを行います。
	 * @param browser ブラウザ。
	 * @param url URL。
	 * @return 存在する場合true。
	 */
	protected boolean findLink(final Browser browser, final String url) {
		boolean ret = false;
		SiteMapPageTestElement pte = this.getSietMapPageTestElement(browser);
		SiteMapFormTestElement fte = pte.getSiteMapForm();
		List<String> list = fte.getLinkList();
		for (String link: list) {
			if (link.indexOf(url) >= 0) {
				ret = true;
			}
		}
		return ret;
	}
}
