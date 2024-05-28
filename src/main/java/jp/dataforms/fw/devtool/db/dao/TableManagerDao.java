package jp.dataforms.fw.devtool.db.dao;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URLDecoder;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.text.StringEscapeUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.stream.JsonWriter;

import jp.dataforms.fw.app.user.dao.UserInfoTable;
import jp.dataforms.fw.controller.Page;
import jp.dataforms.fw.controller.WebComponent;
import jp.dataforms.fw.dao.Dao;
import jp.dataforms.fw.dao.ForeignKey;
import jp.dataforms.fw.dao.Index;
import jp.dataforms.fw.dao.JDBCConnectableObject;
import jp.dataforms.fw.dao.Query;
import jp.dataforms.fw.dao.RecordProcessor;
import jp.dataforms.fw.dao.SubQuery;
import jp.dataforms.fw.dao.Table;
import jp.dataforms.fw.dao.TableRelation;
import jp.dataforms.fw.dao.file.BlobFileStore;
import jp.dataforms.fw.dao.file.FileObject;
import jp.dataforms.fw.dao.file.FileStore;
import jp.dataforms.fw.dao.file.TableFolderFileStore;
import jp.dataforms.fw.dao.sqlgen.SqlGenerator;
import jp.dataforms.fw.field.base.Field;
import jp.dataforms.fw.field.base.FieldList;
import jp.dataforms.fw.field.common.FileField;
import jp.dataforms.fw.response.BinaryResponse;
import jp.dataforms.fw.servlet.DataFormsServlet;
import jp.dataforms.fw.util.ClassFinder;
import jp.dataforms.fw.util.FileUtil;
import jp.dataforms.fw.util.JsonUtil;
import jp.dataforms.fw.util.NumberUtil;
import jp.dataforms.fw.util.StringUtil;

/**
 * TableManagerPage用のDAOクラス。
 *
 */
public class TableManagerDao extends Dao {
    /**
     * Logger.
     */
    private static Logger logger = LogManager.getLogger(TableManagerDao.class.getName());

	/**
	 * コンストラクタ。
	 * @param obj JDBC接続可能オブジェクト。
	 * @throws Exception 例外。
	 */
	public TableManagerDao(final JDBCConnectableObject obj) throws Exception {
		super(obj);
	}


	/**
	 * データベースが初期化されているかどうかを判定します。
	 *
	 * @return データベースが初期化されている場合true。
	 * @throws Exception 例外。
	 */
	public boolean isDatabaseInitialized() throws Exception {
		UserInfoTable table = new UserInfoTable();
		return this.tableExists(table.getTableName());
	}

	/**
	 * テーブルの存在チェックします。
	 * @param sequencename テーブル名。
	 * @return 存在する場合true。
	 * @throws Exception 例外。
	 */
	private boolean sequenceExists(final String sequencename) throws Exception {
		final SqlGenerator gen = this.getSqlGenerator();
		Dao dao = new Dao(this);
		String sql = gen.generateSequenceExistsSql();
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("sequenceName", sequencename);
		int seqcnt = NumberUtil.intValue(dao.executeScalarQuery(sql, param));
		return (seqcnt > 0);
	}

	/**
	 * テーブルクラスの一覧を取得します。
	 * @param data パラメータ。
	 * @return クエリ結果。
	 * @throws Exception 例外。
	 */
	public List<Map<String, Object>> queryTableClass(final Map<String, Object> data) throws Exception {
		// String funcpath = (String) data.get("functionSelect");
		String packageName = (String) data.get("packageName");
		String classname = (String) data.get("className");
		return queryTableClass(packageName, classname);
	}


	/**
	 * テーブルクラスを検索します。
	 * @param packageName パッケージ名。
	 * @param classname テーブルクラス名の部分文字列。
	 * @return テーブルクラス一覧。
	 * @throws Exception 例外。
	 */
	public List<Map<String, Object>> queryTableClass(final String packageName, final String classname) throws Exception {
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		ClassFinder finder = new ClassFinder();
		List<Class<?>> tableList = finder.findClasses(packageName, Table.class);
		int no = 1;
		for (Class<?> tblcls : tableList) {
			if (SubQuery.class.isAssignableFrom(tblcls)) {
				continue;
			}
			if (Table.class.getName().equals(tblcls.getName())) {
				continue;
			}
			if (!StringUtil.isBlank(classname)) {
				if (tblcls.getName().indexOf(classname) < 0) {
					continue;
				}
			}
			Map<String, Object> m = this.getTableInfo(tblcls.getName());
			if (m != null) {
				m.put("rowNo", Integer.valueOf(no));
				result.add(m);
			}
			no++;
		}
		return result;
	}

