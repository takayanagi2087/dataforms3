package jp.dataforms.fw.devtool.init.page;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jp.dataforms.fw.controller.EditForm;
import jp.dataforms.fw.controller.Page;
import jp.dataforms.fw.field.base.TextField;
import jp.dataforms.fw.servlet.DataFormsServlet;
import jp.dataforms.fw.util.ConfUtil;
import jp.dataforms.fw.util.ConfUtil.JndiDataSource;
import jp.dataforms.fw.util.FileUtil;
import jp.dataforms.fw.util.MessagesUtil;
import jp.dataforms.fw.validator.RequiredValidator;
import jp.dataforms.fw.validator.ValidationError;

/**
 * 開発ツール初期化 用フォームクラス。
 */
public class InitDevelopmentToolForm extends EditForm {
	/**
	 * プロジェクトパス。
	 */
	private static final String ID_PROJECT_PATH = "projectPath";
	/**
	 * Javaソースパス。
	 */
	private static final String ID_JAVA_SRC_PATH = "javaSrcPath";
	/**
	 * Webソースパス。
	 */
	private static final String ID_WEB_SRC_PATH = "webSrcPath";
	/**
	 * JNDI Prefix。
	 */
	private static final String ID_JNDI_PREFIX = "jndiPrefix";
	/**
	 * dataSource名称。
	 */
	private static final String ID_DATA_SOURCE = "dataSource";
	/**
	 * Apache DerbyのDB Path。
	 */
	private static final String ID_DERBY_DB_PATH = "derbyDbPath";

	/**
	 * Logger.
	 */
	private static Logger logger = LogManager.getLogger(InitDevelopmentToolForm.class);
	
	/**
	 * コンストラクタ。
	 */
	public InitDevelopmentToolForm() {
		this.addField(new TextField(ID_PROJECT_PATH)).addValidator(new RequiredValidator());
		this.addField(new TextField(ID_JAVA_SRC_PATH)).addValidator(new RequiredValidator());
		this.addField(new TextField(ID_WEB_SRC_PATH)).addValidator(new RequiredValidator());
		this.addField(new TextField(ID_JNDI_PREFIX)).addValidator(new RequiredValidator());
		this.addField(new TextField(ID_DATA_SOURCE)).addValidator(new RequiredValidator());
		this.addField(new TextField(ID_DERBY_DB_PATH)).addValidator(new RequiredValidator());
	}
	
	
	/**
	 * Eclipseを想定したプロジェクトパスを取得します。
	 * @return プロジェクトパス。
	 */
	private String getProjectPath() {
		String path = Page.getServlet().getServletContext().getRealPath("/");
		path = path.replaceAll("\\\\", "/");
		logger.debug("projectPath=" + path);
		Pattern p = Pattern.compile("^(.+)/\\.metadata/.+/(.+?)/");
		Matcher m = p.matcher(path);
		if (m.find()) {
			path = m.group(1) + "/" + m.group(2);
		}
		return path;
	}

	@Override
	public void init() throws Exception {
		super.init();
		this.setFormDataMap(this.queryData(new HashMap<String, Object>()));
	}
	
	@Override
	protected Map<String, Object> queryData(Map<String, Object> data) throws Exception {
		Map<String, Object> ret = new HashMap<String, Object>();
		String path = this.getProjectPath();
		
		File f = new File(path);
		String prjname = f.getName();
		
		ret.put(ID_PROJECT_PATH, path);
		ret.put(ID_JAVA_SRC_PATH, path + "/src/main/java");
		ret.put(ID_WEB_SRC_PATH, path + "/src/main/webapp");

		JndiDataSource ds = DataFormsServlet.getConf().getApplication().getJndiDataSource();
		ret.put(ID_JNDI_PREFIX, ds.getJndiPrefix());
		ret.put(ID_DATA_SOURCE, "jdbc/" + prjname);
		ret.put(ID_DERBY_DB_PATH, path + "/javadb");
		return ret;
	}

	/**
	 * パスが存在するかをチェックします。
	 * @param path パス。
	 * @return 存在する場合true。
	 */
	private boolean exists(final String path) {
		File f = new File(path);
		return f.exists();
	}
	
	
	@Override
	protected List<ValidationError> validateForm(Map<String, Object> data) throws Exception {
		List<ValidationError> list = super.validateForm(data);
		if (list.size() == 0) {
			String projectPath = (String) data.get(ID_PROJECT_PATH);
			if (!this.exists(projectPath)) {
				list.add(new ValidationError(ID_PROJECT_PATH, MessagesUtil.getMessage(getWebEntryPoint(), "error.filenotexists")));
			}
			String javaSrcPath = (String) data.get(ID_JAVA_SRC_PATH);
			if (!this.exists(javaSrcPath)) {
				list.add(new ValidationError(ID_JAVA_SRC_PATH, MessagesUtil.getMessage(getWebEntryPoint(), "error.filenotexists")));
			}
			String webSrcPath = (String) data.get(ID_WEB_SRC_PATH);
			if (!this.exists(webSrcPath)) {
				list.add(new ValidationError(ID_WEB_SRC_PATH, MessagesUtil.getMessage(getWebEntryPoint(), "error.filenotexists")));
			}
		}
		return list;
	}
	
	
	@Override
	protected boolean isUpdate(final Map<String, Object> data) throws Exception {
		return false;
	}
	
