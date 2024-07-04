package jp.dataforms.test.testitem.loginpage;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import jp.dataforms.fw.app.login.page.LoginForm;
import jp.dataforms.fw.app.login.page.LoginPage;
import jp.dataforms.test.app.login.LoginPageTestElement;
import jp.dataforms.test.app.menu.SiteMapPageTestElement;
import jp.dataforms.test.selenium.Browser;
import jp.dataforms.test.testitem.TestItem;

/**
 * LoginPage, LoginFormのテスト項目。
 */
public abstract class LoginFormTestItem extends TestItem {
	/**
	 * コンストラクタ。
	 * @param condition テスト条件。
	 * @param expected 期待値。
	 */
	public LoginFormTestItem(final String condition, final String expected) {
		super(LoginPage.class, LoginForm.class, condition, expected);
	}
	
	/**
	 * ログインページのテスト要素を取得します。
	 * @param browser ブラウザ。
	 * @return ログインページのテスト要素。
	 */
	protected LoginPageTestElement getLoginPageTestElement(final Browser browser) {
		WebElement element = browser.findElement(By.xpath("//body"));
		LoginPageTestElement page = new LoginPageTestElement(browser, element);
		return page;
	}
	
	/**
	 * サイトマップページのテスト要素を取得します。
	 * @param browser ブラウザ。
	 * @return サイトマップページのテスト要素。
	 */
	protected SiteMapPageTestElement getSietMapPageTestElement(final Browser browser) {
		WebElement element = browser.findElement(By.xpath("//body"));
		SiteMapPageTestElement page = new SiteMapPageTestElement(browser, element);
		return page;
	}
}
