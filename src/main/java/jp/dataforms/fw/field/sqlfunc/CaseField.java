package jp.dataforms.fw.field.sqlfunc;

import jp.dataforms.fw.field.base.Field;


/**
 * Case文生成フィールド。
 */
public class CaseField extends SqlField {
	/**
	 * 検索Case式の条件を表すレコード。
	 * @param cond 条件値または条件式。
	 * <pre>
	 *  	nullの場合ELSE。
	 * </pre>
	 * @param value 値。
	 */
	public record WhenThen(String cond, String value) {}
	
	/**
	 * 条件フィールド。
	 */
	private Field<?> condField = null;
	
	/**
	 * WhenThenのリスト。
	 */
	private WhenThen[] whenThenList = null;

	
	/**
	 * コンストラクタ　単純CASE式。
	 * @param field ターゲットフィールド。
	 * @param condField 条件ティールド。
	 * @param condList 条件配列。
	 */
	public CaseField(final Field<?> field, final Field<?> condField, final WhenThen... condList) {
		super(field, null);
		this.condField = condField;
		this.whenThenList = condList;
//		this.setSql(this.getCaseFormula(condField, condList));
	}

	/**
	 * コンストラクタ　検索CASE式。
	 * @param field ターゲットフィールド。
	 * @param condList 条件配列。
	 */
	public CaseField(final Field<?> field, final WhenThen... condList) {
		super(field, null);
		this.whenThenList = condList;
	}

	
	/**
	 * 単純CASE式のSQLを生成します。
	 * @param condField　条件フィールド。
	 * @param condList 条件情報。
	 * @return 単純CASE式。　
	 */
	private String getCaseFormula(final Field<?> condField, final WhenThen... condList) {
		StringBuilder sb = new StringBuilder();
		sb.append("case ");
		sb.append(condField.getQueryColumnName());
		sb.append("\n");
		for (WhenThen th: condList) {
			if (th.cond() != null) {
				sb.append("\t when " + th.cond + " then " + th.value + "\n");
			} else {
				sb.append("\t else " + th.value + "\n");
			}
		}
		sb.append("end");
		return sb.toString();
	}
	
	/**
	 * 単純CASE式のSQLを生成します。
	 * @param condList 条件情報。
	 * @return 単純CASE式。　
	 */
	private String getCaseFormula(final WhenThen... condList) {
		StringBuilder sb = new StringBuilder();
		sb.append("case ");
		sb.append("\n");
		for (WhenThen th: condList) {
			if (th.cond() != null) {
				sb.append("\t when " + th.cond + " then " + th.value + "\n");
			} else {
				sb.append("\t else " + th.value + "\n");
			}
		}
		sb.append("end");
		return sb.toString();
	}

	@Override
	public String getSql() {
		if (this.condField == null) {
			return this.getCaseFormula(this.whenThenList);
		} else {
			return this.getCaseFormula(this.condField, this.whenThenList);
		}
	}
}
