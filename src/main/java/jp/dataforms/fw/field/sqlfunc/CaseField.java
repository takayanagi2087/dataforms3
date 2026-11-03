package jp.dataforms.fw.field.sqlfunc;

import java.util.ArrayList;
import java.util.List;

import jp.dataforms.fw.field.base.Field;
import lombok.Getter;
import lombok.Setter;


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
	 * 条件フィールドクラス。
	 */
	private Field<?> condField = null;
	
	/**
	 * 条件フィールド式。。
	 */
	@Getter
	@Setter
	private String condExp = null;
	
	/**
	 * WhenThenのリスト。
	 */
	private List<WhenThen> whenThenList = null;

	/**
	 * コンストラクタ　単純CASE式。
	 * @param field ターゲットフィールド。
	 * @param condExp 条件フィールド。
	 * @param condList 条件配列。
	 */
	public CaseField(final Field<?> field, final String condExp, final WhenThen... condList) {
		super(field, null);
		this.condExp = condExp;
		this.condField = null;
		this.whenThenList = new ArrayList<WhenThen>();
		for (WhenThen wt: condList) {
			this.whenThenList.add(wt);
		}
//		this.setSql(this.getCaseFormula(condField, condList));
	}
	
	
	/**
	 * コンストラクタ　単純CASE式。
	 * @param field ターゲットフィールド。
	 * @param condField 条件ティールド。
	 * @param condList 条件配列。
	 */
	public CaseField(final Field<?> field, final Field<?> condField, final WhenThen... condList) {
		super(field, null);
		this.condExp = null;
		this.condField = condField;
		this.whenThenList = new ArrayList<WhenThen>();
		for (WhenThen wt: condList) {
			this.whenThenList.add(wt);
		}
//		this.setSql(this.getCaseFormula(condField, condList));
	}
	
	/**
	 * コンストラクタ　検索CASE式。
	 * @param field ターゲットフィールド。
	 * @param condList 条件配列。
	 */
	public CaseField(final Field<?> field, final WhenThen... condList) {
		super(field, null);
		this.whenThenList = new ArrayList<WhenThen>();
		for (WhenThen wt: condList) {
			this.whenThenList.add(wt);
		}
	}

	/**
	 * WhenThenを追加します。
	 * @param wt WhenThen。 
	 */
	public void addWhenThen(final WhenThen wt) {
		if (this.whenThenList == null) {
			this.whenThenList = new ArrayList<WhenThen>();
		}
		this.whenThenList.add(wt);
	}
	
	/**
	 * 単純CASE式のSQLを生成します。
	 * @param strCondField　条件フィールド(文字列)。
	 * @param condField　条件フィールド。
	 * @param condList 条件情報。
	 * @return 単純CASE式。　
	 */
	private String getCaseFormula(final String strCondField, final Field<?> condField, final List<WhenThen> condList) {
		StringBuilder sb = new StringBuilder();
		sb.append("case ");
		if (strCondField != null) {
			sb.append(strCondField);
		} else {
			sb.append(condField.getQueryColumnName());
		}
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
	private String getCaseFormula(final List<WhenThen> condList) {
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
		if (this.condField == null && this.condExp == null) {
			return this.getCaseFormula(this.whenThenList);
		} else {
			return this.getCaseFormula(this.condExp, this.condField, this.whenThenList);
		}
	}
}