	/**
	 * テーブルの状態を取得します。
	 * @param m マップ。
	 * @param gen sqlジェネレータ。
	 * @param tbl テーブル。
	 * @throws Exception 例外。
	 */
	private void getTableStatus(final Map<String, Object> m, final SqlGenerator gen, final Table tbl) throws Exception {
		if (this.tableExists(tbl.getTableName())) {
			m.put("tableExists", Boolean.valueOf(true));
			m.put("status", "1");
			m.put("statusVal", "1");
			String sql = gen.generateRecordCountSql(tbl.getTableName());
			Dao dao = new Dao(this);
			Object c = dao.executeScalarQuery(sql, null);
			Integer reccnt = NumberUtil.intValue(c);
			m.put("recordCount", reccnt);
		} else {
			m.put("tableExists", Boolean.valueOf(false));
			m.put("status", "0");
			m.put("statusVal", "0");
			m.put("recordCount", Integer.valueOf(0));
		}
		String backupTable = tbl.getBackupTableName();
		logger.info("backupTable=" + backupTable);
		if (this.tableExists(backupTable)) {
			m.put("backupTable", "1");
		} else {
			m.put("backupTable", "0");
		}
	}




	/**
	 * テーブルに関する情報を取得します。
	 * @param classname テーブルクラス名。
	 * @return テーブル情報。
	 * @throws Exception 例外。
	 */
	public Map<String, Object> getTableInfo(final String classname) throws Exception {
		logger.debug("*** classname=" + classname);
		if (TableManagerDao.abstractTableSet.contains(classname)) {
			return null;
		}
		Map<String, Object> tableInfo = new HashMap<String, Object>();
		tableInfo.put("checkedClass", classname);
		tableInfo.put("className", classname);
		Table tbl = Table.newInstance(classname);
		if (tbl == null) {
			return null;
		}
		tableInfo.put("tableName", tbl.getTableName());
		tableInfo.put("tableComment", tbl.getComment());
		SqlGenerator gen = this.getSqlGenerator();
		List<String> sqllist = gen.generateCreateTableSqlList(tbl);
		String indexNames = "";
		List<Index> ilist = tbl.getIndexList();
		for (Index idx: ilist) {
			sqllist.add(gen.generateCreateIndexSql(idx));
			if (indexNames.length() > 0) {
				indexNames += "\n";
			}
			indexNames += idx.getClass().getSimpleName();
		}
		tableInfo.put("indexNames", indexNames);
		StringBuilder sb = new StringBuilder();
		for (String sql : sqllist) {
			sb.append(sql); sb.append(";\n");
		}
		tableInfo.put("createTableSql", sb.toString());
		this.getTableStatus(tableInfo, gen, tbl);
		boolean st = tbl.structureAccords(this);
		tableInfo.put("difference", (st ? "0" : "1"));
		tableInfo.put("differenceVal", (st ? "0" : "1"));
		boolean seq = tbl.isAutoIncrementId();
		tableInfo.put("sequenceGeneration", (seq ? "1" : "0"));

		logger.info("backup table=" + tbl.getBackupTableName());

		return tableInfo;
	}

	/**
	 * テーブル情報のリストを取得します。
	 * @param classlist テーブルクラス一覧。
	 * @return テーブル情報リスト。
	 * @throws Exception 例外。
	 */
	public List<Map<String, Object>> getTableInfoList(final List<String> classlist) throws Exception {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		for (String cls : classlist) {
			Map<String, Object> m = this.getTableInfo(cls);
			if (m != null) {
				list.add(m);
			}
		}
		return list;
	}


	/**
	 * テーブルを削除します。
	 * @param className テーブルクラス名。
	 * @throws Exception 例外。
	 */
	public void dropTable(final String className) throws Exception {
		Table tbl = Table.newInstance(className);
		this.dropIndex(tbl);
		SqlGenerator gen = this.getSqlGenerator();
		String sql = gen.generateDropTableSql(tbl.getTableName());
		this.executeUpdate(sql, (Map<String, Object>) null);
	}

	/**
	 * バックアップテーブルを削除します。
	 * @param className テーブルクラス名。
	 * @throws Exception 例外。
	 */
	public void dropBackupTable(final String className) throws Exception {
		Table tbl = Table.newInstance(className);
		SqlGenerator gen = this.getSqlGenerator();
		String sql = gen.generateDropTableSql(tbl.getBackupTableName());
		this.executeUpdate(sql, (Map<String, Object>) null);
	}

