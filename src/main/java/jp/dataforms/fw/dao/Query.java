package jp.dataforms.fw.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.dataforms.fw.dao.condition.ConditionExpression;
import jp.dataforms.fw.dao.condition.ConditionExpressionList.Operator;
import jp.dataforms.fw.field.base.Field;
import jp.dataforms.fw.field.base.FieldList;
import jp.dataforms.fw.field.sqlfunc.AliasField;
import jp.dataforms.fw.field.sqlfunc.GroupSummaryField;
import jp.dataforms.fw.field.sqlfunc.SqlField;
import jp.dataforms.fw.util.StringUtil;


/**
 * 問い合わせクラス。
 * <pre>
 * SQLのSELECT文に対応するクラスです。
 * DAOに対して、このクラスのインスタンスを渡すと、DBMSに対応したSqlGeneratorが
 * 適切なSQLを作成し、問い合わせを行います。
 * </pre>
 */
public class Query {
    /**
     * Logger.
     */
//    private static Logger logger = LogManager.getLogger(Query.class);

	/**
	 * フィールドリスト。
	 * <pre>
	 * 結果リストに格納するフィールドリスト.
	 * </pre>
	 */
	private FieldList fieldList = new FieldList();

	/**
	 * distinctフラグ。
	 * <pre>
	 * trueの場合生成するSQLは"select distinct ..."となります。
	 * </pre>
	 */
	private boolean distinct = false;

	/**
	 * 結合元のテーブル。
	 */
	private Table mainTable = null;
	/**
	 * 内部結合テーブルリスト。
	 */
	private TableList joinTableList = null;
	/**
	 * 左外部結合テーブルリスト。
	 */
	private TableList leftJoinTableList = null;
	/**
	 * 右外部結合テーブルリスト。
	 */
	private TableList rightJoinTableList = null;

	/**
	 * 問い合わせフォームフィールドリスト。
	 *
	 */
	private FieldList conditionFieldList = null;

	/**
	 * 検索条件式。
	 */
	private ConditionExpression conditionExpression = null;

	/**
	 * 問い合わせフォームの入力データ。
	 */
	private Map<String, Object> conditionData = null;

	/**
	 * 条件式の自動作成フラグ。
	 * <pre>
	 * trueの場合問い合わせフォームの情報から自動的に条件式を生成します。
	 * </pre>
	 */
	private boolean autoFieldCondition = true;

	/**
	 * ソートフィールドリスト。
	 */
	private FieldList orderByFieldList = null;

	/**
	 * 結合情報リスト。
	 */
	private List<JoinInfo> joinInfoList = null;

	/**
	 * フィールドとテーブル別名とのマップ。
	 */
	private Map<String, String> fieldTableAliasMap = null;

	/**
	 * 削除フラグの有効性。
	 */
	private boolean effectivenessOfDeleteFlag = true;

	/**
	 * 固定検索条件。
	 */
	private String condition = null;

	/**
	 * コメント。
	 */
	private String comment = null;
	
	/**
	 * 各条件の結合演算子を指定します。
	 */
	private Operator conditionOperator = Operator.AND;

	/**
	 * コンストラクタ。
	 */
	public Query() {

	}

	/**
	 * 条件オペレータを指定します。
	 * @param conditionOperator 条件オペレータ。
	 */
	public void setConditionOperator(final Operator conditionOperator) {
		this.conditionOperator = conditionOperator;
	}

	/**
	 * SQLの条件演算時を取得します。
	 * @return " and " または " or "を返します。
	 */
	public String getConditionOperatorSql() {
		if (this.conditionOperator == Operator.AND) {
			return " and ";
		} else {
			return " or ";
		}
	}
	
	/**
	 * コメントを取得します。
	 * @return コメント。
	 */
	public String getComment() {
		return comment;
	}

	/**
	 * コメント。
	 * @param comment コメント。
	 */
	public void setComment(final String comment) {
		this.comment = comment;
	}


	/**
	 * 問合せに対応するHtmlTableのIDを取得します。
	 * @return 問合せに対応するHtmlTableのID。
	 */
	public String getListId() {
		Table mt = this.getMainTable();
		String tableName = mt.getTableName();
		String tid = StringUtil.snakeToCamel(tableName) + "List";
		return tid;
	}

	/**
	 * フィールドリストを取得します。
	 * @return フィールドリスト。
	 */
	public FieldList getFieldList() {
		return fieldList;
	}


