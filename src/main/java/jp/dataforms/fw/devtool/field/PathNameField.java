package jp.dataforms.fw.devtool.field;

import jp.dataforms.fw.field.sqltype.VarcharField;

/**
 * ファイル名やディレクトリ名等のパスフィールドクラス。
 *
 */
public class PathNameField extends VarcharField {
	/**
	 * コンストラクタ。
	 */
	public PathNameField() {
		super(null, 1024);
	}

	/**
	 * コンストラクタ。
	 * @param id フィールドID。
	 */
	public PathNameField(final String id) {
		super(id, 1024);
	}
}
