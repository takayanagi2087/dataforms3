package jp.dataforms.fw.htmltable;


import jp.dataforms.fw.field.base.Field;
import jp.dataforms.fw.field.base.FieldList;

/**
 * ページスクロールするHTMLテーブル。
 *
 */
public class PageScrollHtmlTable extends HtmlTable {
    /**
     * Logger.
     */
//    private static Logger log = Logger.getLogger(DataForms.class.getName());

    /**
     * Spanフラグをtrueにします。
     */
    private void setSpanFlagToField() {
    	for (Field<?> f: this.getFieldList()) {
    		f.setSpanField(true);
    	}
    }

	/**
	 * コンストラクタ。
	 * @param id テーブルID。
	 * @param fieldList フィールドリスト。
	 */
	public PageScrollHtmlTable(final String id, final FieldList fieldList) {
		super(id, fieldList);
	}

	/**
	 * コンストラクタ。
	 * @param id テーブルID。
	 * @param fieldList フィールドリスト。
	 */
	public PageScrollHtmlTable(final String id, final Field<?>... fieldList) {
		super(id, fieldList);
	}

	@Override
	protected void onBind() {
		super.onBind();
		this.setSpanFlagToField();
	}

	@Override
	public void init() throws Exception {
		this.setAdditionalHtml(this.getPage().getPageFramePath() + "/PageController.html");
		super.init();
	}
	
}