	/**
	 * テーブルをバックアップテーブルに移動します。
	 * @param className テーブルクラス名。
	 * @throws Exception 例外。
	 * @return バックアップテーブル名。
	 */
	public String moveToBackupTable(final String className) throws Exception {
		Table tbl = Table.newInstance(className);
		this.dropIndex(tbl);
		SqlGenerator gen = this.getSqlGenerator();
		String oldname = tbl.getTableName();
		String newname = tbl.getBackupTableName();
		if (this.tableExists(newname)) {
			String sql = this.getSqlGenerator().generateDropTableSql(newname);
			try {
				this.executeUpdate(sql, (Map<String, Object>) null);
			} catch (SQLException e) {
				logger.warn(e.getMessage(), e);
			}
		}
		try {
			String sql = gen.generateRenameTableSql(oldname, newname);
			this.executeUpdate(sql, (Map<String, Object>) null);
		} catch (SQLException e) {
			logger.warn(e.getMessage(), e);
		}
		return newname;
	}

	/**
	 * ダウンロードパラメータをマップに変換する。
	 * @param param ダウンロードパラメータ。
	 * @return マップへの返還結果。
	 * @throws Exception 例外。
	 */
	private Map<String, Object> getDownloadParamMap(final String param) throws Exception {
		Map<String, Object> ret = new HashMap<String, Object>();
		String [] sp = param.split("&");
		for (String pair: sp) {
			String [] p = pair.split("=");
			if (p.length == 2) {
				String key = URLDecoder.decode(p[0], "utf-8");
				String value = URLDecoder.decode(p[1], "utf-8");
				ret.put(key, value);
			}
		}
		return ret;
	}

	/**
	 * TODO:ファイルフィールドクラスに移動。
	 * 
	 * フィールドに対応したファイル情報を取得します。
	 * @param f フィールド。
	 * @param value DBから取得したオブジェクト。
	 * @param filePath ファイルの出力パス。
	 * @param table テーブル。
	 * @param data 1レコードのデータ。
	 * @return ファイル情報。
	 * @throws Exception 例外。
	 */
	private Map<String, Object> getFileInfo(final FileField<?> f, final Object value, final String filePath, final Table table, final Map<String, Object> data) throws Exception {
		Map<String, Object> ret = new HashMap<String, Object>();
		File dir = new File(filePath);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		FileObject fo = null;
		if (f instanceof FileField) {
			FileField<?> ff = (FileField<?>) f;
			ff.setDBValue(value);
			fo = ff.getValue();
			FileStore store = ff.newFileStore();
			if (store instanceof TableFolderFileStore) {
				;
			} else if (store instanceof BlobFileStore) {
				fo.setDownloadParameter(ff.getBlobDownloadParameter(data));
			}
		}
		Map<String, Object> dlp = this.getDownloadParamMap(fo.getDownloadParameter());
		logger.debug(() -> "dlp=" + JsonUtil.encode(dlp, true));
		ret.put("filename", fo.getFileName());
		String key = "";
		for (Field<?> pkf: table.getPkFieldList()) {
			if (key.length() > 0) {
				key += "_";
			}
			key += data.get(pkf.getId()).toString();
		}
		BinaryResponse resp = f.download(dlp);
		String fn =  key + "_" + f.getId() + "_" + fo.getFileName();
		resp.saveFile(dir + "/" + fn);
		ret.put("saveFile", fn);
		ret.put("downloadParameter", fo.getDownloadParameter());
		ret.put("length", fo.getLength());
		return ret;
	}

	/**
	 * TODO:Tableクラスに移動する。
	 * 
	 * テーブルのバックアップを取得します。
	 * @param classname テーブルクラス名。
	 * @param outdir 出力ディレクトリ。
	 * @return バックアップファイルのパス。
	 * @throws Exception 例外。
	 */
	public String exportData(final String classname, final String outdir) throws Exception {
		final Table tbl = Table.newInstance(classname);
		String datapath = outdir + "/" + classname.replaceAll("\\.", "/") + ".data.json";
		logger.debug(() -> "datapath=" + datapath);

		String filePath = outdir + "/" + classname.replaceAll("\\.", "/");
		File ff = new File(filePath);
		if (ff.exists()) {
			// 既にファイルが存在する場合は削除。
			File[] flist = ff.listFiles();
			for (File f: flist) {
				f.delete();
			}
		}
		if (this.tableExists(tbl.getTableName())) {
			File f = new File(datapath);
			File dir = f.getParentFile();
			if (!dir.exists()) {
				dir.mkdirs();
			}
			String sql = "select * from " + tbl.getTableName();
			try (FileOutputStream out = new FileOutputStream(f)) {
				try (JsonWriter writer = JsonUtil.getJsonWriter(out)){
					writer.beginArray();
					try {
						this.executeQuery(sql, null,  new RecordProcessor() {
							@Override
							public boolean process(final Map<String, Object> m) throws Exception {
								logger.debug(() -> "m=" + m.toString());
								writer.beginObject();
								FieldList flist = tbl.getFieldList();
								for (Field<?> fld : flist) {
									String id = fld.getId();
									Object value = m.get(id);
									if (fld instanceof FileField) 	{
										if (value != null) {
											writer.name(id);
											Map<String, Object> finfo = TableManagerDao.this.getFileInfo((FileField<?>) fld, value, filePath, tbl, m);
											writer.beginObject();
											Set<String> ks = finfo.keySet();
											for (String k: ks) {
												if (finfo.get(k) instanceof Long) {
													writer.name(k).value((Long) finfo.get(k));
												} else {
													writer.name(k).value((String) finfo.get(k));
												}
											}
											writer.endObject();
											//rec.put(id, TableManagerDao.this.getFileInfo((FileField<?>) fld, value, filePath, tbl, m));
										}
									} else {
										fld.setValueObject(value);
										if (fld.getClientValue() != null) {
											fld.setValueObject(value);
											writer.name(id).value(fld.getClientValue().toString());
//											rec.put(id, fld.getClientValue().toString());
										} else {
											writer.name(id).nullValue();
											// rec.put(id, null);
										}
									}
								}
								writer.endObject();
								return true;
							}
						});
					} finally {
						writer.endArray();
					}
				}
			}
		}
		return datapath;
	}

	
	/**
	 * 作成者ユーザID, 更新者ユーザIDを適切に設定します。
	 * @param data ユーザIDを設定するデータマップ。
	 */
	private void setUserIdValue(final Map<String, Object> data) {
		if (data.get("createUserId") == null) {
			data.put("createUserId", Long.valueOf(0));
		}
		if (data.get("updateUserId") == null) {
			data.put("updateUserId", Long.valueOf(0));
		}
	}

