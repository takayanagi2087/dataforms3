package jp.dataforms.test.executor;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.Dimension;

import jp.dataforms.fw.app.login.page.LoginForm;
import jp.dataforms.fw.app.login.page.LoginPage;
import jp.dataforms.fw.controller.Page;
import jp.dataforms.test.component.PageTestElement;
import jp.dataforms.test.selenium.Browser;
import jp.dataforms.test.testitem.TestItem;
import jp.dataforms.test.testitem.loginpage.LoginFormTestItem;

/**
 * ログインページテスター。
 */
public class LoginPageTester extends PageTester {
	/**
	 * Logger.
	 */
	private static Logger logger = LogManager.getLogger(LoginPageTester.class);
	
	/**
	 * コンストラクタ。
	 * @param confFile 設定ファイル。
	 */
	public LoginPageTester(final String confFile) {
		super(confFile, LoginPage.class);
	}
	
	/**
	 * バリデージョンのテスト。
	 * @param pt ページのテスト要素。
	 * @return テスト結果リスト。
	 * @throws Exception 例外。
	 */
	private List<TestItem> testValidation(final PageTestElement pt) throws Exception {
		Page page = this.getPageInstance();
		Browser b = pt.getBrowser();
		b.setClientSize(new Dimension(1024, 540));
		List<TestItem> list = this.queryCheckItem("jp.dataforms.test.testitem.loginpage", LoginFormTestItem.class, null, null);
		for (TestItem ci: list) {
			logger.info("GROUP:" + ci.getGroup() + ", SEQ:" + ci.getSeq());
			logger.info("CONDITION:" + ci.getCondition());
			ci.exec(page, pt);
			Browser.sleep(1);
		}
		return list;
	}
	
	
	@Override
	public void exec() throws Exception {
		TestItem.setTestResult(this.getConf().getTestApp().getTestResult());
		Browser browser = this.getBrowser();
		PageTestElement pt = openPage(browser);
		List<TestItem> list = new ArrayList<TestItem>();
		list.addAll(this.testResponsive(pt, LoginPage.class, LoginForm.class));
		list.addAll(this.testValidation(pt));
		// this.getResultJson(list);
		this.saveIndexHtml(list);
		browser.close();
	}
	
	/**
	 * メイン処理。
	 * @param args コマンドライン。
	 * <pre>
	 * args[0]	...	テスト設定ファイル。
	 * args[1]	... テストURI。
	 * </pre>
	 */
	public static void main(String[] args) {
		try {
			LoginPageTester exec = new LoginPageTester(args[0]);
			exec.readConf();
			exec.exec();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}
	
}