	/**
	 * フィールドリストを設定します。
	 * @param fieldList フィールドリスト。
	 */
	public void setFieldList(final FieldList fieldList) {
		this.fieldList = fieldList;
	}

	/**
	 * 指定されたIDに対応するフィールドを取得します。
	 * @param id フィールドID。
	 * @return フィールド。
	 */
	public Field<?> getField(final String id) {
		Field<?> field = this.fieldList.get(id);
		if (field instanceof GroupSummaryField) {
			GroupSummaryField<?> f = (GroupSummaryField<?>) field;
			return f.getTargetField();
		} else if (field instanceof SqlField) {
			SqlField f = (SqlField) field;
			return f.getTargetField();
		} else {
			return field;
		}
	}

	/**
	 * 結合元のテーブルを取得します。
	 * @return 結合元のテーブル。
	 */
	public Table getMainTable() {
		return mainTable;
	}

	/**
	 * 結合元のテーブルを設定します。
	 * @param mainTable 結合元のテーブル。
	 */
	public void setMainTable(final Table mainTable) {
		this.mainTable = mainTable;
	}


	/**
	 * テーブル結合情報。
	 *
	 */
	public static class JoinInfo {
		/**
		 * 内部結合。
		 */
		public static final String INNER_JOIN = " inner join ";
		/**
		 * 左結合。
		 */
		public static final String LEFT_JOIN = " left join ";
		/**
		 * 右結合。
		 */
		public static final String RIGHT_JOIN = " right join ";

		/**
		 * 結合タイプ。
		 */
		private String joinType = null;
		/**
		 * 結合するテーブル。
		 */
		private Table joinTable = null;

		/**
		 * 結合条件関数インターフェース。
		 */
		private JoinConditionInterface joinCondition = null;

		/**
		 * 生成された結合条件式。
		 */
		private String generatedCondition = null;


		/**
		 * コンストラクタ。
		 * @param joinType 結合タイプ。
		 * @param joinTable 結合するテーブル。
		 * @param joinCondition 結合条件関数インターフェース。
		 */
		public JoinInfo(final String joinType, final Table joinTable, final JoinConditionInterface joinCondition) {
			this.joinType = joinType;
			this.joinTable = joinTable;
			this.joinCondition = joinCondition;
		}

		/**
		 * 結合タイプを取得します。
		 * @return 結合タイプ。
		 */
		public String getJoinType() {
			return joinType;
		}

		/**
		 * 結合テーブルを取得します。
		 * @return 結合タイプ。
		 */
		public Table getJoinTable() {
			return joinTable;
		}

		/**
		 * 結合条件関数インターフェースを取得します。
		 * @return 結合条件関数インターフェース。
		 */
		public JoinConditionInterface getJoinCondition() {
			return joinCondition;
		}


		/**
		 * 生成された条件式を取得します。
		 * @return 生成された条件式。
		 */
		public String getGeneratedCondition() {
			return generatedCondition;
		}

		/**
		 * 生成された条件式を設定します。
		 * @param generatedCondition 生成された条件式。
		 */
		public void setGeneratedCondition(final String generatedCondition) {
			this.generatedCondition = generatedCondition;
		}

	}


	/**
	 * 結合情報リストを追加します。
	 * @param joinInfo 結合情報リスト。
	 */
	protected void addJoinInfo(final JoinInfo joinInfo) {
		if (this.joinInfoList == null) {
			this.joinInfoList = new ArrayList<JoinInfo>();
		}
		this.joinInfoList.add(joinInfo);
	}

	/**
	 * 内部結合を追加します。
	 * @param table 結合するテーブル。
	 * @param alias 別名。
	 * @param joinCondition 結合条件関数インターフェース。
	 */
	public void addInnerJoin(final Table table, final String alias, final JoinConditionInterface joinCondition) {
		if (alias != null) {
			table.setAlias(alias);
		}
		this.addJoinInfo(new JoinInfo(JoinInfo.INNER_JOIN, table, joinCondition));
	}

	/**
	 * 内部結合を追加します。
	 * @param table 結合するテーブル。
	 * @param joinCondition 結合条件関数インターフェース。
	 */
	public void addInnerJoin(final Table table, final JoinConditionInterface joinCondition) {
		this.addInnerJoin(table, null, joinCondition);
	}

