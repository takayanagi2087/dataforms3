package jp.dataforms.fw.validator;

/**
 * 表示中必須バリデータ。
 *
 */
public class DisplayedRequiredValidator extends RequiredValidator {
	/**
	 * コンストラクタ。
	 */
	public DisplayedRequiredValidator() {
		
	}
	
	/**
	 * {@inheritDoc}
	 * <pre>
	 * サーバーサイドでは判定できないので、常にtrueを返します。
	 * </pre>
	 */
	@Override
	public boolean validate(final Object value) {
		return true;
	}
}
