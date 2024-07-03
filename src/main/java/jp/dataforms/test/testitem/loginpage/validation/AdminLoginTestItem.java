package jp.dataforms.test.testitem.loginpage.validation;

import java.io.File;

import jp.dataforms.fw.controller.Page;
import jp.dataforms.test.annotation.TestItemInfo;
import jp.dataforms.test.component.ButtonTestElement;
import jp.dataforms.test.component.FormTestElement;
import jp.dataforms.test.component.PageTestElement;
import jp.dataforms.test.executor.PageTester.Conf;
import jp.dataforms.test.executor.PageTester.TestUser;
import jp.dataforms.test.selenium.Browser;
import jp.dataforms.test.testitem.TestItem;
import jp.dataforms.test.testitem.loginpage.LoginFormTestItem;

/**
 * 管理者ログイン。
 */
@TestItemInfo(group = "login", seq = "001")
public class AdminLoginTestItem extends LoginFormTestItem {
	/**
	 * Logger.
	 */
	// private static Logger logger = LogManager.getLogger(PadPasswordTestItem.class);

	/**
	 * テスト条件。
	 */
	private static final String CONDITION = """
		管理者でログインする。
		""";

	/**
	 * 期待値。
	 */
	private static final String EXPECTED = """
		管理者のサイトマップが表示されること。
		""";

	/**
	 * コンストラクタ。
	 */
	public AdminLoginTestItem() {
		super(CONDITION, EXPECTED);
	}
	
	
	
	@Override
	protected ResultType test(final Page page, final Browser browser) throws Exception {
		PageTestElement pageTestElement = browser.getPage();
		Conf conf = TestItem.getConf();
		TestUser user = conf.getTestUser("admin");
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