	/**
	 * 内部結合を追加します。
	 * @param table 結合するテーブル。
	 * @param alias 別名。
	 */
	public void addInnerJoin(final Table table, final String alias) {
		this.addInnerJoin(table, alias, (JoinConditionInterface) null);
	}

	/**
	 * 内部結合を追加します。
	 * @param table 結合するテーブル。
	 */
	public void addInnerJoin(final Table table) {
		this.addInnerJoin(table, null, (JoinConditionInterface) null);
	}

	/**
	 * 左外部結合を追加します。
	 * @param table 結合するテーブル。
	 * @param alias 別名。
	 * @param joinCondition 結合条件関数インターフェース。
	 */
	public void addLeftJoin(final Table table, final String alias, final JoinConditionInterface joinCondition) {
		if (alias != null) {
			table.setAlias(alias);
		}
		this.addJoinInfo(new JoinInfo(JoinInfo.LEFT_JOIN, table, joinCondition));
	}

	/**
	 * 左外部結合を追加します。
	 * @param table 結合するテーブル。
	 * @param joinCondition 結合条件関数インターフェース。
	 */
	public void addLeftJoin(final Table table, final JoinConditionInterface joinCondition) {
		this.addLeftJoin(table, null, joinCondition);
	}

	/**
	 * 左外部結合を追加します。
	 * @param table 結合するテーブル。
	 * @param alias 別名。
	 */
	public void addLeftJoin(final Table table, final String alias) {
		this.addLeftJoin(table, alias, (JoinConditionInterface) null);
	}

	/**
	 * 左外部結合を追加します。
	 * @param table 結合するテーブル。
	 */
	public void addLeftJoin(final Table table) {
		this.addLeftJoin(table, null, (JoinConditionInterface) null);
	}



	/**
	 * 右外部結合を追加します。
	 * @param table 結合するテーブル。
	 * @param alias 別名。
	 * @param joinCondition 結合条件関数インターフェース。
	 */
	public void addRightJoin(final Table table, final String alias, final JoinConditionInterface joinCondition) {
		if (alias != null) {
			table.setAlias(alias);
		}
		this.addJoinInfo(new JoinInfo(JoinInfo.RIGHT_JOIN, table, joinCondition));
	}


	/**
	 * 右外部結合を追加します。
	 * @param table 結合するテーブル。
	 * @param joinCondition 結合条件関数インターフェース。
	 */
	public void addRightJoin(final Table table, final JoinConditionInterface joinCondition) {
		this.addRightJoin(table, null, joinCondition);
	}

	/**
	 * 右外部結合を追加します。
	 * @param table 結合するテーブル。
	 * @param alias 別名。
	 */
	public void addRightJoin(final Table table, final String alias) {
		this.addRightJoin(table, alias, (JoinConditionInterface) null);
	}


	/**
	 * 右外部結合を追加します。
	 * @param table 結合するテーブル。
	 */
	public void addRightJoin(final Table table) {
		this.addRightJoin(table, null, (JoinConditionInterface) null);
	}

	/**
	 * 旧形式のjoinTableListをjoinInfoListに展開する。
	 * @param type 結合タイプ。
	 * @param tlist テーブルリスト。
	 */
	private void addJoinTableList(final String type, final TableList tlist) {
		if (tlist != null) {
			for (Table t: tlist) {
				this.addJoinInfo(new JoinInfo(type, t, (JoinConditionInterface) null));
			}
		}
	}

	/**
	 * setXXXJoinTableListで設定した、結合情報をjoinInfoListに転記する。
	 */
	public void buildJoinInfoList() {
		this.addJoinTableList(JoinInfo.INNER_JOIN, this.joinTableList);
		this.addJoinTableList(JoinInfo.LEFT_JOIN, this.leftJoinTableList);
		this.addJoinTableList(JoinInfo.RIGHT_JOIN, this.rightJoinTableList);
		this.joinTableList = null;
		this.leftJoinTableList = null;
		this.rightJoinTableList = null;
	}

	/**
	 * 結合情報リストを取得します。
	 * @return 結合情報リスト。
	 */
	public List<JoinInfo> getJoinInfoList() {
		return joinInfoList;
	}

