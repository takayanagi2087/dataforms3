package jp.dataforms.test.checkitem;

import jp.dataforms.fw.controller.WebComponent;
import jp.dataforms.test.annotation.CheckItemInfo;
import jp.dataforms.test.component.TestElement;
import lombok.Getter;


/**
 * テスト項目クラス。
 */
public abstract class CheckItem {
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
		return a.target();
	}
	
	/**
	 * グループを取得します。
	 * @return グループを取得します。
	 */
	public String getGroup() {
		CheckItemInfo a = this.getClass().getAnnotation(CheckItemInfo.class);
		return a.group();
	}


	/**
	 * テスト順を取得します。
	 * @return テスト順を取得します。
	 */
	public String getSeq() {
		CheckItemInfo a = this.getClass().getAnnotation(CheckItemInfo.class);
		return a.seq();
	}


	/**
	 * 回帰テスト対象フラグを取得します。
	 * @return 回帰テスト対象フラグ。
	 */
	public boolean getRegression() {
		CheckItemInfo a = this.getClass().getAnnotation(CheckItemInfo.class);
		return a.regression();
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
	 * @param tester テースター。
	 * @return テスト結果。
	 * @throws Exception 例外。
	 */
	public abstract ResultType test(final TestElement tester) throws Exception;
	
	/**
	 * 結果の保存処理。
	 * @throws Exception 例外。
	 */
	public void saveResult() throws Exception {
		
	}
}

