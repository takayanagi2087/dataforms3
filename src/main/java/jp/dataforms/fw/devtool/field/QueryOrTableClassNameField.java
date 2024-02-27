package jp.dataforms.fw.devtool.field;

import jp.dataforms.fw.dao.Table;
import jp.dataforms.fw.devtool.validator.ClassNameValidator;

/**
 * テーブルまたは問合せクラス名フィールド。
 *
 */
public class QueryOrTableClassNameField extends QueryClassNameField {

	/**
	 * コンストラクタ。
	 */
	public QueryOrTableClassNameField() {
		this(null);
	}

	/**
	 * コンストラクタ。
	 * @param id フィールドID。
	 */
	public QueryOrTableClassNameField(final String id) {
		super(id);
		this.addBaseClass(Table.class);
	}

	@Override
	protected void onBind() {
		super.onBind();
		this.removeValidator(ClassNameValidator.class);
		this.addValidator(new ClassNameValidator("((Query)|(Table))"));
	}

}