	/**
	 * 条件フィールドリストを設定します。
	 * <pre>
	 * 検索条件に使用するフィールドリスト指定します。
	 * このフィールドリストに存在しかつ問い合わせフォームの入力データが存在した場合、
	 * 検索の条件式を生成します。
	 * このリスト中のフィールドにはMatchTypeを指定し、検索条件式の生成を制御できます。
	 * </pre>
	 *　
	 *　<table>
	 *  	<caption>MatchType一覧</caption>
	 *		<thead>
	 *			<tr>
	 *				<th>条件タイプ</th><th>意味</th><th>生成条件式</th><th>データ編集</th>
	 *			</tr>
	 *		</thead>
	 *		<tbody>
	 *			<tr>
	 *				<td>FULL</td><td>完全一致</td><td>field_id = :field_id</td><td></td>
	 *				<td>PART</td><td>部分一致</td><td>field_id like :field_id</td><td>'%入力値%'</td>
	 *				<td>BEGIN</td><td>先頭一致</td><td>field_id like :field_id</td><td>'入力値%'</td>
	 *				<td>END</td><td>末尾一致</td><td>field_id like :field_id</td><td>'%入力値'</td>
	 *				<td>RANGE_FROM</td><td>範囲開始</td><td>field_id &gt;= :field_id</td><td></td>
	 *				<td>RANGE_TO</td><td>範囲終了</td><td>field_id &lt;= :field_id</td><td></td>
	 *			</tr>
	 *		</tbody>
	 *  </table>
	 *
	 * @param conditionFieldList 条件フィールドリスト。
	 */
	public void setConditionFieldList(final FieldList conditionFieldList) {
		this.conditionFieldList = conditionFieldList;
		// 無条件に条件式を生成するように、仮パラメータを設定.
		Map<String, Object> cond = new HashMap<String, Object>();
		for (Field<?> f : conditionFieldList) {
			cond.put(f.getId(), new Object());
		}
		this.setConditionData(cond);
	}


	/**
	 * 条件フィールドリストを取得します。
	 * @return 条件フィールドリスト。
	 */
	public FieldList getConditionFieldList() {
		return conditionFieldList;
	}


	/**
	 * 検索の条件式を設定します。
	 * <pre>
	 * conditonFieldListでは全てのフィールド条件はANDで処理されますが、
	 * conditionExpressionは複数の条件のAND ORの設定が可能になっています。
	 * conditonFieldListとconditionExpressionは排他利用です。
	 * </pre>
	 * @param conditionExpression 検索の条件式。
	 */
	public void setConditionExpression(final ConditionExpression conditionExpression) {
		this.conditionExpression = conditionExpression;
		this.conditionFieldList = conditionExpression.getFieldList();
	}


	/**
	 * 検索の条件式を取得します。
	 * @return 検索の条件式。
	 */
	public ConditionExpression getConditionExpression() {
		return conditionExpression;
	}

	/**
	 * 条件データを取得します。
	 * @return 問い合わせフォームの入力データ。
	 */
	public Map<String, Object> getConditionData() {
		return conditionData;
	}


	/**
	 * 条件データを設定します。
	 * @param conditionData 問い合わせフォームの入力データ。
	 */
	public void setConditionData(final Map<String, Object> conditionData) {
		this.conditionData = conditionData;
	}

	/**
	 * 条件式の自動作成フラグを取得します。
	 * @return 条件式の自動作成フラグ。
	 */
	public boolean isAutoFieldCondition() {
		return autoFieldCondition;
	}

	/**
	 * 条件式の自動作成フラグを設定します。
	 * @param autoFieldCondition 条件式の自動作成フラグ。
	 */
	public void setAutoFieldCondition(boolean autoFieldCondition) {
		this.autoFieldCondition = autoFieldCondition;
	}

	/**
	 * 固定検索条件を取得します。
	 * @return 固定検索条件。
	 */
	public String getCondition() {
		return condition;
	}

	/**
	 * 固定検索条件を取得します。
	 * @param condition 固定検索条件。
	 */
	public void setCondition(final String condition) {
		this.condition = condition;
	}

	/**
	 * フィールドとテーブル別名とのマップを設定します。
	 * @param fieldTableAliasMap フィールドとテーブル別名とのマップ。
	 */
	public void setFieldTableAliasMap(final Map<String, String> fieldTableAliasMap) {
		this.fieldTableAliasMap = fieldTableAliasMap;
	}

