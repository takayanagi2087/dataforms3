package jp.dataforms.fw.field.sqlfunc;

import jp.dataforms.fw.dao.sqlgen.SqlGenerator;
import jp.dataforms.fw.field.base.DateTimeField;
import jp.dataforms.fw.field.base.Field;
import jp.dataforms.fw.field.sqltype.TimestampField;
import lombok.Getter;

/**
 * 日付加算フィールド。
 */
public class TimestampAddField extends TimestampField implements DateTimeAddFunctionField {

	/**
	 * 抽出する日付フィールドの種類。
	 */
	@Getter
	private FieldType type = null;
	
	/**
	 * 抽出対象フィールド 。
	 */
	private DateTimeField<?> targetField = null;
	
	/**
	 * 加算する値。
	 */
	@Getter
	private Integer addValue = 0;
	/**
	 * コンストラクタ。
	 * @param id フィールドID。
	 * @param type 加算日付フィールドの種類。
	 * @param field 対象フィールド。
	 * @param addValue　加算する値。
	 */
	public TimestampAddField(final String id, final FieldType type, final TimestampField field, final int addValue) {
		super(id);
		this.type = type;
		this.targetField = field;
		this.addValue = addValue;
	}

	@Override
	public Field<?> getTargetField() {
		return this.targetField;
	}

	@Override
	public String getFunctionSql(SqlGenerator gen) {
		return gen.generateDateAddFunction(this);
	}

}
