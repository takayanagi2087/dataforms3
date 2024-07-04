package jp.dataforms.test.testitem.loginpage.validation;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jp.dataforms.fw.controller.Page;
import jp.dataforms.test.annotation.TestItemInfo;
import jp.dataforms.test.annotation.TestItemInfo.Type;
import jp.dataforms.test.component.FormTestElement;
import jp.dataforms.test.component.PageTestElement;
import jp.dataforms.test.selenium.Browser;
import jp.dataforms.test.testitem.loginpage.LoginFormTestItem;

/**
 * LoginFormのバリデーション。
 */
@TestItemInfo(group = "validation", seq = "001", type = Type.ERROR)
public class EnptyValidationTestItem extends LoginFormTestItem {
	/**
	 * Logger.
	 */
	private static Logger logger = LogManager.getLogger(EnptyValidationTestItem.class);

	/**
	 * テスト条件。
	 */
	private static final String CONDITION = """
		何も入力しないでログインボタンを押下する。
		""";

	/**
	 * 期待値。
	 */
	private static final String EXPECTED = """
		「ログインID.が入力されていません。」というメッセージが表示されること。				
		""";

	/**
	 * コンストラクタ。
	 */
	public EnptyValidationTestItem() {
		super(CONDITION, EXPECTED);
	}
	
	
	@Override
	protected ResultType test(final Browser browser) throws Exception {
		PageTestElement pageTestElement = browser.getPageTestElement();
		FormTestElement f = pageTestElement.getForm("loginForm");
		f.getButton("loginButton").click();
		Browser.sleep(2);
		String message = pageTestElement.getErrorMessage();
		logger.debug("text=" + message);
		if ("ログインID.が入力されていません。".equals(message)) {
			return ResultType.SYSTEM_OK;
		} else {
			return ResultType.SYSTEM_NG;
		}
	}

	@Override
	protected String saveAttachFile(final Page page, final Browser browser, final ResultType result) throws Exception {
		PageTestElement pageTestElement = browser.getPageTestElement();
		String imageFile =  this.getTestItemPath() + "/" + this.getFileName() + ".png";
		String path = pageTestElement.getBrowser().saveScreenShot(imageFile);
		File f = new File(path);
		String ret = "<img src='" + f.getName() + "' width='1024'/>";
		return ret;
	}
	

}
