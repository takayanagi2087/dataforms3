package jp.dataforms.fw.controller;

import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jp.dataforms.fw.annotation.WebMethod;
import jp.dataforms.fw.exception.ApplicationException;
import jp.dataforms.fw.field.base.FieldList;
import jp.dataforms.fw.htmltable.HtmlTable;
import jp.dataforms.fw.report.ExcelExportDataFile;
import jp.dataforms.fw.report.ExportDataFile;
import jp.dataforms.fw.response.BinaryResponse;
import jp.dataforms.fw.response.JsonResponse;
import jp.dataforms.fw.response.Response;
import jp.dataforms.fw.validator.ValidationError;

/**
 * 問い合わせフォームクラスです。
 * <pre>
 * 問い合わせの条件を入力するフォームです。
 *
 * </pre>
 *
 */
public abstract class QueryForm extends Form {
    /**
     * Logger.
     */
    private static Logger logger = LogManager.getLogger(QueryForm.class.getName());


    /**
     * コンストラクタ。
     */
    public QueryForm() {
    	this(DataForms.ID_QUERY_FORM);
    }

	/**
	 * コンストラクタ。
	 * @param id フォームID。
	 */
	public QueryForm(final String id) {
		super(id);
	}

	/**
	 * 問い合わせの開始処理を行います。
	 * <pre>
	 * 問い合わせの条件をチェックし、その結果を返すのみの処理になっています。
	 * 条件が正常な場合、Javascript側の処理でQueryResultFormに検索条件を渡します。
	 * </pre>
	 * @param p パラメータ。
	 * @return 応答情報。
	 * @throws Exception 例外。
	 */
    @WebMethod
	public JsonResponse query(final Map<String, Object> p) throws Exception {
    	List<ValidationError> err = this.validate(p);
    	JsonResponse result = null;
    	if (err.size() > 0) {
    		result = new JsonResponse(JsonResponse.INVALID, err);
    	} else {
        	result = new JsonResponse(JsonResponse.SUCCESS, "");
    	}
    	return result;
    }

    /**
     * エクスポートすべきデータを検索します。
     * <pre>
     * エクスポート機能が必要な場合実装します。
     * </pre>
     * @param data 問合せフォームのデータ。
     * @return ダウンロードすべきデータ。
     * @throws Exception 例外。
     */
    protected List<Map<String, Object>> queryExportData(final Map<String, Object> data) throws Exception {
		throw new ApplicationException(this.getPage(), "error.notimplemetmethod");
    }

    /**
     * エクスポートすべきデータのフィールドリストを取得します。
     * <pre>
     * QueryResultFormのqueryResultテーブルのフィールドリストを取得します。
     * </pre>
     * @param data 問合せフォームのデータ。
     * @return エクスポートすべきデータのフィールドリスト。
     * @throws Exception 例外。
     */
    protected FieldList getExportDataFieldList(final Map<String, Object> data) throws Exception {
    	WebComponent df = this.getParent();
    	Form form = (Form) df.getComponent(Page.ID_QUERY_RESULT_FORM);
    	if (form != null) {
    		HtmlTable table = (HtmlTable) form.getComponent(Page.ID_QUERY_RESULT);
    		if (table != null) {
        		return table.getFieldList();
    		}
    	}
    	// 問合せ結果フォームがない場合
		throw new ApplicationException(this.getPage(), "error.notimplemetmethod");
    }

    /**
     * エクスポートするデータファイルを取得します。
     * <pre>
     * ExportDataFileインターフェースを実装したクラスのインスタンスとして
     * ExcelExportDataFileクラスのインスタンスを返します。
     * エクスポートデータを別の形式にしたい場合、このメソッドをオーバーライドし
     * 別のExportDataFileインターフェースを実装したクラスのインスタンスを返すようにします。
     * </pre>
     * @return エクスポートするデータファイル。
     */
    protected ExportDataFile getExportDataFile() {
    	return new ExcelExportDataFile();
    }

    /**
     * 検索結果をエクスポートします。
     * @param p 問合せフォームのデータ。
     * @return ダウンロードデータ。
     * @throws Exception 例外。
     */
    @WebMethod
    public Response exportData(final Map<String, Object> p) throws Exception {
    	List<ValidationError> err = this.validate(p);
    	Response result = null;
    	if (err.size() > 0) {
    		result = new JsonResponse(JsonResponse.INVALID, err);
    	} else {
        	String sortOrder = (String) p.get("sortOrder");
        	logger.debug(() -> "sortOrder=" + sortOrder);
    		Map<String, Object> data = this.convertToServerData(p);
    		data.put("sortOrder", sortOrder);
    		List<Map<String, Object>> list = this.queryExportData(data);
    		int no = 0;
    		for (Map<String, Object> m: list) {
    			m.put("rowNo", Integer.valueOf(++no));
    		}
    		ExportDataFile exdata = this.getExportDataFile();
    		FieldList flist = this.getExportDataFieldList(data);
    		byte[] exceldata = exdata.getExportData(list, flist);
    		result = new BinaryResponse(exceldata, exdata.getContentType(), exdata.getFileName());
    	}
    	return result;
    }


}