	/**
	 * マッチ対象のフィールドの構文を取得します。
	 * @param field フィールド。
	 * @return フィールド式。
	 */
	public String getMatchFieldSql(final Field<?> field) {
		String fid = field.getMatchFieldId();
		String t = null;
		if (field.getTable() != null) {
			t = field.getTable().getAlias();
		}
		if (t == null) {
			if (this.fieldTableAliasMap != null) {
				t = this.fieldTableAliasMap.get(fid);
			}
		}
		if (t != null) {
			String ret = t + "." + StringUtil.camelToSnake(fid);
			return ret;
		}
		return null;
	}

	/**
	 *
	 * 特殊な条件式生成メソッドです。
	 * <pre>
	 * 問合せの際の条件式に特殊なものを使用する場合、このメソッドオーバーライドし条件式を作成してください、
	 * 特殊な条件式を作成しない場合nullを返します。
	 * </pre>
	 *
	 * @param f フィールド。
	 * @param data 条件データ。
	 * @return 基本的にnullを返します。
	 */
	public String getConditonExpression(final Field<?> f, final Map<String, Object> data) {
		return null;
	}

	/**
	 * order byフィールドのSQLを生成します。
	 * @param field フィールドID。
	 * @return order by フィールドのSQL。
	 */
	public String getOrderByFieldSql(final Field<?> field) {
		StringBuilder sb = new StringBuilder();
		sb.append(field.getTable().getAlias());
		sb.append(".");
		if (field instanceof AliasField) {
			sb.append(StringUtil.camelToSnake(((AliasField) field).getTargetField().getId()));
		} else {
			sb.append(StringUtil.camelToSnake(field.getId()));
		}
		if (field.getSortOrder() == Field.SortOrder.ASC) {
			sb.append(" asc");
		} else {
			sb.append(" desc");
		}
		return sb.toString();
	}

	/**
	 * group byフィールドのSQLを生成します。
	 * @param field フィールドID。
	 * @return group by フィールドのSQL。
	 */
	public String getGroupByFieldSql(final Field<?> field) {
		StringBuilder sb = new StringBuilder();
		sb.append(field.getTable().getAlias());
		sb.append(".");
		if (field instanceof AliasField) {
			sb.append(StringUtil.camelToSnake(((AliasField) field).getTargetField().getId()));
		} else {
			sb.append(StringUtil.camelToSnake(field.getId()));
		}
		return sb.toString();
	}


	/**
	 * distinctフラグを取得します。
	 * @return distinctフラグ。
	 */
	public boolean isDistinct() {
		return distinct;
	}

	/**
	 * distinctフラグを設定します。
	 * @param distinct distinctフラグ。
	 */
	public void setDistinct(final boolean distinct) {
		this.distinct = distinct;
	}

	/**
	 * Group by対象のフィールドリストを取得します。
	 * @return Group by対象のフィールドリスト。
	 */
	public FieldList getGroupByFieldList() {
		FieldList ret = new FieldList();
		boolean flg = false;
		for (Field<?> f : this.fieldList) {
			if (f instanceof GroupSummaryField) {
				flg = true;
				break;
			}
		}
		if (flg) {
			for (Field<?> f : this.fieldList) {
				if (!(f instanceof GroupSummaryField)) {
					ret.add(f);
				}
			}
		}
		return ret;
	}

	/**
	 * ソートするフィールドリストを取得します。
	 * @return ソートするフィールドリスト。
	 */
	public FieldList getOrderByFieldList() {
		return orderByFieldList;
	}

	/**
	 * ソートするフィールドリストを設定します。
	 * @param orderByFieldList ソートするフィールドリスト。
	 */
	public void setOrderByFieldList(final FieldList orderByFieldList) {
		this.orderByFieldList = orderByFieldList;
	}

	/**
	 * 削除フラグの有効性を取得します。
	 * <pre>
	 * trueの場合delete_flag='0'の条件を自動生成します。
	 * </pre>
	 * @return 削除フラグの有効性.
	 */
	public boolean isEffectivenessOfDeleteFlag() {
		return effectivenessOfDeleteFlag;
	}

	/**
	 * 削除フラグの有効性を設定します。
	 * @param effectivenessOfDeleteFlag 有効の場合true。
	 */
	public void setEffectivenessOfDeleteFlag(final boolean effectivenessOfDeleteFlag) {
		this.effectivenessOfDeleteFlag = effectivenessOfDeleteFlag;
	}

