package jp.dataforms.fw.validator;

/**
 * 郵便番号バリデータ。
 *
 */
public final class ZipCodeValidator extends RegexpValidator {
	/**
	 * コンストラクタ。
	 */
	public ZipCodeValidator() {
		super("error.zipcode", "[0-9]{3}\\-[0-9]{4}");
	}
}
