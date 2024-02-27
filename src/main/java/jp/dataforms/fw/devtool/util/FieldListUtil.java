package jp.dataforms.fw.devtool.util;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import jp.dataforms.fw.dao.file.FileObject;
import jp.dataforms.fw.dao.file.ImageData;
import jp.dataforms.fw.dao.file.VideoData;
import jp.dataforms.fw.dao.sqldatatype.SqlBigint;
import jp.dataforms.fw.dao.sqldatatype.SqlChar;
import jp.dataforms.fw.dao.sqldatatype.SqlDate;
import jp.dataforms.fw.dao.sqldatatype.SqlDouble;
import jp.dataforms.fw.dao.sqldatatype.SqlInteger;
import jp.dataforms.fw.dao.sqldatatype.SqlNumeric;
import jp.dataforms.fw.dao.sqldatatype.SqlSmallint;
import jp.dataforms.fw.dao.sqldatatype.SqlTime;
import jp.dataforms.fw.dao.sqldatatype.SqlTimestamp;
import jp.dataforms.fw.dao.sqldatatype.SqlVarchar;
import jp.dataforms.fw.field.base.Field;
import jp.dataforms.fw.field.common.CharSingleSelectField;
import jp.dataforms.fw.field.common.FileObjectField;
import jp.dataforms.fw.field.common.ImageField;
import jp.dataforms.fw.field.common.MultiSelectField;
import jp.dataforms.fw.field.common.VideoField;
import jp.dataforms.fw.field.sqltype.BigintField;
import jp.dataforms.fw.field.sqltype.CharField;
import jp.dataforms.fw.field.sqltype.ClobField;
import jp.dataforms.fw.field.sqltype.DateField;
import jp.dataforms.fw.field.sqltype.IntegerField;
import jp.dataforms.fw.field.sqltype.NumericField;
import jp.dataforms.fw.field.sqltype.SmallintField;
import jp.dataforms.fw.field.sqltype.TimeField;
import jp.dataforms.fw.field.sqltype.TimestampField;
import jp.dataforms.fw.field.sqltype.VarcharField;
import jp.dataforms.fw.util.ImportUtil;
import jp.dataforms.fw.util.StringUtil;

/**
 * フィールドリスト、ユーティリティ。
 *
 */
public final class FieldListUtil {
	/**
	 * コンストラクタ。
	 */
	private FieldListUtil() {

	}

	/**
	 * フィールドID取得関数インターフェース。
	 *
	 */
	@FunctionalInterface
	public interface GetFieldIdFunctionalInterface {
		/**
		 * 指定されたマップからフィールドIDを取得します。
		 * @param m マップ。
		 * @return フィールドID。
		 */
		String getFieldId(final Map<String, Object> m);
	}


	/**
	 * フィールドIdの定数を展開します。
	 * @param list フィールドリスト。
	 * @param func フィールドID取得関数インターフェース。
	 * @return フィールドIDの定数値。
	 */
	public static String generateFieldIdConstant(final List<Map<String, Object>> list, final GetFieldIdFunctionalInterface func) {
		StringBuilder sb = new StringBuilder();
		for (Map<String, Object> m: list) {
			String fieldId = func.getFieldId(m);
			String comment = (String) m.get("comment");
			sb.append("\t\t/** " + comment + "のフィールドID。 */\n");
			sb.append("\t\tpublic static final String ID_");
			sb.append(StringUtil.camelToUpperCaseSnake(fieldId));
			sb.append(" = \"");
			sb.append(fieldId);
			sb.append("\";\n");
		}
		return sb.toString();
	}


	/**
	 * フィールドID取得関数インターフェース。
	 *
	 */
	@FunctionalInterface
	public interface GetClassNameFunctionalInterface {
		/**
		 * 指定されたマップからフィールドIDを取得します。
		 * @param m マップ。
		 * @return フィールドID。
		 */
		String getClassName(final Map<String, Object> m);
	}