	/**
	 * web.xmlをコピーします。
	 * @param webSrcPath webのソースパス。
	 * @throws Exception 例外。
	 */
	private void copyWebXML(final String webSrcPath) throws Exception {
		ConfUtil util = new ConfUtil();
		String webxml = util.getWebXML();
		FileUtil.writeTextFile(webSrcPath + "/WEB-INF/web.xml", webxml, "utf-8");
	}
	
	/**
	 * log4j2.xmlをコピーします。
	 * @param javaSrcPath javaのソースパスを作成します。
	 * @throws Exception 例外。
	 */
	private void copyLog4j2XML(final String javaSrcPath) throws Exception {
		ConfUtil util = new ConfUtil();
		String log4j2xml = util.getLog4j2XML();
		FileUtil.writeTextFile(javaSrcPath + "/log4j2.xml", log4j2xml, "utf-8");
	}

	/**
	 * context.xmlを作成します。
	 * @param webSrcPath WEBソースパス。
	 * @param dataSource データソースの名称。
	 * @param derbyDbPath derbyのデータベースパス。
	 * @throws Exception 例外。
	 */
	private void copyContextXML(final String webSrcPath, final String dataSource, final String derbyDbPath) throws Exception {
		ConfUtil util = new ConfUtil();
		String contextxml = util.getContextXML();
		contextxml = contextxml.replaceAll("\\$\\{dataSource}", dataSource);
		contextxml = contextxml.replaceAll("\\$\\{dataBasePath}", derbyDbPath);
		FileUtil.writeTextFile(webSrcPath + "/META-INF/context.xml", contextxml, "utf-8");
	
	}
	
	/**
	 * 設定ファイルを作成します。
	 * @param webSrcPath webソースパス。
	 * @param javaSrcPath javaソースパス。
	 * @param jndiPrefix JNDI接頭辞
	 * @param dataSource データソース。
	 * @throws Exception 例外。
	 */
	private void copyConfFile(final String webSrcPath, final String javaSrcPath, final String jndiPrefix, final String dataSource) throws Exception {
		ConfUtil util = new ConfUtil();
		String conf = util.getDefaultConfFile();
		conf = conf.replaceAll("\"initialized\": false", "\"initialized\": true");
		conf = conf.replaceAll("\"javaSourcePath\": null", "\"javaSourcePath\": \"" + javaSrcPath + "\"");
		conf = conf.replaceAll("\"webSourcePath\": null", "\"webSourcePath\": \"" + webSrcPath + "\"");
		conf = conf.replaceAll("\"jndiPrefix\": \"java:/comp/env/\"", "\"jndiPrefix\": \"" + jndiPrefix + "\"");
		conf = conf.replaceAll("\"dataSource\": \"jdbc/dfdb\"", "\"dataSource\": \"" + dataSource + "\"");
		FileUtil.writeTextFile(webSrcPath + "/WEB-INF/dataforms.conf.jsonc", conf, "utf-8");
	}
	
	@Override
	protected void insertData(final Map<String, Object> data) throws Exception {
		String javaSrcPath = (String) data.get(ID_JAVA_SRC_PATH);
		String webSrcPath = (String) data.get(ID_WEB_SRC_PATH);
		String jndiPrefix = (String) data.get(ID_JNDI_PREFIX);
		String dataSource = (String) data.get(ID_DATA_SOURCE);
		String derbyDbPath = (String) data.get(ID_DERBY_DB_PATH);
		this.copyWebXML(webSrcPath);
		this.copyLog4j2XML(javaSrcPath);
		this.copyContextXML(webSrcPath, dataSource, derbyDbPath);
		this.copyConfFile(webSrcPath, javaSrcPath, jndiPrefix, dataSource);
	}
	
	@Override
	protected void updateData(Map<String, Object> data) throws Exception {
		// 更新処理　この処理は呼ばれない。
	}
	
	@Override
	public void deleteData(Map<String, Object> data) throws Exception {
		// 削除処理　この処理は呼ばれない。
	}
	
	@Override
	protected String getSavedMessage(Map<String, Object> data) {
		return null;
	}
}
