package jp.dataforms.fw.dao;

/**
 * 一意制約。
 *
 */
public class UniqueKey extends Constraint {
	/**
	 * コンストラクタ。
	 * @param name 制約名称。
	 * @param msgkey メッセージキー。
	 */
	public UniqueKey(final String name, final String msgkey) {
		super(name, msgkey);
	}

	/**
	 * コンストラクタ。
	 * @param name 制約名称。
	 */
	public UniqueKey(final String name) {
		super(name, Constraint.DEFAULT_MESSAGE_KEY);
	}

}