	/**
	 * インポートデータのファイルフィールド関連の変換を行います。
	 *
	 * @param data インポートデータ。
	 * @param datadir データディレクトリ。
	 * @param table テーブル。
	 * @throws Exception 例外.
	 */
	private void convertImportData(final Map<String, Object> data, final String datadir, final Table table) throws Exception {
		String filePath = datadir + "/" + table.getClass().getName().replaceAll("\\.", "/");
		for (Field<?> f: table.getFieldList()) {
			if (f instanceof FileField) {
				@SuppressWarnings("unchecked")
				Map<String, Object> m = (Map<String, Object>) data.get(f.getId());
				if (m != null) {
					FileObject obj = ((FileField<?>) f).getFileObjectFromImportMap(m, filePath);
					data.put(f.getId(), obj);
				}
			} else {
				f.setClientValue(data.get(f.getId()));
				data.put(f.getId(), f.getValue());
			}
		}
	}


	/**
	 * 初期データをインポートします。
	 * @param classname クラス名。
	 * @throws Exception 例外。
	 */
	public void importIntialData(final String classname) throws Exception {
		final Table tbl = Table.newInstance(classname);
		this.deleteAllRecord(tbl);
		String initialDataPath = Page.getServlet().getServletContext().getRealPath("/WEB-INF/initialdata");
		String file = tbl.getImportData(initialDataPath);
		if (file == null) {
			List<Map<String, Object>> list = tbl.getInitialData();
			if (list != null) {
				String sql = this.getSqlGenerator().generateInsertSql(tbl);
				for (int i = 0; i < list.size(); i++) {
					Map<String, Object> m = list.get(i);
					Map<String, Object> data = tbl.getFieldList().convertClientToServer(m);
					this.setUserIdValue(data);
					this.executeUpdate(sql, data);
				}
			}
		} else {
			this.importData(classname, initialDataPath);
		}
	}

	/**
	 * レコードの存在チェック。
	 * @param table テーブル。
	 * @param data データ。
	 * @return 存在する場合true。
	 * @throws Exception 例外。
	 */
	private boolean existRecord(final Table table, final Map<String, Object> data) throws Exception {
		Query q = new Query();
		q.setFieldList(table.getPkFieldList());
		q.setMainTable(table);
		q.setConditionFieldList(table.getPkFieldList());
		q.setConditionData(data);
		q.setEffectivenessOfDeleteFlag(false);
		List<Map<String, Object>> list = this.executeQuery(q);
		return (list.size() > 0);
	}

	/**
	 * テーブルのデータを削除。
	 * @param classname テーブルクラス名。
	 * @throws Exception 例外。
	 */
	public void deleteTableData(final String classname) throws Exception {
		final Table table = Table.newInstance(classname);
		this.deleteAllRecord(table);
	}


	/**
	 * 指定フォルダのデータをインポートします。
	 * @param classname テーブルクラス名。
	 * @param path データのパス。
	 * @throws Exception データ。
	 */
	public void importData(final String classname, final String path) throws Exception {
		final Table tbl = Table.newInstance(classname);
		String file = tbl.getImportData(path);
		if (file != null) {
			try (InputStream is = new FileInputStream(file)) {
				this.importJson(is, tbl, path);
				this.adjustSequence(tbl);
			}
		}
	}

