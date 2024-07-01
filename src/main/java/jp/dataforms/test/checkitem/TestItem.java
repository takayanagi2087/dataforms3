package jp.dataforms.test.checkitem;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jp.dataforms.fw.controller.Page;
import jp.dataforms.fw.controller.WebComponent;
import jp.dataforms.fw.devtool.javasrc.JavaSrcGenerator.Template;
import jp.dataforms.fw.util.FileUtil;
import jp.dataforms.test.annotation.TestItemInfo;
import jp.dataforms.test.component.PageTestElement;
import jp.dataforms.test.component.TestElement;
import lombok.Getter;
import lombok.Setter;


/**
 * テスト項目クラス。
 */
public abstract class TestItem {
	/**
	 * Logger.
	 */
	private static Logger logger = LogManager.getLogger(TestItem.class);
	
	/**
	 * テスト対象ページクラス。
	 */
	@Getter
	private Class<? extends Page> pageClass = null;
	
	/**
	 * テスト対象コンポーネントクラス。
	 */
	@Getter
	private Class<? extends WebComponent> compClass = null;

	/**
	 * テストの条件。
	 */
	@Getter
	private String condition = null;
	
	/**
	 * テストの期待値。
	 */
	@Getter
	private String expected = null;

	/**
	 * テスト結果。
	 */
	public static enum ResultType {
		/**
		 * プログラムでチェックしてOKとなった。
		 */
		SYSTEM_OK,
		/**
		 * プログラムでチェックしてNGとなった。
		 */
		SYSTEM_NG,
		/**
		 * ユーザが出力された結果を確認する必要がある。
		 */
		USER_CHECK
	}
	
	
	/**
	 * チェック結果。
	 */
	private ResultType checkResult = null;

	/**
	 * チェック結果を取得します。
	 * @return チェック結果。
	 */
	public ResultType getCheckResult() {
		return checkResult;
	}

	/**
	 * チェック結果を設定します。
	 * @param checkResult チェック結果。
	 */
	protected void setCheckResult(final ResultType checkResult) {
		this.checkResult = checkResult;
	}
	
	/**
	 * テスト時刻。
	 */
	private Date testDate = null;
	
	
	/**
	 * テスト日時を取得します。
	 * @return テスト時刻。
	 */
	public Date getTestDate() {
		return testDate;
	}

	
	/**
	 * テスト時刻を設定します。
	 * @param testDate テスト日時。
	 */
	protected void setTestDate(final Date testDate) {
		this.testDate = testDate;
	}
	
	/**
	 * テスト日時の表示形式を取得します。
	 * @return テスト日時の表示形式。
	 */
	public String getTestDateText() {
		SimpleDateFormat fmt = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		return fmt.format(this.testDate);
	}
	
	/**
	 * コンストラクタ。
	 * @param pageClass ページクラス。
	 * @param compClass ページクラス。
	 * @param condition テスト条件。
	 * @param expected 期待値。
	 */
	public TestItem(final Class<? extends Page> pageClass, final Class<? extends WebComponent> compClass, final String condition, final String expected) {
		this.pageClass = pageClass;
		this.compClass = compClass;
		this.condition = condition;
		this.expected = expected;
	}
	

	/**
	 * グループを取得します。
	 * @return グループを取得します。
	 */
	public String getGroup() {
		TestItemInfo a = this.getClass().getAnnotation(TestItemInfo.class);
		if (a != null) {
			return a.group();
		}
		return null;
	}


	/**
	 * テスト順を取得します。
	 * @return テスト順を取得します。
	 */
	public String getSeq() {
		TestItemInfo a = this.getClass().getAnnotation(TestItemInfo.class);
		if (a != null) {
			return a.seq();
		}
		return null;
	}


	/**
	 * 回帰テスト対象フラグを取得します。
	 * @return 回帰テスト対象フラグ。
	 */
	public boolean getRegression() {
		TestItemInfo a = this.getClass().getAnnotation(TestItemInfo.class);
		if (a != null) {
			return a.regression();
		} else {
			return false;
		}
	}

	/**
	 * テスト順を取得します。
	 * @return テスト順を取得します。
	 */
	public TestItemInfo.Type getType() {
		TestItemInfo a = this.getClass().getAnnotation(TestItemInfo.class);
		return a.type();
	}
	
