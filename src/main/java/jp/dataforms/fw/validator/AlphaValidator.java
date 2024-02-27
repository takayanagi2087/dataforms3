package jp.dataforms.fw.validator;

/**
 * 半角英字バリデータクラス。
 *
 */
public final class AlphaValidator extends RegexpValidator {
	/**
	 * コンストラクタ。
	 */
	public AlphaValidator() {
		super("error.alpha", "^[a-zA-Z]+$");
	}
}