	/**
	 * JSONデータをテーブルにインポートします。
	 * @param is JSONデータの入力ストリーム。
	 * @param tbl インポート先のテーブル。
	 * @param path JSONのベースパス。
	 * @throws Exception 例外。
	 */
	private void importJson(InputStream is, final Table tbl, final String path) throws Exception {
		// JSONReader を取得
		JsonUtil.readBigArray(is, (rec) -> {
			@SuppressWarnings("unchecked")
			Map<String, Object> m = (Map<String, Object>) rec;
			logger.debug(() -> "m=" + m.toString());
			for (String key: m.keySet()) {
				Object v = m.get(key);
				if (v instanceof String) {
					String str = (String) v;
					logger.debug("unescape=" + str + "->" + StringEscapeUtils.unescapeHtml4(str));
					m.put(key, StringEscapeUtils.unescapeHtml4(str));
				}
			}
			this.convertImportData(m, path, tbl);
			Map<String, Object> data = tbl.convertImportData(m);
			this.setUserIdValue(data);
			if (this.existRecord(tbl, data)) {
				this.executeUpdate0(tbl, data);
			} else {
				this.executeInsert0(tbl, data);
			}
		});
/*		JSONReader reader = new JSON().getReader(is);
		JSONEventType type = null;
		while ((type = reader.next()) != null) {
			if (type ==  JSONEventType.START_OBJECT) {
				@SuppressWarnings("unchecked")
				Map<String, Object> m = (Map<String, Object>) reader.getMap();
				logger.debug(() -> "m=" + m.toString());
				for (String key: m.keySet()) {
					Object v = m.get(key);
					if (v instanceof String) {
						String str = (String) v;
						logger.debug("unescape=" + str + "->" + StringEscapeUtils.unescapeHtml4(str));
						m.put(key, StringEscapeUtils.unescapeHtml4(str));
					}
				}
				this.convertImportData(m, path, tbl);
				Map<String, Object> data = tbl.convertImportData(m);
				this.setUserIdValue(data);
				if (this.existRecord(tbl, data)) {
					this.executeUpdate0(tbl, data);
				} else {
					this.executeInsert0(tbl, data);
				}
			}
		}*/
	}


	/**
	 * インポート後のシーケンス調整。
	 * @param tbl テーブル。
	 * @throws Exception 例外。
	 */
	private void adjustSequence(final Table tbl) throws Exception {
		SqlGenerator gen = this.getSqlGenerator();
		if (gen.isSequenceSupported()) {
			if (tbl.recordIdExists() && tbl.isAutoIncrementId()) {
				String sql = gen.generateAdjustSequenceSql(tbl);
				if (sql != null) {
					this.executeQuery(sql, (Map<String, Object>) null);
				} else {
					String maxsql = gen.generateGetMaxValueSql(tbl, tbl.getIdField(), new FieldList(), null);
					Long max = NumberUtil.longValueObject(this.executeScalarQuery(maxsql, null));
					if (max != null) {
						String dssql = gen.generateDropSequenceSql(tbl.getSequenceName());
						this.executeUpdate(dssql, (Map<String, Object>) null);
						String cssql = gen.generateCreateSequenceSql(tbl.getSequenceName(), max.longValue() + 1);
						this.executeUpdate(cssql, (Map<String, Object>) null);
					}
				}
			}
		}
	}


	/**
	 * web.xmlのuser-info-table-classに指定されたテーブルのクラスを取得します。
	 * @return テーブルクラス。
	 */
	private static Set<String> getAbstractTableSet() {
		Set<String> ret = new HashSet<String>();
		List<String> list = DataFormsServlet.getConf().getInitialize().getAbstractTableList();
		if (list != null) {
			ret.addAll(list);
		}
		return ret;
	}

	/**
	 * 抽象テーブルクラスの集合。
	 */
	private static Set<String> abstractTableSet = null;

	/**
	 * 初期化処理。
	 */
	static {
		abstractTableSet = TableManagerDao.getAbstractTableSet();
	}

	/**
	 * テーブル作成します。
	 * @param className テーブルクラス名。
	 * @throws Exception 例外。
	 */
	public void createTable(final String className) throws Exception {
		if (TableManagerDao.abstractTableSet.contains(className)) {
			return;
		}
		SqlGenerator gen = this.getSqlGenerator();
		Table tbl = Table.newInstance(className);
		List<String> sqllist = gen.generateCreateTableSqlList(tbl);
		for (String sql: sqllist) {
			this.executeUpdate(sql, (Map<String, Object>) null);
		}
		if (gen.isSequenceSupported()) {
			String cseq = gen.generateCreateSequenceSql(tbl);
			if (cseq != null) {
				String seqname = tbl.getSequenceName();
				if (!this.sequenceExists(seqname)) {
					this.executeUpdate(cseq, (Map<String, Object>) null);
				}
			}
		}

	}

