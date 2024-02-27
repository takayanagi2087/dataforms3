package jp.dataforms.fw.devtool.field;

import jp.dataforms.fw.dao.QuerySetDao;

/**
 * テーブルクラス名フィールドクラス。
 *
 */
public class DaoClassNameField extends SimpleClassNameField {
	/**
	 * フィールドコメント。
	 */
	private static final String COMMENT = "DAOクラス名";
	/**
	 * コンストラクタ。
	 */
	public DaoClassNameField() {
		this(null);
	}
	/**
	 * コンストラクタ。
	 * @param id フィールドID。
	 */
	public DaoClassNameField(final String id) {
		super(id);
		this.addBaseClass(QuerySetDao.class);
		this.setComment(COMMENT);
		this.setAutocomplete(false);
	}

	@Override
	protected String getClassNameSuffix() {
		return "Dao";
	}
}
