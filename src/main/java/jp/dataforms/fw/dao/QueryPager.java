package jp.dataforms.fw.dao;

import java.util.HashMap;
import java.util.Map;

/**
 * 問い合わせページャークラス。
 * <pre>
 * 問い合わせ結果のページ取得を行います。
 * </pre>
 *
 */
public class QueryPager {
	/**
	 * 問い合わせ。
	 */
	private Query query = null;

	/**
	 * SQL。
	 */
	private String sql = null;

	/**
	 * 一ページの行数。
	 */
	private int linesParPage = 10;

	/**
	 * コンストラクタ。
	 * @param query 問い合わせ。
	 * @param lines 1ページの行数。
	 */
	public QueryPager(final Query query, final int lines) {
		this.query = query;
		this.linesParPage = lines;
	}

	/**
	 * コンストラクタ。
	 * @param sql SQL。
	 * @param lines ページの行数。
	 */
	public QueryPager(final String sql, final int lines) {
		this.sql = sql;
		this.linesParPage = lines;
	}

	/**
	 * 問い合わせを取得します。
	 * @return 問い合わせ。
	 */
	public Query getQuery() {
		return query;
	}

	/**
	 * SQLを取得します。
	 * @return SQL。
	 */
	public String getSql() {
		return sql;
	}


	/**
	 * SQLを設定します。
	 * @param sql SQL。
	 */
	public void setSql(final String sql) {
		this.sql = sql;
	}

	/**
	 * 1ページの行数を取得します。
	 * @return 1ページの行数。
	 */
	public int getLinesParPage() {
		return linesParPage;
	}

	/**
	 * 取得行開始。
	 */
	private Integer rowFrom = null;
	/**
	 * 取得行終了。
	 */
	private Integer rowTo = null;


	/**
	 * 指定したページの範囲パラメータを取得します。
	 * @param page ページ。
	 * @return パラメータのマップ。
	 */
	public Map<String, Object> getPageParameter(final int page) {
		Map<String, Object> ret = new HashMap<String, Object>();
		ret.put("rowFrom", page * this.linesParPage);
		ret.put("rowTo", (page + 1) * this.linesParPage - 1);
		this.setRowFrom(page * this.linesParPage);
		this.setRowTo((page + 1) * this.linesParPage - 1);
		return ret;
	}

	/**
	 * 取得開始行を取得します。
	 * @return 取得開始行。
	 */
	public Integer getRowFrom() {
		return rowFrom;
	}

	/**
	 * 取得開始行を取得します。
	 * @param rowFrom 設定します。
	 */
	public void setRowFrom(final Integer rowFrom) {
		this.rowFrom = rowFrom;
	}

	/**
	 * 取得終了行を取得します。
	 * @return 取得終了行。
	 */
	public Integer getRowTo() {
		return rowTo;
	}

	/**
	 * 取得終了行を設定します。
	 * @param rowTo 取得終了行。
	 */
	public void setRowTo(final Integer rowTo) {
		this.rowTo = rowTo;
	}
}