	/**
	 * 副問合せに指定された条件フィールドリストを追加します。。
	 *
	 * @param flist 追加先のフィールドリスト。
	 * @param query 問合せ。
	 */
	protected void margeConditionFieldList(final FieldList flist, final Query query) {
		FieldList cflist = query.getConditionFieldList();
		if (cflist != null) {
			flist.marge(cflist);
		}
		if (query.getMainTable() instanceof SubQuery) {
			Query sq = ((SubQuery) query.getMainTable()).getQuery();
			this.margeConditionFieldList(flist, sq);
		}
		List<Query.JoinInfo> jlist = query.getJoinInfoList();
		if (jlist != null) {
			for (Query.JoinInfo ji: query.getJoinInfoList()) {
				if (ji.getJoinTable() instanceof SubQuery) {
					Query sq = ((SubQuery) ji.getJoinTable()).getQuery();
					this.margeConditionFieldList(flist, sq);
				}
			}
		}
	}

	/**
	 * 副問合せに指定された条件フィールドリストを取得します。
	 * @return 副問合せに指定された条件フィールドリスト。
	 */
	public FieldList getSubQueryConditionFieldList() {
		FieldList flist = new FieldList();
		this.margeConditionFieldList(flist, this);
		return flist;
	}


	/**
	 * 条件フィールドリストに対応する条件式を取得します。
	 * @param dao DAO。
	 * @param flist 条件フィールドリスト。
	 * @param p 条件パラメータ。
	 * @return 条件式。
	 */
	public String getWhereCondition(final Dao dao, final FieldList flist, final Map<String, Object> p) {
		return dao.getWhereCondition(this, flist, p);
	}


	/**
	 * サブクエリを作成します。
	 * @param alias 別名。
	 * @return サブクエリ。
	 */
	public SubQuery createSubQuery(final String alias) {
		SubQuery ret = new SubQuery(this);
		if (alias != null) {
			ret.setAlias(alias);
		}
		return ret;
	}

	/**
	 * サブクエリを作成します。
	 * @return サブクエリ。
	 */
	public SubQuery createSubQuery() {
		return this.createSubQuery(null);
	}


	/**
	 * UNION 情報。
	 */
	public static class UnionInfo {
		/**
		 * ALLフラグ。
		 */
		private boolean allFlag = false;
		/**
		 * 問合せ。
		 */
		private Query query = null;

		/**
		 * コンストラクタ。
		 * @param all UNION ALLフラグ。
		 * @param query 問合せ。
		 */
		public UnionInfo(final Query query, final boolean all) {
			this.query = query;
			this.allFlag = all;
		}

		/**
		 * UNION ALLフラグを取得します。
		 * @return union allの場合true。
		 */
		public boolean isAllFlag() {
			return allFlag;
		}

		/**
		 * 問合せを取得します。
		 * @return 問合せを。
		 */
		public Query getQuery() {
			return query;
		}
	}

	/**
	 * Unionする問合せリスト。
	 */
	private List<UnionInfo> unionQueryList = null;

	/**
	 * Unionする問合せを追加。
	 * @param query Unionする問合せ。
	 * @param allFlag allフラグ。
	 */
	public void addUnionQuery(final Query query, final boolean allFlag) {
		if (this.unionQueryList	 == null) {
			this.unionQueryList = new ArrayList<UnionInfo>();
		}
		this.unionQueryList.add(new UnionInfo(query, allFlag));
	}

	/**
	 * Unionする問合せを追加。
	 * @param query Unionする問合せ。
	 */
	public void addUnionQuery(final Query query) {
		this.addUnionQuery(query, false);
	}

	/**
	 * Unionする問合せリストを取得します。
	 * @return Unionする問合せリスト。
	 */
	public List<UnionInfo> getUnionQueryList() {
		return unionQueryList;
	}

	/**
	 * 指定されたクラスのテーブルを取得します。
	 * @param tblclass テーブルクラス。
	 * @param alias テーブルの別名。
	 * @return テーブルクラスのインスタンス。
	 */
	protected Table getTable(final Class<? extends Table> tblclass, final String alias) {
		Table table = null;
		if (tblclass.isAssignableFrom(this.mainTable.getClass()) && alias.equals(this.mainTable.getAlias())) {
			table = this.mainTable;
		} else {
			for (JoinInfo info: this.joinInfoList) {
				if (tblclass.isAssignableFrom(info.getJoinTable().getClass()) && alias.equals(info.getJoinTable().getAlias())) {
					table = info.getJoinTable();
					break;
				}
			}
		}
		return table;
	}
}
