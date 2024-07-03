package jp.dataforms.test.testitem.loginpage.login;

import java.io.File;

import jp.dataforms.fw.controller.Page;
import jp.dataforms.test.component.ButtonTestElement;
import jp.dataforms.test.component.FormTestElement;
import jp.dataforms.test.component.PageTestElement;
import jp.dataforms.test.executor.PageTester.Conf;
import jp.dataforms.test.executor.PageTester.TestUser;
import jp.dataforms.test.selenium.Browser;
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
	
	@Override
	protected ResultType test(final Page page, final Browser browser) throws Exception {
		PageTestElement pageTestElement = browser.getPage();
		Conf conf = TestItem.getConf();
		TestUser user = conf.getTestUser(this.getLoginId());
		FormTestElement f = pageTestElement.getForm("loginForm");
		f.getField("loginId").setValue(user.getLoginId());
		f.getField("password").setValue(user.getPassword());
		f.getButton("loginButton").click();
		Browser.sleep(2);
		ResultType ret = ResultType.SYSTEM_OK;
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
	protected void finish(final Page page, final Browser browser) throws Exception {
		Browser.sleep(2);
		PageTestElement pageTestElement = browser.getPage();
		ButtonTestElement btn = pageTestElement.getButton("logoutButton");
		btn.click();
	}
}
