package jp.dataforms.fw.validator;

/**
 * 数字バリデータクラス。
 *
 */
public final class NumberCharValidator extends RegexpValidator {
	/**
	 * コンストラクタ。
	 */
	public NumberCharValidator() {
		super("error.numberchar", "^[0-9]+$");
	}
}