	/**
	 * テーブルに付随するインデックスを全て削除します。
	 * @param table インデックスを削除するテーブル。
	 * @throws Exception 例外。
	 */
	public void dropIndex(final Table table) throws Exception {
		SqlGenerator gen = this.getSqlGenerator();
		List<Map<String, Object>> idxflist = this.getCurrentDBIndexInfo(table);
		//logger.debug("idxflist=" + JSON.encode(idxflist, true));
		Set<String> inset = new HashSet<String>();
		for (Map<String, Object> m: idxflist) {
			String idxName = (String) m.get("indexName");
			if (idxName != null) {
				idxName = idxName.toLowerCase();
				if (Pattern.matches(".+_index$", idxName)) {
					logger.debug("idxName={}", idxName);
					if (!inset.contains(idxName)) {
						try {
							String usql = gen.generateDropUniqueSql(table, idxName);
							if (usql != null) {
								this.executeUpdate(usql, (Map<String, Object>) null);
							}
						} catch (SQLException e) {
							logger.warn(e.getMessage(), e);
						}
						try {
							String sql = gen.generateDropIndexSql(idxName, table.getTableName());
							this.executeUpdate(sql, (Map<String, Object>) null);
						} catch (SQLException e) {
							logger.warn(e.getMessage(), e);
						}
						inset.add(idxName);
					}
				}
			}
		}
	}


	/**
	 * テーブルに付随するインデックスを全て削除します。
	 * @param table インデックスを削除するテーブル。
	 * @throws Exception 例外。
	 */
	public void dropForeignKey(final Table table) throws Exception {
		SqlGenerator gen = this.getSqlGenerator();
		TableRelation rel = table.getTableRelation();
		if (rel != null) {
			Set<String> fkset = this.getForeignKeyNameSet(table);
			for (String fkName: fkset) {
				try {
					String sql = gen.generateDropForeignKeySql(table.getTableName(), fkName);
					this.executeUpdate(sql, (Map<String, Object>) null);
				} catch (SQLException e) {
					logger.warn(() -> "msg=" + e.getMessage(), e);
				}
			}
		}
	}


	/**
	 * テーブルに付随するインデックスを全て作成します。
	 * @param table インデックスを作成するテーブル。
	 * @throws Exception 例外。
	 */
	public void createIndex(final Table table) throws Exception {
		SqlGenerator gen = this.getSqlGenerator();
		List<Index> list = table.getIndexList();
		for (Index index: list) {
			try {
				String sql = gen.generateCreateIndexSql(index);
				this.executeUpdate(sql, (Map<String, Object>) null);
			} catch (SQLException e) {
				logger.warn(() -> "msg=" + e.getMessage(), e);
			}
			if (index.isUnique()) {
				// ユニークインデックスの場合一意制約も付ける。
				String usql = gen.generateAddUniqueSql(index);
				if (usql != null) {
					try {
						this.executeUpdate(usql, (Map<String, Object>) null);
					} catch (SQLException e) {
						logger.warn(() -> "msg=" + e.getMessage(), e);
					}
				}
			}
		}
	}

	/**
	 * テーブルに付随する外部キーを全て作成します。
	 * @param table テーブル。
	 * @throws Exception 例外。
	 */
	public void createForeignKey(final Table table) throws Exception {
		SqlGenerator gen = this.getSqlGenerator();
		TableRelation rel = table.getTableRelation();
		if (rel != null) {
			List<ForeignKey> fklist = rel.getForeignKeyList();
			if (fklist != null) {
				for (ForeignKey fk: fklist) {
					try {
						String sql = gen.generateCreateForeignKeySql(fk);
						this.executeUpdate(sql, (Map<String, Object>) null);
					} catch (Exception e) {
						logger.warn(() -> "msg=" + e.getMessage(), e);
					}
				}
			}
		}
	}


	/**
	 * テーブルの初期化を行います。
	 * @param className テーブルクラス名。
	 * @throws Exception 例外。
	 */
	public void initTable(final String className) throws Exception {
		if (TableManagerDao.abstractTableSet.contains(className)) {
			return;
		}
		Table tbl = Table.newInstance(className);
		// テーブルが存在したらバックアップを行う.
		if (this.tableExists(tbl.getTableName())) {
			this.dropIndex(tbl);
			this.moveToBackupTable(className);
		}
		this.createTable(className);
		this.importIntialData(className);
		this.createIndex(tbl);
	}


