package jp.dataforms.fw.validator;


/**
 * DBマッチングパリデータ基本クラス。
 */
public abstract class DbMatchValidator extends FieldValidator {
	/**
	 * コンストラクタ。
	 */
	public DbMatchValidator() {
		super("error.codenotexist");
	}

	/**
	 * コンストラクタ。
	 * @param msgkey メッセージキー。
	 */
	public DbMatchValidator(final String msgkey) {
		super(msgkey);
	}

/*	@Override
	public boolean validate(Object value) throws Exception {
		// TODO マスタをアクセスしチェックする処理を実装してください。
		return false;
	}
*/
}

