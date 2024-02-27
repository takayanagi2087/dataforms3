package jp.dataforms.fw.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jp.dataforms.fw.util.StringUtil;

/**
 * 外部キー制約クラス。
 *
 */
public class ForeignKey extends Constraint {

	/**
	 * Logger.
	 */
	private static Logger logger = LogManager.getLogger(ForeignKey.class);

	/**
	 * 参照元テーブルのインスタンス。
	 */
	private Table table = null;
	/**
	 * 参照元テーブルのフィールドIDリスト。
	 */
	private String[] fieldIdList = null;
	/**
	 * 参照先テーブルのクラス。
	 */
	private Class<? extends Table> referenceTableClass = null;
	/**
	 * 参照フィールドIDリスト。
	 */
	private String[] referenceFieldIdList = null;


	/**
	 * コンストラクタ。
	 * @param constraintName 制約名。
	 * @param fieldIdList フィールドIDのリスト。
	 * @param refTableClass 参照テーブルクラス。
	 * @param refFieldIdList 参照フィールドIDリスト。
	 * @param msgkey メッセージキー。
	 */
	public ForeignKey(final String constraintName, final String [] fieldIdList, final Class<? extends Table> refTableClass, final String[]  refFieldIdList, final String msgkey) {
		super(constraintName, msgkey);
		this.fieldIdList = fieldIdList;
		this.referenceTableClass = refTableClass;
		this.referenceFieldIdList = refFieldIdList;
	}



	/**
	 * コンストラクタ。
	 * @param constraintName 制約名。
	 * @param fieldIdList フィールドIDのリスト。
	 * @param refTableClass 参照テーブルクラス。
	 * @param refFieldIdList 参照フィールドIDリスト。
	 */
	public ForeignKey(final String constraintName, final String [] fieldIdList, final Class<? extends Table> refTableClass, final String[]  refFieldIdList) {
		this(constraintName, fieldIdList, refTableClass, refFieldIdList, Constraint.DEFAULT_MESSAGE_KEY);
	}

	/**
	 * コンストラクタ。
	 * <pre>
	 * 参照元フィールドIDのリストと参照先フィールドIDのリストが同じ場合のコンストラクタ。
	 * </pre>
	 * @param constraintName 制約名。
	 * @param fieldIdList フィールドIDのリスト。
	 * @param refTableClass 参照テーブルクラス。
	 */
	public ForeignKey(final String constraintName, final String [] fieldIdList, final Class<? extends Table> refTableClass) {
		this(constraintName, fieldIdList, refTableClass, fieldIdList);
	}

	/**
	 * コンストラクタ。
	 * <pre>
	 * フィールド数が1件の場合。
	 * </pre>
	 * @param constraintName 制約名。
	 * @param fieldId フィールドID。
	 * @param refTableClass 参照先テーブルクラス。
	 * @param refFieldId 参照フィールドID。
	 * @param msgkey メッセージキー。
	 */
	public ForeignKey(final String constraintName, final String fieldId, final Class<? extends Table> refTableClass, final String refFieldId, final String msgkey) {
		super(constraintName, msgkey);
		String [] fieldIdList = new String[1];
		fieldIdList[0] = fieldId;
		this.fieldIdList = fieldIdList;
		this.referenceTableClass = refTableClass;
		String [] refFieldIdList = new String[1];
		refFieldIdList[0] = refFieldId;
		this.referenceFieldIdList = refFieldIdList;
	}

	/**
	 * コンストラクタ。
	 * <pre>
	 * フィールド数が1件の場合。
	 * </pre>
	 * @param constraintName 制約名。
	 * @param fieldId フィールドID。
	 * @param refTableClass 参照先テーブルクラス。
	 * @param refFieldId 参照フィールドID。
	 */
	public ForeignKey(final String constraintName, final String fieldId, final Class<? extends Table> refTableClass, final String refFieldId) {
		this(constraintName, fieldId, refTableClass, refFieldId, Constraint.DEFAULT_MESSAGE_KEY);
	}

	/**
	 * コンストラクタ。
	 * <pre>
	 * フィールド数が1件でかつ参照先フィールドIDと同じ場合。
	 * </pre>
	 * @param constraintName 制約名。
	 * @param fieldId フィールドID。
	 * @param refTableClass 参照先テーブルクラス。
	 */
	public ForeignKey(final String constraintName, final String fieldId, final Class<? extends Table> refTableClass) {
		this(constraintName, fieldId, refTableClass, fieldId);
	}

