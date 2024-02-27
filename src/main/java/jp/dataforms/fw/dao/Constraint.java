package jp.dataforms.fw.dao;

/**
 * 制約クラス。
 * <pre>
 * 一意制約、外部キー制約違反時のメッセージを管理するクラスです。
 * </pre>
 */
public class Constraint {

	/**
	 * デフォルトメッセージキー。
	 */
	public static final  String DEFAULT_MESSAGE_KEY = "error.constraintviolation";

	/**
	 * 制約名。
	 */
	private String constraintName = null;

	/**
	 * 制約違反時のメッセージキー。
	 */
	private String violationMessageKey = null;

	/**
	 * コンストラクタ。
	 * @param name 制約名称。
	 */
	public Constraint(final String name) {
		this(name, Constraint.DEFAULT_MESSAGE_KEY);
	}

	/**
	 * コンストラクタ。
	 * @param name 制約名称。
	 * @param msgkey 違反時のメッセージキー。
	 */
	public Constraint(final String name, final String msgkey) {
		this.constraintName = name;
		this.violationMessageKey = msgkey;
	}

	/**
	 * 制約名称を取得します。
	 * @return 制約名称。
	 */
	public String getConstraintName() {
		return constraintName;
	}

	/**
	 * 制約名称を設定します。
	 * @param constraintName 制約名称。
	 */
	public void setConstraintName(final String constraintName) {
		this.constraintName = constraintName;
	}

	/**
	 * 違反時のメッセージキーを取得します。
	 * @return メッセージキー。
	 */
	public String getViolationMessageKey() {
		return violationMessageKey;
	}

	/**
	 * 違反時のメッセージキーを取得します。
	 * @param violationMessageKey メッセージキー。
	 */
	public void setViolationMessageKey(final String violationMessageKey) {
		this.violationMessageKey = violationMessageKey;
	}


}
