package jp.dataforms.fw.devtool.field;

import java.lang.reflect.Modifier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jp.dataforms.fw.dao.Query;

/**
 * 問合せクラス名フィールドクラス。
 *
 */
public class QueryClassNameField extends SimpleClassNameField {
	/**
	 * Log.
	 */
	private static Logger log = LogManager.getLogger(QueryClassNameField.class);
	/**
	 * フィールドコメント。
	 */
	private static final String COMMENT = "問合せクラス名";
	/**
	 * コンストラクタ。
	 */
	public QueryClassNameField() {
		this(null);
	}
	/**
	 * コンストラクタ。
	 * @param id フィールドID。
	 */
	public QueryClassNameField(final String id) {
		super(id);
		this.addBaseClass(Query.class);
		this.setComment(COMMENT);
		this.setAutocomplete(true);
	}

	@Override
	protected boolean isExcetionClass(final String classname) throws Exception {
		log.debug("classname=" + classname);
		@SuppressWarnings("unchecked")
		Class<? extends Query> c = (Class<? extends Query>) Class.forName(classname);
		if ((c.getModifiers() & Modifier.ABSTRACT) != 0) {
			return true;
		}
		/*if (c.getName().indexOf("$") > 0) {
			// インナークラスは除外。
			return true;
		}*/
		if ((c.getModifiers() & Modifier.PUBLIC) == 0) {
			return true;
		}
		try {
			c.getConstructor();
		} catch (NoSuchMethodException e) {
			// デフォルトコンストラクタが存在しない場合はヒットさせない。
			return true;
		}
		boolean ret = super.isExcetionClass(classname);
		return ret;
	}

	@Override
	protected String getClassNameSuffix() {
		return "((Query)|(Table))";
	}

	@Override
	protected void onBind() {
		super.onBind();
	}

}
