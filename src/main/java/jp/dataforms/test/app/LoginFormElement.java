package jp.dataforms.test.app;


import org.openqa.selenium.WebElement;

import jp.dataforms.test.component.Tester;
import jp.dataforms.test.component.FormTester;
import jp.dataforms.test.selenium.Browser;

/**
 * ログインフォーム要素。 
 */
public class LoginFormElement extends FormTester {
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
	public LoginFormElement(final Browser browser, final Tester parent, final WebElement webElement) {
		super(browser, parent, webElement);
	}
	
	

}
