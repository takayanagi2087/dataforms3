package jp.dataforms.fw.field.common;

import java.util.ArrayList;
import java.util.List;

import jp.dataforms.fw.dao.sqldatatype.SqlClob;
import jp.dataforms.fw.dao.sqlgen.mysql.MysqlSqlGenerator;
import jp.dataforms.fw.dao.sqlgen.pgsql.PgsqlSqlGenerator;
import jp.dataforms.fw.field.base.Field;
import jp.dataforms.fw.util.JsonUtil;
import jp.dataforms.fw.util.StringUtil;

/**
 * 複数選択可能な選択肢フィールドクラス。
 * <pre>
 * {@code
 * 対応するHTMLのタグは複数選択の&lt;select&gt;と&lt;input type=&quot;checkbox&quot;&gt;になります。
 * Listはjson形式に変換し、CLOBに記録します。
 * }
 * </pre>
 * @param <TYPE> データ型。
 */
public class MultiSelectField<TYPE> extends SelectField<List<TYPE>> implements SqlClob, OptionField<TYPE> {
	/**
	 * Logger.
	 */
//	private static Logger logger = LogManager.getLogger(MultiSelectField.class);
	
	/**
	 * HTMLフィールドタイプ。
	 */
	public enum HtmlFieldType {
		/**
		 * チェックボックス。
		 */
		CHECKBOX,
		/**
		 * マルチ選択リスト。
		 */
		SELECT
	};

	/**
	 * Htmlフィールドタイプ。
	 */
	private HtmlFieldType htmlFieldType = HtmlFieldType.CHECKBOX;

	/**
	 * HTMLフィールドタイプを取得します。
	 * @return HTMLフィールドタイプ。
	 */
	public HtmlFieldType getHtmlFieldType() {
		return htmlFieldType;
	}

	/**
	 * HTMLフィールドタイプを設定します。
	 * <pre>
	 * このプロパティは開発ツールでHTMLを作成する際参照されるだけで、実行時には参照されません。
	 * </pre>
	 * @param htmlFieldType HTMLフィールドタイプ。
	 * @return 設定されたフィールド。
	 */
	public Field<?> setHtmlFieldType(final HtmlFieldType htmlFieldType) {
		this.htmlFieldType = htmlFieldType;
		return this;
	}

	/**
	 * コンストラクタ。
	 * @param id フィールドID。
	 */
	public MultiSelectField(final String id) {
		this(id, String.class);
		this.setDbDependentType(PgsqlSqlGenerator.DATABASE_PRODUCT_NAME, "text");
		this.setDbDependentType(MysqlSqlGenerator.DATABASE_PRODUCT_NAME, "longtext");
	}

	/**
	 * リスト要素のクラス。
	 */
	private Class<?> elementClass = null;
	
	/**
	 * コンストラクタ。
	 * @param id フィールドID。
	 * @param cls 。
	 */
	public MultiSelectField(final String id, final Class<?> cls) {
		super(id);
		this.setDbDependentType(PgsqlSqlGenerator.DATABASE_PRODUCT_NAME, "text");
		this.setDbDependentType(MysqlSqlGenerator.DATABASE_PRODUCT_NAME, "longtext");
		this.elementClass = cls;
	}
	

	private List<Object> convertListValue(final List<Object> list) {
		List<Object> ret = new ArrayList<Object>();
		if (list != null) {
			for (Object str: list) {
				if (String.class.isAssignableFrom(this.elementClass)) {
					ret.add(str);
				} else if (Short.class.isAssignableFrom(this.elementClass)) {
					Short v = Short.parseShort(str.toString());
					ret.add(v);
				} else if (Integer.class.isAssignableFrom(this.elementClass)) {
					Integer v = Integer.parseInt(str.toString());
					ret.add(v);
				} else if (Long.class.isAssignableFrom(this.elementClass)) {
					Long v = Long.parseLong(str.toString());
					ret.add(v);
				}
			}
		}
		return ret;
	}


	@SuppressWarnings("unchecked")
	@Override
	public void setClientValue(final Object v) {
		if (v instanceof String) {
			// 選択件数が1件の場合、文字列で渡ったてくるので、Listを作成します。
			List<Object> list = new ArrayList<Object>();
			list.add(v);
			this.setValue((List<TYPE>) this.convertListValue(list));
		} else {
			List<Object> list = (List<Object>) v;
			this.setValue((List<TYPE>) this.convertListValue(list));
		}
	}

	/**
	 * {@inheritDoc}
	 * <pre>
	 * DBに保存する場合json形式に変換します。
	 * </pre>
	 */
	@Override
	public Object getDBValue() {
		String ret = null;
		if (!StringUtil.isBlank(this.getValue())) {
			ret = JsonUtil.encode(this.getValue(), true);
		}
		return ret;
	}

	/**
	 * {@inheritDoc}
	 * <pre>
	 * DBにから取得したjson形式の文字列を、リスト形式に変換します。
	 * </pre>
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void setDBValue(final Object value) {
		List<TYPE> list = new ArrayList<TYPE>();
		if (!StringUtil.isBlank(value)) {
			list = (List<TYPE>) JsonUtil.decode(value.toString(), ArrayList.class);
		}
		super.setValue(list);
	}
}