	/**
	 * テストを実行します。
	 * 
	 * @param page ページクラスのインスタンス。
	 * @param pageTestElement ページのテスト要素。
	 * @return テスト結果。
	 * @throws Exception 例外。
	 */
	public abstract ResultType test(final Page page, final PageTestElement pageTestElement) throws Exception;
	
	
	/**
	 * テスト結果のパス。
	 */
	@Getter
	@Setter
	private static String testResult = null;
	

	/**
	 * 結果の出力先を取得します。
	 * @return 結果の出力先。 
	 */
	public String getResultPath() {
		String path = TestItem.testResult; 
		logger.debug("result path=" + path);
		return path;
	}
	
	/**
	 * ページのテンプレートを取得します。
	 * @return ページテンプレート。
	 * @throws Exception 例外。
	 */
	protected Template getTemplate() throws Exception {
		Template tmp = new Template(TestItem.class, "template/template.html");
		return tmp;
	}
	
	/**
	 * 添付ファイルを作成します。
	 * @param page ページクラスのインスタンス。
	 * @param testElement テスト要素。
	 * @param result テスト要素。
	 * @return リンク情報。
	 * @throws Exception 例外。
	 */
	protected String saveAttachFile(final Page page, final TestElement testElement, final ResultType result) throws Exception {
		return "";
	}
	
	/**
	 * HTMLファイル名を取得します。
	 * @return HTMLファイル名。
	 */
	protected String getFileName() {
		return this.getGroup() + "-" + this.getSeq();
	}
	
	/**
	 * テスト結果を保存するパスを取得します。
	 * @return テスト結果を保存するパス。
	 */
	public String getTestItemPath() {
		Class<? extends Page> pageClass = this.getPageClass();
		Class<? extends WebComponent> compClass = this.getCompClass();
		String resultPath = this.getResultPath() + "/" + pageClass.getName() + "/" + compClass.getSimpleName();
		return resultPath;
	}
	
	/**
	 * 結果ファイルのパスを取得します。
	 * @return 結果ファイルのパス。
	 */
	public String getTestItemHtmlPath() {
		String resultPath = this.getTestItemPath() + "/" + this.getFileName() + ".html";
		return resultPath;
	}
	
	/**
	 * 結果の保存処理。
	 * @param page ページクラスのインスタンス。
	 * @param testElement テスト要素。
	 * @param result テスト結果。
	 * @throws Exception 例外。
	 */
	public void saveResult(final Page page, final TestElement testElement, final ResultType result) throws Exception {
		Template templ = this.getTemplate();
		Date today = new Date();
		this.setTestDate(today);
		this.setCheckResult(result);
		templ.replace("pageName", page.getPageName());
		templ.replace("pageClass", page.getClass().getName());
		templ.replace("group", this.getGroup());
		templ.replace("seq", this.getSeq());
		templ.replace("condition", this.getCondition());
		templ.replace("expected", this.getExpected());
		templ.replace("testDate", this.getTestDateText());
		templ.replace("result", result.name());
		templ.replace("attachFiles", this.saveAttachFile(page, testElement, result));
		logger.debug("html=" + templ.getSource());
		String resultPath = this.getTestItemHtmlPath();
		File dir = new File(resultPath).getParentFile();
		if (!dir.exists()) {
			dir.mkdirs();
		}
		logger.debug("resultPath=" + resultPath);
		FileUtil.writeTextFile(resultPath, templ.getSource(), "utf-8");
	}
	
	/**
	 * テスト結果一覧の行を取得します。
	 * @param no 行番号。
	 * @return テスト結果一覧の行。
	 */
	public String getListRow(final int no) {
		StringBuilder sb = new StringBuilder();
		sb.append("\t\t\t\t<tr>");
		sb.append("<td>" + no + "</td>");
		String link = "./" + this.getCompClass().getSimpleName() + "/" + this.getFileName() + ".html";
		sb.append("<td><a href='" + link + "'>" + this.getGroup() + "-" + this.getSeq() + "</a></td>");
		sb.append("<td>" + this.getCondition() + "</td>");
		sb.append("<td>" + this.getExpected() + "</td>");
		sb.append("<td>" + this.getTestDateText() + "</td>");
		sb.append("<td>" + this.getCheckResult().name() + "</td>");
		sb.append("</tr>\n");
		return sb.toString();
	}
}

