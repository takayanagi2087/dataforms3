package jp.dataforms.test.component;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import jp.dataforms.test.app.LoginFormElement;
import jp.dataforms.test.selenium.Browser;

/**
 * DataFormsのテスター。
 */
public class DataFormsTester extends Tester {
	/**
	 * Logger.
	 */
	private static Logger logger = LogManager.getLogger(DataFormsTester.class);

	/**
	 * 問い合わせフォーム。
	 */
	public static final String ID_QUERY_FORM = "queryForm";

	/**
	 * 問い合わせ結果フォーム。
	 */
	public static final String ID_QUERY_RESULT_FORM = "queryResultForm";

	/**
	 * 編集フォーム。
	 */
	public static final String ID_EDIT_FORM = "editForm";

	/**
	 * コンストラクタ。
	 * @param browser ブラウザ。
	 * @param parent 親コンポーネント。
	 * @param element Web要素。
	 */
	public DataFormsTester(final Browser browser, final Tester parent, final WebElement element) {
		super(browser, parent, element);
	}

	/**
	 * フォームを取得します。
	 * @param id フォームID。
	 * @param cls フォームクラス。
	 * @return フォーム。
	 */
	public FormTester getForm(final String id, final Class<? extends FormTester> cls) {
		try {
			WebElement element = this.getWebElement().findElement(By.xpath("//form[@data-id='" + id + "']"));
			FormTester form = cls.getConstructor(Browser.class, Tester.class, WebElement.class).newInstance(this.getBrowser(), this, element);
			return form;
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
		}
		return null;
	}

	/**
	 * フォームを取得します。
	 * @param cls フォームクラス。
	 * @return フォーム。
	 */
	public FormTester getForm(final Class<? extends FormTester> cls) {
		try {
			java.lang.reflect.Field field = cls.getField("ID");
			String id = (String) field.get(null);
			WebElement element = this.getWebElement().findElement(By.xpath("//form[@data-id='" + id + "']"));
			FormTester form = cls.getConstructor(Browser.class, Tester.class, WebElement.class).newInstance(this.getBrowser(), this, element);
			return form;
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
		}
		return null;
	}

	
	
	/**
	 * フォームを取得します。
	 * @param id フォームID。
	 * @return フォーム。
	 */
	public FormTester getForm(final String id) {
		return this.getForm(id, FormTester.class);
	}

	/**
	 * 問い合わせフォームを取得します。
	 * @return 問い合わせフォーム。
	 */
	public QueryFormTester getQueryForm() {
		QueryFormTester f = (QueryFormTester) this.getForm(QueryFormTester.class);
		return f;
	}

	/**
	 * 問い合わせ結果フォームを取得します。
	 * @return 問い合わせフォーム。
	 */
	public QueryResultFormTester getQueryResultForm() {
		QueryResultFormTester f = (QueryResultFormTester) this.getForm(QueryResultFormTester.class);
		return f;
	}

	/**
	 * 編集フォームを取得します。
	 * @return 編集フォーム。
	 */
	public EditFormTester getEditForm() {
		EditFormTester f = (EditFormTester) this.getForm(EditFormTester.class);
		return f;
	}

	/**
	 * ログインフォームを取得します。
	 * @return ログインフォーム。
	 */
	public LoginFormElement getLoginForm() {
		LoginFormElement f = (LoginFormElement) this.getForm(LoginFormElement.class);
		return f;
	}

}
