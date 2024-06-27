package jp.dataforms.test.app;


import org.openqa.selenium.WebElement;

import jp.dataforms.test.component.TestElement;
import jp.dataforms.test.component.FormTestElement;
import jp.dataforms.test.selenium.Browser;

/**
 * ログインフォーム要素。 
 */
public class LoginFormElement extends FormTestElement {
	/**
	 * ログインフォーム。
	 */
	public static final String ID = "loginForm";

	/**
	 * ログインボタンのID。
	 */
	public static final String ID_LOGIN_BUTTON = "loginButton";
	
	/**
	 * LoginIDのフィールドID。
	 */
	public static final String ID_LOGIN_ID = "loginId";
	/**
	 * PasswordのフィールドID。
	 */
	public static final String ID_PASSWORD = "password";
	
	/**
	 * コンストラクタ。
	 * @param browser ブラウザ。
	 * @param parent 親コンポーネント。
	 * @param webElement FormのWebElement。
	 */
	public LoginFormElement(final Browser browser, final TestElement parent, final WebElement webElement) {
		super(browser, parent, webElement);
	}
	
	

}
