package jp.dataforms.fw.validator;

/**
 * ASCII文字バリデータクラス。
 *
 */
public final class AsciiValidator extends RegexpValidator {
	/**
	 * コンストラクタ。
	 */
	public AsciiValidator() {
		super("error.ascii", "^[\\u0020-\\u007E]+$");
	}
}
