package jp.dataforms.fw.validator;

/**
 * 半角カタカナバリデータクラス。
 *
 */
public final class HankakukatakanaValidator extends RegexpValidator {
	/**
	 * コンストラクタ。
	 */
	public HankakukatakanaValidator() {
		super("error.hankakukatakana", "^[\\uFF65-\\uFF9F]+$");
	}
}
