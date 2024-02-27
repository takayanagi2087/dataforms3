package jp.dataforms.fw.exception;

/**
 * バリデーション結果を投げるための例外。
 *
 */
public class ThrowObjectException extends Exception {
	/**
	 * serialVersionUID。
	 */
	private static final long serialVersionUID = -6521562904237376064L;
	/**
	 * 例外の情報保存したオブジェクト。
	 */
	private Object exceptionInfo = null;
	/**
	 * コンストラクタ。
	 * @param exinfo バリデーション情報オブジェクト。
	 */
	public ThrowObjectException(final Object exinfo) {
		this.exceptionInfo = exinfo;
	}

	/**
	 * 例外情報を取得します。
	 * @return 例外の情報を示すオブジェクト。
	 */
	public Object getExceptionInfo() {
		return this.exceptionInfo;
	}
}
