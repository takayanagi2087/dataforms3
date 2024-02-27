package jp.dataforms.fw.report;

import java.util.List;
import java.util.Map;

import jp.dataforms.fw.field.base.FieldList;

/**
 * エクスポートデータのインターフェースです。
 *
 */
public interface ExportDataFile {


	/**
	 * エクスポートデータのファイル名を取得します。
	 * @param fileName エクスポートデータのファイル名。
	 */
	void setFileName(final String fileName);

	/**
	 * エクスポートデータのファイル名を取得します。
	 * @return エクスポートデータのファイル名。
	 */
	String getFileName();

	/**
	 * エクスポートデータのContent-typeを取得します。
	 * @return エクスポートデータのContent-type。
	 */
	String getContentType();
	/**
	 * エクスポートデータを取得すします。
	 * @param data エクスポートするデータのリスト。
	 * @param flist エクスポートするデータのフィールドリスト。
	 * @return エクスポートデータ。
	 * @throws Exception 例外。
	 */
	byte[] getExportData(final List<Map<String, Object>> data, final FieldList flist) throws Exception;
}
