package jp.dataforms.fw.devtool.table.page;

import org.apache.poi.ss.usermodel.Workbook;

import jp.dataforms.fw.field.base.FieldList;
import jp.dataforms.fw.field.sqltype.IntegerField;
import jp.dataforms.fw.field.sqltype.VarcharField;
import jp.dataforms.fw.report.ExcelReport;
import jp.dataforms.fw.report.ReportTable;

/**
 * テーブル一覧レポートクラスです。
 * 
 */
public class TableListReport extends ExcelReport {
	/**
	 * コンストラクタ。
	 * @param wb ワークブック。
	 */
	public TableListReport(final Workbook wb) {
		this.setWorkbook(wb);
		FieldList flist = new FieldList();
		flist.add(new IntegerField("no"));
		flist.add(new VarcharField("tableName", 1024));
		flist.add(new VarcharField("tableComment", 1024));
		flist.add(new VarcharField("tableClassName", 1024));
		ReportTable rtbl = new ReportTable("tableList", flist);
		this.addReportTable(rtbl);
		this.setRowsParPage(36);
	}
}
