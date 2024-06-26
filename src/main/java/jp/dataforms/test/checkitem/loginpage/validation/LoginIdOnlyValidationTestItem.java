package jp.dataforms.test.checkitem.loginpage.validation;

import java.io.File;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jp.dataforms.fw.controller.Page;
import jp.dataforms.test.annotation.TestItemInfo;
import jp.dataforms.test.annotation.TestItemInfo.Type;
import jp.dataforms.test.checkitem.loginpage.LoginFormTestItem;
import jp.dataforms.test.component.FormTestElement;
import jp.dataforms.test.component.PageTestElement;
import jp.dataforms.test.selenium.Browser;

/**
 * LoginFormのバリデーション。
 */
@TestItemInfo(group = "validation", seq = "002", type = Type.ERROR)
public class LoginIdOnlyValidationTestItem extends LoginFormTestItem {
	/**
	 * Logger.
	 */
	private static Logger logger = LogManager.getLogger(LoginIdOnlyValidationTestItem.class);

	/**
	 * テスト条件。
	 */
	private static final String CONDITION = """
		ログインIDのみを入力しログインボタンを押下する。
		""";

	/**
	 * 期待値。
	 */
	private static final String EXPECTED = """
		「パスワードが入力されていません。」「パスキーが入力されていません。」というメッセージが表示されること。
				
		""";

	/**
	 * コンストラクタ。
	 */
	public LoginIdOnlyValidationTestItem() {
		super(CONDITION, EXPECTED);
	}
	
	
	@Override
	protected ResultType test(Page page, final PageTestElement pageTestElement) throws Exception {
		FormTestElement f = pageTestElement.getForm("loginForm");
		f.getField("loginId").setValue("user");
		f.getButton("loginButton").click();
		Browser.sleep(2);
	//	String message = pageTestElement.findWebElement(By.id("errorMessages")).getText().trim();
		List<String> messageList = pageTestElement.getErrorMessageList();
		logger.debug("text=" + messageList);
		if (messageList.indexOf("パスワードが入力されていません。") >= 0 
			&& messageList.indexOf("パスキーが入力されていません。") >= 0) {
			return ResultType.SYSTEM_OK;
		} else {
			return ResultType.SYSTEM_NG;
		}
	}

	@Override
	protected String saveAttachFile(Page page, PageTestElement pageTestElement, ResultType result) throws Exception {
		String imageFile =  this.getTestItemPath() + "/" + this.getFileName() + ".png";
		String path = pageTestElement.getBrowser().saveScreenShot(imageFile);
		File f = new File(path);
		String ret = "<img src='" + f.getName() + "' width='1024'/>";
		return ret;
	}
	

}
