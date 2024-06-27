package jp.dataforms.test.checkitem;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jp.dataforms.fw.controller.Page;
import jp.dataforms.fw.controller.WebComponent;
import jp.dataforms.fw.devtool.javasrc.JavaSrcGenerator.Template;
import jp.dataforms.fw.util.FileUtil;
import jp.dataforms.test.annotation.CheckItemInfo;
import jp.dataforms.test.component.TestElement;
import lombok.Getter;
import lombok.Setter;


/**
 * テスト項目クラス。
 */
public abstract class CheckItem {
	/**
	 * Logger.
	 */
	private static Logger logger = LogManager.getLogger(CheckItem.class);
	
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
	 * コンストラクタ。
	 * @param condition テスト条件。
	 * @param expected 期待値。
	 */
	public CheckItem(final String condition, final String expected) {
		this.condition = condition;
		this.expected = expected;
	}
	

	/**
	 * チェック対象のクラスを取得します。
	 * @return チェック対象のクラスを取得します。
	 */
	public Class<? extends WebComponent> getTargetClass() {
		CheckItemInfo a = this.getClass().getAnnotation(CheckItemInfo.class);
		if (a != null) {
			return a.target();
		}
		return null;
	}
	
	/**
	 * グループを取得します。
	 * @return グループを取得します。
	 */
	public String getGroup() {
		CheckItemInfo a = this.getClass().getAnnotation(CheckItemInfo.class);
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
		CheckItemInfo a = this.getClass().getAnnotation(CheckItemInfo.class);
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
		CheckItemInfo a = this.getClass().getAnnotation(CheckItemInfo.class);
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
	public CheckItemInfo.Type getType() {
		CheckItemInfo a = this.getClass().getAnnotation(CheckItemInfo.class);
		return a.type();
	}
	
	/**
	 * テストを実行します。
	 * 
	 * @param page ページクラスのインスタンス。
	 * @param testElement テスト要素。
	 * @return テスト結果。
	 * @throws Exception 例外。
	 */
	public abstract ResultType test(final Page page, final TestElement testElement) throws Exception;
	
	
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
		String path = CheckItem.testResult + "/" + this.getGroup(); 
		logger.debug("result path=" + path);
		return path;
	}
	
	/**
	 * ページのテンプレートを取得します。
	 * @return ページテンプレート。
	 * @throws Exception 例外。
	 */
	protected Template getTemplate() throws Exception {
		Template tmp = new Template(CheckItem.class, "template/template.html");
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
	 * 結果の保存処理。
	 * @param page ページクラスのインスタンス。
	 * @param testElement テスト要素。
	 * @param result テスト結果。
	 * @throws Exception 例外。
	 */
	public void saveResult(final Page page, final TestElement testElement, final ResultType result) throws Exception {
		Template templ = this.getTemplate();
		templ.replace("pageName", page.getPageName());
		templ.replace("pageClass", page.getClass().getName());
		templ.replace("group", this.getGroup());
		templ.replace("seq", this.getSeq());
		templ.replace("condition", this.getCondition());
		templ.replace("expected", this.getExpected());
		templ.replace("result", result.name());
		templ.replace("attachFiles", this.saveAttachFile(page, testElement, result));
		logger.debug("html=" + templ.getSource());
		File dir = new File(this.getResultPath());
		if (!dir.exists()) {
			dir.mkdirs();
		}
		String resultPath = this.getResultPath() + "/" + this.getFileName() + ".html";
		logger.debug("resultPath=" + resultPath);
		FileUtil.writeTextFile(resultPath, templ.getSource(), "utf-8");
	}
}

