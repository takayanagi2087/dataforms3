package jp.dataforms.test.testitem;

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
import jp.dataforms.test.selenium.Browser;
import jp.dataforms.test.tester.PageTester.Conf;
import lombok.Data;
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
	 * 設定情報。
	 */
	@Getter
	@Setter
	private static Conf conf = null;
	
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
	private ResultType result = null;

	/**
	 * チェック結果を取得します。
	 * @return チェック結果。
	 */
	public ResultType getResult() {
		return result;
	}

	/**
	 * チェック結果を設定します。
	 * @param result チェック結果。
	 */
	protected void setResult(final ResultType result) {
		this.result = result;
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
	 * ページのインスタンスを取得します。
	 * @return ページのインスタンス。
	 * @throws Exception 例外。
	 */
	protected Page getPageInstance() throws Exception {
		Page page = this.pageClass.getConstructor().newInstance();
		return page;
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
	 * テストの開始時に必要な処理がある場合、その処理を記述します。
	 * @param browser ブラウザ。
	 * @throws Exception 例外。
	 */
	protected void start(final Browser browser) throws Exception {
	}
	
	/**
	 * テストを実行します。
	 * 
	 * @param browser ブラウザ。
	 * @return テスト結果。
	 * @throws Exception 例外。
	 */
	protected abstract ResultType test(final Browser browser) throws Exception;
	
	/**
	 * テストの終了時に必要な処理がある場合、その処理を記述します。
	 * @param browser ブラウザ。
	 * @throws Exception 例外。
	 */
	protected void finish(final Browser browser) throws Exception {
	
	}
	
	/**
	 * テストを実行します。
	 * @param browser ブラウザ。
	 * @return テスト結果。
	 * @throws Exception 例外。
	 */
	public ResultType exec(final Browser browser) throws Exception {
		this.start(browser);
		ResultType ret = this.test(browser);
		this.saveResult(browser, ret);
		this.finish(browser);
		return ret;
	}
	
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
	 * @param browser ブラウザ。
	 * @param result テスト要素。
	 * @return リンク情報。
	 * @throws Exception 例外。
	 */
	protected String saveAttachFile(final Page page, final Browser browser, final ResultType result) throws Exception {
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
	 * @param browser ブラウザ。
	 * @param result テスト結果。
	 * @throws Exception 例外。
	 */
	protected void saveResult(final Browser browser, final ResultType result) throws Exception {
		Template templ = this.getTemplate();
		Date today = new Date();
		this.setTestDate(today);
		this.setResult(result);
		Page page = this.getPageInstance();
		templ.replace("pageName", page.getPageName());
		templ.replace("pageClass", page.getClass().getName());
		templ.replace("group", this.getGroup());
		templ.replace("seq", this.getSeq());
		templ.replace("condition", this.getCondition());
		templ.replace("expected", this.getExpected());
		templ.replace("testDate", this.getTestDateText());
		templ.replace("result", result.name());
		templ.replace("attachFiles", this.saveAttachFile(page, browser, result));
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
		sb.append("<td>" + this.getResult().name() + "</td>");
		sb.append("</tr>\n");
		return sb.toString();
	}
	
	/**
	 * テスト結果情報。
	 */
	@Data
	public static class TestItemResult {
		/**
		 * テストタイプ。
		 */
		private String testType = null;
		/**
		 * 結果詳細HTMLリンク。
		 */
		private String link = null;
		/**
		 * テスト対象のコンポーネントクラス名。
		 */
		private String compClass = null;
		/**
		 * テストID。
		 */
		private String testId = null;
		/**
		 * テスト条件。
		 */
		private String condition = null;
		/**
		 * テスト期待値。
		 */
		private String expected = null;
		/**
		 * テスト日時。
		 */
		private String testDate = null;
		/**
		 * テスト日時。
		 */
		private String result = null;
		/**
		 * コンストラクタ。
		 */
		public TestItemResult() {
			
		}
	}

	/**
	 * テスト結果を取得します。
	 * @return テスト結果。
	 */
	public TestItemResult getTestItemResult() {
		TestItemResult ret = new TestItemResult();

		ret.setTestType(this.getType().name());
		String link = "./" + this.getCompClass().getSimpleName() + "/" + this.getFileName() + ".html";
		ret.setLink(link);
		ret.setCompClass(this.getCompClass().getSimpleName());
		ret.setTestId(this.getGroup() + "-" + this.getSeq());
		ret.setCondition(this.getCondition());
		ret.setExpected(this.getExpected());
		ret.setTestDate(this.getTestDateText());
		ret.setResult(this.getResult().name());
		return ret;
	}
}