	/**
	 * フィールドに対応する値の型を返します。
	 * @param field フィールド。
	 * @return 値の型。
	 */
	private static Class<?> getFieldValueType(final Class<?> field) {
		Class<?> ret = Object.class;
		if (FileObjectField.class.isAssignableFrom(field)) {
			ret = FileObject.class;
		} else if (ImageField.class.isAssignableFrom(field)) {
			ret = ImageData.class;
		} else if (VideoField.class.isAssignableFrom(field)) {
			ret = VideoData.class;
		} else 	if (SqlVarchar.class.isAssignableFrom(field)
			|| SqlChar.class.isAssignableFrom(field)
			|| CharField.class.isAssignableFrom(field)
			|| VarcharField.class.isAssignableFrom(field)
			|| ClobField.class.isAssignableFrom(field)
			|| CharSingleSelectField.class.isAssignableFrom(field)) {
			ret = String.class;
		} else if (DateField.class.isAssignableFrom(field)) {
			ret = java.sql.Date.class;
		} else if (TimeField.class.isAssignableFrom(field)) {
			ret = java.sql.Time.class;
		} else if (TimestampField.class.isAssignableFrom(field)) {
			ret = java.sql.Timestamp.class;
		} else if (SmallintField.class.isAssignableFrom(field)) {
			ret = Short.class;
		} else if (IntegerField.class.isAssignableFrom(field)) {
			ret = Integer.class;
		} else if (BigintField.class.isAssignableFrom(field)) {
			ret = Long.class;
		} else if (NumericField.class.isAssignableFrom(field)) {
			ret = BigDecimal.class;
		} else if (MultiSelectField.class.isAssignableFrom(field)) {
			ret = List.class;
		} else if (SqlBigint.class.isAssignableFrom(field)) { //
			ret = Long.class;
		} else if (SqlInteger.class.isAssignableFrom(field)) {
			ret = Integer.class;
		} else if (SqlSmallint.class.isAssignableFrom(field)) {
			ret = Short.class;
		} else if (SqlDouble.class.isAssignableFrom(field)) {
			ret = Double.class;
		} else if (SqlNumeric.class.isAssignableFrom(field)) {
			ret = BigDecimal.class;
		} else if (SqlDate.class.isAssignableFrom(field)) {
			ret = java.sql.Date.class;
		} else if (SqlTime.class.isAssignableFrom(field)) {
			ret = java.sql.Time.class;
		} else if (SqlTimestamp.class.isAssignableFrom(field)) {
			ret = java.sql.Timestamp.class;
		}
		return ret;
	}

	/**
	 * フィールドのgetter/setterを作成します。。
	 * @param list フィールドリスト。
	 * @param func フィールドID取得関数インターフェース。
	 * @param cfunc クラス名取得関数インターフェース。
	 * @param implist インポートリストユーティリティ。
	 * @return フィールドのgetter/setter。
	 * @throws Exception 例外。
	 */
	public static String generateFieldValueGetterSetter(final List<Map<String, Object>> list, final GetFieldIdFunctionalInterface func, final GetClassNameFunctionalInterface cfunc, final ImportUtil implist) throws Exception {
		StringBuilder sb = new StringBuilder();
		for (Map<String, Object> m: list) {
			String fieldClassName = cfunc.getClassName(m);
			@SuppressWarnings("unchecked")
			Class<? extends Field<?>> cls = (Class<? extends Field<?>>) Class.forName(fieldClassName);
			Class<?> valueType = FieldListUtil.getFieldValueType(cls);
			String fieldId = func.getFieldId(m);
			String uFieldId = StringUtil.firstLetterToUpperCase(fieldId);
			String comment = (String) m.get("comment");
			if (StringUtil.isBlank(comment)) {
				comment = Field.newFieldInstance(cls).getComment();
			}
			String vtype = valueType.getName();
			if ("java.util.List".equals(vtype)) {
				vtype = "java.util.List<?>";
			}
			sb.append("\t\t/**\n");
			sb.append("\t\t * " + comment + "を取得します。\n");
			sb.append("\t\t * @return " + comment + "。\n");
			sb.append("\t\t */\n");
			sb.append("\t\tpublic " + vtype + " get" + uFieldId+ "() {\n");
			if ("java.lang.Short".equals(valueType.getName())) {
				implist.add("dataforms.util.NumberUtil");
				sb.append("\t\t\treturn NumberUtil.shortValueObject(this.getMap().get(Entity.ID_" + StringUtil.camelToUpperCaseSnake(fieldId) + "));\n");
			} else if ("java.lang.Integer".equals(valueType.getName())) {
				implist.add("dataforms.util.NumberUtil");
				sb.append("\t\t\treturn NumberUtil.integerValueObject(this.getMap().get(Entity.ID_" + StringUtil.camelToUpperCaseSnake(fieldId) + "));\n");
			} else if ("java.lang.Long".equals(valueType.getName())) {
				implist.add("dataforms.util.NumberUtil");
				sb.append("\t\t\treturn NumberUtil.longValueObject(this.getMap().get(Entity.ID_" + StringUtil.camelToUpperCaseSnake(fieldId) + "));\n");
			} else {
				sb.append("\t\t\treturn (" + vtype + ") this.getMap().get(Entity.ID_" + StringUtil.camelToUpperCaseSnake(fieldId) + ");\n");
			}
			sb.append("\t\t}\n\n");

			sb.append("\t\t/**\n");
			sb.append("\t\t * " + comment + "を設定します。\n");
			sb.append("\t\t * @param " + fieldId + " " + comment + "。\n");
			sb.append("\t\t */\n");
			sb.append("\t\tpublic void set" + uFieldId+ "(final " + vtype + " " + fieldId + ") {\n");
			sb.append("\t\t\tthis.getMap().put(Entity.ID_" + StringUtil.camelToUpperCaseSnake(fieldId) + ", " + fieldId + ");\n");
			sb.append("\t\t}\n\n");
		}
		return sb.toString();
	}

