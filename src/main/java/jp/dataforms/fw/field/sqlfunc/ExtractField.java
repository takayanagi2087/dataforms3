package jp.dataforms.fw.field.sqlfunc;

import jp.dataforms.fw.dao.sqlgen.SqlGenerator;
import jp.dataforms.fw.field.base.DateTimeField;
import jp.dataforms.fw.field.base.Field;
import jp.dataforms.fw.field.sqltype.IntegerField;
import lombok.Getter;

/**
 * 日付フィールド取得フィールド。
 */
public class ExtractField extends IntegerField implements DateFunctionField {

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
	 * コンストラクタ。
	 * @param id フィールドID。
	 * @param type 抽出する日付フィールドの種類。
	 * @param field 抽出対象フィールド。
	 */
	public ExtractField(final String id, final FieldType type, final DateTimeField<?> field) {
		super(id);
		this.type = type;
		this.targetField = field;
	}
	
	@Override
	public Field<?> getTargetField() {
		return this.targetField;
	}


	@Override
	public String getFunctionSql(SqlGenerator gen) {
		return gen.generateExtractFunction(this);
	}

}
