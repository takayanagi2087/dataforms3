package jp.dataforms.test.app.login;


import org.openqa.selenium.WebElement;

import jp.dataforms.test.component.ButtonTestElement;
import jp.dataforms.test.component.FieldTestElement;
import jp.dataforms.test.component.FormTestElement;
import jp.dataforms.test.component.TestElement;
import jp.dataforms.test.selenium.Browser;

/**
 * ログインフォーム要素。 
 */
public class LoginFormTestElement extends FormTestElement {
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
	public LoginFormTestElement(final Browser browser, final TestElement parent, final WebElement webElement) {
		super(browser, parent, webElement);
	}
	
	/**
	 * ログインボタンを取得します。
	 * @return ログインボタンのテスト要素。
	 */
	public ButtonTestElement getLoginButton() {
		return this.getButton(ID_LOGIN_BUTTON);
	}

	/**
	 * ログインIDフィールドのテスト要素を取得します。
	 * @return ログインIDフィールドのテスト要素。
	 */
	public FieldTestElement getLoginIdField() {
		return this.getField(ID_LOGIN_ID);
	}
	
	/**
	 * ログインIDフィールドのテスト要素を取得します。
	 * @return ログインIDフィールドのテスト要素。
	 */
	public FieldTestElement getPasswordField() {
		return this.getField(ID_PASSWORD);
	}
	
}