	/**
	 * テーブル構造の更新を行います。
	 * @param className テーブルクラス名。
	 * @throws Exception 例外。
	 */
	public void updateTable(final String className) throws Exception {
		if (TableManagerDao.abstractTableSet.contains(className)) {
			return;
		}
		Table tbl = Table.newInstance(className);
		if (this.tableExists(tbl.getTableName())) {
			this.dropIndex(tbl);
			this.moveToBackupTable(className);
			this.createTable(className);
			this.createIndex(tbl);
			String bakfile = tbl.getBackupTableName();
			List<Map<String, Object>> collist = this.getTableColumnList(bakfile);
			SqlGenerator gen = this.getSqlGenerator();
			String sql = gen.generateCopyDataSql(tbl,  bakfile, collist);
			this.executeUpdate(sql, (Map<String, Object>) null);
		} else {
			this.initTable(className);
		}
	}

	/**
	 * 初期化するパッケージリストを取得します。
	 * @return 初期化するパッケージリスト。
	 */
	private List<String> getInitializePackageList() {
		return DataFormsServlet.getConf().getInitialize().getDatabasePackageList();
	}

	/**
	 * システム中の全テーブルクラスのリストを取得します。
	 * @return システム中の全テーブルクラスのリスト。
	 * @throws Exception 例外。
	 */
	@SuppressWarnings("unchecked")
	public List<Class<? extends Table>> getAllTables() throws Exception {
		List<Class<? extends Table>> list = new ArrayList<Class<? extends Table>>();
		List<String> pkglist = this.getInitializePackageList();
		ClassFinder finder = new ClassFinder();
		for (String pkg: pkglist) {
			List<Class<?>> tableList = finder.findClasses(pkg, Table.class);
			for (Class<?> t: tableList) {
				list.add((Class<? extends Table>) t);
			}
		}
		return list;
	}

	/**
	 * 全テーブルの外部キーを作成します。
	 * @throws Exception 例外。
	 */
	public void createAllForeignKeys() throws Exception {
		List<Class<? extends Table>> tbllist = this.getAllTables();
		for (Class<? extends Table> tcls: tbllist) {
			if (SubQuery.class.isAssignableFrom(tcls)) {
				continue;
			}
			if (tcls.isAnonymousClass()) {
				continue;
			}
/*			if (tcls.getConstructor() == null) {
				continue;
			}*/
			Table table = tcls.getDeclaredConstructor().newInstance();
			this.createForeignKey(table);
		}
	}

	/**
	 * 全テーブルの外部キーを削除します。
	 * @throws Exception 例外。
	 */
	public void dropAllForeignKeys() throws Exception {
		List<Class<? extends Table>> tbllist = this.getAllTables();
		for (Class<? extends Table> tcls: tbllist) {
			if (SubQuery.class.isAssignableFrom(tcls)) {
				continue;
			}
			if (tcls.isAnonymousClass()) {
				continue;
			}
/*			if (tcls.getConstructor() == null) {
				continue;
			}*/
			Table table = tcls.getDeclaredConstructor().newInstance();
			this.dropForeignKey(table);
		}
	}


	/**
	 * フィールド情報を取得します。
	 * @param func 機能(パッケージ)名。
	 * @param rs 結果セット。
	 * @return フィールド情報。
	 * @throws Exception 例外。
	 */
	private Map<String, Object> getTableClassFieldInfo(final String func, final ResultSet rs) throws Exception {
		SqlGenerator gen = this.getSqlGenerator();
		Map<String, Object> colinfo = new HashMap<String, Object>();
		String name = rs.getString("COLUMN_NAME");
		colinfo.put("columnName", name);
		String fieldClassName = StringUtil.firstLetterToUpperCase(StringUtil.snakeToCamel(name + "_field"));
		colinfo.put("fieldClassName", fieldClassName);
		if (func != null && func.length() > 0) {
			colinfo.put("packageName", func.substring(1) + ".field");
		}
		colinfo.put("superPackageName", WebComponent.BASE_PACKAGE + ".field.sqltype");
		String type = rs.getString("TYPE_NAME");
		int size = rs.getInt("COLUMN_SIZE");
		int scale = rs.getInt("DECIMAL_DIGITS");
		int t = rs.getInt("DATA_TYPE");
		String remarks = rs.getString("REMARKS");
		String dataType = gen.converTypeNameForDatabaseMetaData(type);
		if (t == Types.CHAR) {
			colinfo.put("superSimpleClassName", "CharField");
			colinfo.put("fieldLength", size);
		} else if (t == Types.VARCHAR) {
			if ("text".equalsIgnoreCase(type)) {
				colinfo.put("superSimpleClassName", "ClobField");
			} else {
				colinfo.put("superSimpleClassName", "VarcharField");
				colinfo.put("fieldLength", size);
			}
		} else if (t == Types.LONGVARCHAR) {
			colinfo.put("superSimpleClassName", "ClobField");
		} else if (t == Types.SMALLINT) {
			colinfo.put("superSimpleClassName", "SmallintField");
		} else if (t == Types.INTEGER) {
			colinfo.put("superSimpleClassName", "IntegerField");
		} else if (t == Types.BIGINT) {
			colinfo.put("superSimpleClassName", "BigintField");
		} else if (t == Types.DOUBLE) {
			colinfo.put("superSimpleClassName", "DoubleField");
		} else if (t == Types.NUMERIC || t == Types.DECIMAL) {
			colinfo.put("superSimpleClassName", "NumericField");
			colinfo.put("fieldLength", size + "," + scale);
		} else if (t == Types.DATE) {
			colinfo.put("superSimpleClassName", "DateField");
		} else if (t == Types.TIME) {
			colinfo.put("superSimpleClassName", "TimeField");
		} else if (t == Types.TIMESTAMP) {
			colinfo.put("superSimpleClassName", "TimestampField");
		} else if (t == Types.CLOB) {
			colinfo.put("superSimpleClassName", "ClobField");
		} else if (t == Types.BLOB) {
			colinfo.put("superSimpleClassName", "BlobField");
		}
		colinfo.put("dataType", dataType);
		colinfo.put("remarks", remarks);
		colinfo.put("overwriteMode", "error");
		return colinfo;
	}


