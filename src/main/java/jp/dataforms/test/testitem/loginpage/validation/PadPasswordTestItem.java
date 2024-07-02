package jp.dataforms.test.testitem.loginpage.validation;

import java.io.File;

import jp.dataforms.fw.controller.Page;
import jp.dataforms.test.annotation.TestItemInfo;
import jp.dataforms.test.annotation.TestItemInfo.Type;
import jp.dataforms.test.component.AlertDialogTestElement;
import jp.dataforms.test.component.FormTestElement;
import jp.dataforms.test.component.PageTestElement;
import jp.dataforms.test.selenium.Browser;
import jp.dataforms.test.testitem.loginpage.LoginFormTestItem;

/**
 * LoginFormのバリデーション。
 */
@TestItemInfo(group = "validation", seq = "003", type = Type.ERROR)
public class PadPasswordTestItem extends LoginFormTestItem {
	/**
	 * Logger.
	 */
	// private static Logger logger = LogManager.getLogger(PadPasswordTestItem.class);

	/**
	 * テスト条件。
	 */
	private static final String CONDITION = """
		ユーザIDと間違ったパスワードを入力しログインボタンを押下する。
		""";

	/**
	 * 期待値。
	 */
	private static final String EXPECTED = """
		「ユーザIDまたはパスワードが違います。」というメッセージが表示されること。
		""";

	/**
	 * コンストラクタ。
	 */
	public PadPasswordTestItem() {
		super(CONDITION, EXPECTED);
	}
	
	
	@Override
	protected PageTestElement start(Page page, PageTestElement pageTestElement) throws Exception {
		Browser browser = pageTestElement.getBrowser();
		return browser.reload();
	}
	
	@Override
	protected ResultType test(Page page, PageTestElement pageTestElement) throws Exception {
		FormTestElement f = pageTestElement.getForm("loginForm");
		f.getField("loginId").setValue("user");
		f.getField("password").setValue("PadPassword");
		f.getButton("loginButton").click();
		Browser.sleep(2);
		AlertDialogTestElement alertDialog = pageTestElement.getAlertDialog();
		String msg = alertDialog.getMessage();
		ResultType ret = null;
		if (msg.equals("ユーザIDまたはパスワードが違います。")) {
			ret = ResultType.SYSTEM_OK;
		} else {
			ret = ResultType.SYSTEM_NG;
		}
		return ret;
	}

	@Override
	protected void finish(Page page, PageTestElement pageTestElement) throws Exception {
		pageTestElement.getAlertDialog().clickOkButton();
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
