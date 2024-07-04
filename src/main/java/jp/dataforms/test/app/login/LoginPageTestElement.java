package jp.dataforms.test.app.login;

import org.openqa.selenium.WebElement;

import jp.dataforms.test.component.PageTestElement;
import jp.dataforms.test.selenium.Browser;

/**
 * ログインページのテスト要素。
 */
public class LoginPageTestElement extends PageTestElement {
	/**
	 * コンストラクタ。
	 * @param browser ブラウザ。
	 * @param element WebElement。
	 */
	public LoginPageTestElement(final Browser browser, final WebElement element) {
		super(browser, null, element);
	}
	
	/**
	 * ログインフォームを取得します。
	 * @return ログインフォームのテスト要素。
	 */
	public LoginFormTestElement getLoginForm() {
		return (LoginFormTestElement) this.getForm(LoginFormTestElement.ID, LoginFormTestElement.class);
	}
}