	/**
	 * カラムリストにPKリストに設定します。
	 * @param tblname テーブル名称。
	 * @param collist カラムリスト。
	 * @return PKフラグを設定したフィールドリスト。
	 * @throws Exception 例外。
	 */
	public List<Map<String, Object>> setPkFlag(final String tblname, final List<Map<String, Object>> collist) throws Exception {
		Connection conn = this.getConnection();
		DatabaseMetaData md = conn.getMetaData();
		String schema = getSchema(conn);
		try (ResultSet rs = md.getPrimaryKeys(conn.getCatalog(), schema, tblname)) {
			while (rs.next()) {
				String colname = rs.getString("COLUMN_NAME");
				for (Map<String, Object> m: collist) {
					String name = (String) m.get("columnName");
					if (name.equals(colname)) {
						m.put("pkFlag", "1");
					}
				}
			}
		}
		return collist;
	}

	/**
	 * テーブル構造取得します。
	 * @param func 機能(パッケージ)名。
	 * @param tblname テーブル名。
	 * @return テーブル構造。
	 * @throws Exception 例外。
	 */
	public List<Map<String, Object>> getTableColumnList(final String func, final String tblname) throws Exception {
		SqlGenerator gen = this.getSqlGenerator();
		Connection conn = this.getConnection();
		DatabaseMetaData md = conn.getMetaData();
		String schema = getSchema(conn);
		List<Map<String, Object>> collist = new ArrayList<Map<String, Object>>();
		ResultSet rs = md.getColumns(conn.getCatalog(), schema, gen.convertTableNameForDatabaseMetaData(tblname), "%");
		try {
			while (rs.next()) {
				Map<String, Object> m = this.getTableClassFieldInfo(func, rs);
				collist.add(m);
			}
		} finally {
			rs.close();
		}

		this.setPkFlag(tblname, collist);
		return collist;
	}

	/**
	 * SQLスクリプトの実行。
	 * @param script SQLスクリプトファイル。
	 * @throws Exception 例外。
	 */
	public void executeSqlScript(final String script) throws Exception {
		File f = new File(script);
		if (f.exists()) {
			String sql = FileUtil.readTextFile(script, "utf-8");

			Pattern p = Pattern.compile("--.*$", Pattern.MULTILINE);
			Matcher m = p.matcher(sql);
			sql = m.replaceAll("");
			String [] scriptList = sql.split(";");
			Connection conn = this.getConnection();
			for (String s: scriptList) {
				String sc = s.trim();
				if (sc.length() > 0) {
					logger.info(() -> "script=" + s);
					try (PreparedStatement st = conn.prepareStatement(s)) {
						st.execute();
					}
				}
			}
		}
	}

	/**
	 * テーブル再構築前のSQLの実行。
	 * @throws Exception 例外。
	 */
	public void executeBeforeRebuildSql() throws Exception {
		String beforeSql = this.getSqlGenerator().getBeforeRebildSql();
		try {
			this.executeSqlScript(beforeSql);
		} catch (Exception ex) {
			logger.error(ex.getMessage());
		}
	}

	/**
	 * テーブル再構築後のSQLの実行。
	 * @throws Exception 例外。
	 */
	public void executeAfterRebuildSql() throws Exception {
		String afterSql = this.getSqlGenerator().getAfterRebildSql();
		try {
			this.executeSqlScript(afterSql);
		} catch (Exception ex) {
			logger.error(ex.getMessage());
		}
	}
}
