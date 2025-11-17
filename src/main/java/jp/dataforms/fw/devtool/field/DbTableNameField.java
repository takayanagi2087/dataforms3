package jp.dataforms.fw.devtool.field;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jp.dataforms.fw.dao.Dao;
import jp.dataforms.fw.field.base.TextField;
import jp.dataforms.fw.servlet.DataFormsServlet;
import jp.dataforms.fw.util.ConfUtil.DbcpDataSource;
import jp.dataforms.fw.util.ConfUtil.JndiDataSource;

/**
 * DB中のテーブルを検索するフィールド。
 *
 */
public class DbTableNameField extends TextField {

	/**
	 * Logger.
	 */
	private static Logger logger = LogManager.getLogger(DbTableNameField.class);

	/**
	 * コンストラクタ。
	 */
	public DbTableNameField() {
		super(null);
	}

	/**
	 * コンストラクタ。
	 * @param id フィールド。
	 */
	public DbTableNameField(final String id) {
		super(id);
	}


	@Override
	protected List<Map<String, Object>> queryAutocompleteSourceList(Map<String, Object> data) throws Exception {
		String id = (String) data.get("currentFieldId");
		String rowid = this.getHtmlTableRowId(id);
		String colid = this.getHtmlTableColumnId(id);
		String tblname = (String) data.get(id);
		Dao dao = null;
		DbcpDataSource dbcpds = DataFormsServlet.getConf().getOriginalDbcpDataSource();
		if (dbcpds != null) {
			dao = new Dao(dbcpds);
		} else {
			JndiDataSource jds = DataFormsServlet.getConf().getOriginalJndiDataSource();
			dao = new Dao(jds);
		}
		try (Connection conn = dao.getConnection()) {
			List<Map<String, Object>> tableList = dao.queryTableInfo();
			List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
			for (Map<String, Object> m: tableList) {
				Dao.TableInfoEntity e = new Dao.TableInfoEntity(m);
				String name = e.getTableName();
				logger.debug("name=" + name);
				if (name.toLowerCase().indexOf(tblname.toLowerCase()) >= 0) {
					Map<String, Object> rm = new HashMap<String, Object>();
					rm.put(colid, name);
					rm.put("label", name + ":" + (e.getRemarks() == null ? "" : e.getRemarks()));
//					rm.put("tableComment", e.getRemarks());
					result.add(rm);
				}
			}
			return this.convertToAutocompleteList(rowid, result, colid, "label"/*, "tableComment"*/);
		}
	}
}