	/**
	 * 参照元テーブルを取得します。
	 * @return テーブル。
	 */
	public Table getTable() {
		return table;
	}

	/**
	 * 参照元テーブルを設定します。
	 * @param table テーブル。
	 */
	public void setTable(final Table table) {
		this.table = table;
	}

	/**
	 * 参照元フィールドIDのリストを取得します。
	 * @return リンク元フィールドIDのリスト。
	 */
	public String[] getFieldIdList() {
		return fieldIdList;
	}

	/**
	 * 参照元フィールドIDのリストを設定します。
	 * @param fieldIdList リンク元フィールドIDのリスト。
	 */
	public void setFieldIdList(final String[] fieldIdList) {
		this.fieldIdList = fieldIdList;
	}

	/**
	 * 参照先テーブルのクラスを取得します。
	 * @return 参照テーブルのクラス。
	 */
	public Class<? extends Table> getReferenceTableClass() {
		return referenceTableClass;
	}

	/**
	 * 参照先テーブルのクラスを設定します。
	 * @param referenceTableClass 参照テーブルのクラス。
	 */
	public void setReferenceTableClass(final Class<? extends Table> referenceTableClass) {
		this.referenceTableClass = referenceTableClass;
	}

	/**
	 * 参照先フィールドIDのリストを取得します。
	 * @return 参照先フィールドID。
	 */
	public String[] getReferenceFieldIdList() {
		return referenceFieldIdList;
	}

	/**
	 * 参照先フィールドIDのリストを設定します。
	 * @param referenceFieldIdList 参照先フィールドIDのリスト。
	 */
	public void setReferenceFieldIdList(final String[] referenceFieldIdList) {
		this.referenceFieldIdList = referenceFieldIdList;
	}

	/**
	 * 外部キーのフィールドリストを取得します。
	 * @param constname 外部キー制約名称。
	 * @param dbfklist テーブルに付随するDBの外部キー情報。
	 * @return 外部キーのフィールドリスト。
	 */
	private List<Map<String, Object>> getForeignKeyFieldList(final String constname, final List<Map<String, Object>> dbfklist) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		for (Map<String, Object> m: dbfklist) {
			String fkname = (String) m.get("fkName");
			if (constname.equalsIgnoreCase(fkname)) {
				list.add(m);
			}
		}
		return list;
	}

	/**
	 * ソース上の外部キーとDB上の外部キーの違いを検出します。
	 * @param dbfklist DB上の外部キー。
	 * @return 異なっている場合false
	 * @throws Exception 例外。
	 */
	public boolean structureAccords(final List<Map<String, Object>> dbfklist) throws Exception {
		String constname = StringUtil.camelToSnake(this.getConstraintName());
		String tblname = this.getReferenceTableClass().getDeclaredConstructor().newInstance().getTableName();
		String[] flist = this.getFieldIdList();
		String[] rflist = this.getReferenceFieldIdList();

		List<Map<String, Object>> fldlist = this.getForeignKeyFieldList(constname, dbfklist);
		logger.debug(() -> "fldlist.size()=" + fldlist.size());
		if (flist.length != fldlist.size()) {
			logger.info(() -> "foreign key " + constname + " field count missmatch.");
			return false;
		}
		if (rflist.length != fldlist.size()) {
			logger.info(() -> "foreign key " + constname + " field count missmatch.");
			return false;
		}
		int fidx = 0;
		for (Map<String, Object> m: fldlist) {
			String fld = StringUtil.camelToSnake(flist[fidx]);
			String rfld = StringUtil.camelToSnake(rflist[fidx]);
			fidx++;
			String pktable = (String) m.get("pktableName");
			String fkcol = (String) m.get("fkcolumnName");
			String pkcol = (String) m.get("pkcolumnName");
			if (!tblname.equalsIgnoreCase(pktable)) {
				logger.info(() -> "foreign key " + constname + " tabe name missmatch.(" + tblname + "," + pktable +  ")");
				return false;
			}
			if (!fld.equalsIgnoreCase(fkcol)) {
				logger.info(() -> "foreign key " + constname + " fk column name missmatch.(" + fld + "," + fkcol +  ")");
				return false;
			}
			if (!rfld.equalsIgnoreCase(pkcol)) {
				logger.info(() -> "foreign key " + constname + " pk column name missmatch.(" + rfld + "," + pkcol +  ")");
				return false;
			}
		}
		return true;
	}
}