	/*
	private static Class<? extends Field<?>> getFieldClass(final String fieldClassName) {
		try {
			@SuppressWarnings("unchecked")
			Class<? extends Field<?>> cls = (Class<? extends Field<?>>) Class.forName(fieldClassName);
			return cls;
		} catch (Exception ex) {
			return null;
		}

	}
	*/

	/**
	 * フィールドのインスタンスのgetterを展開します。
	 * @param list フィールドリスト。
	 * @param func フィールドID取得関数インターフェース。
	 * @return フィールドIDの定数値。
	 * @throws Exception 例外。
	 */
	public static String generateFieldGetter(final List<Map<String, Object>> list, final GetFieldIdFunctionalInterface func) throws Exception {
		return FieldListUtil.generateFieldGetter(list, func, null);
	}

	/**
	 * フィールドのインスタンスのgetterを展開します。
	 * @param list フィールドリスト。
	 * @param func フィールドID取得関数インターフェース。
	 * @param implist インポートリスト。
	 * @return フィールドIDの定数値。
	 * @throws Exception 例外。
	 */
	public static String generateFieldGetter(final List<Map<String, Object>> list, final GetFieldIdFunctionalInterface func, final ImportUtil implist) throws Exception {
		StringBuilder sb = new StringBuilder();
		for (Map<String, Object> m: list) {
			String fieldId = func.getFieldId(m);
			String uFieldId = StringUtil.firstLetterToUpperCase(fieldId);
			String fieldClassName = (String) m.get("fieldClassName");
			String fsel = (String) m.get("sel");
			if ("dataforms.field.sqlfunc.CountField".equals(fsel)) {
				BigintField bifld = new BigintField(fieldId);
				fieldClassName = bifld.getClass().getName();
			}
			if (implist != null) {
				implist.add(fieldClassName);
			}
			String fieldClassSimpleName = fieldClassName;
			int idx = fieldClassSimpleName.lastIndexOf(".");
			if (idx >= 0) {
				fieldClassSimpleName = fieldClassSimpleName.substring(idx + 1);
			}
			String comment = (String) m.get("comment");
			sb.append("\t/**\n");
			sb.append("\t * " + comment + "フィールドを取得します。\n");
			sb.append("\t * @return " + comment + "フィールド。\n");
			sb.append("\t */\n");
			sb.append("\tpublic " + fieldClassSimpleName + " get" + uFieldId + "Field() {\n");
			sb.append("\t\treturn (" + fieldClassSimpleName + ") this.getField(Entity.ID_" + StringUtil.camelToUpperCaseSnake(fieldId) + ");\n");
			sb.append("\t}\n\n");
		}
		return sb.toString();
	}


}
