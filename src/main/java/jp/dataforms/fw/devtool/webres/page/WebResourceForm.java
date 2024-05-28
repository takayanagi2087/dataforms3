package jp.dataforms.fw.devtool.webres.page;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jp.dataforms.fw.annotation.WebMethod;
import jp.dataforms.fw.app.login.page.LoginInfoForm;
import jp.dataforms.fw.app.menu.page.SideMenuForm;
import jp.dataforms.fw.controller.DataForms;
import jp.dataforms.fw.controller.Form;
import jp.dataforms.fw.controller.Page;
import jp.dataforms.fw.controller.WebComponent;
import jp.dataforms.fw.devtool.base.page.DeveloperPage;
import jp.dataforms.fw.devtool.field.ClassNameField;
import jp.dataforms.fw.devtool.field.FieldLayoutSelectField;
import jp.dataforms.fw.devtool.field.JavascriptClassField;
import jp.dataforms.fw.devtool.field.PathNameField;
import jp.dataforms.fw.devtool.field.WebComponentTypeField;
import jp.dataforms.fw.devtool.field.WebSourcePathField;
import jp.dataforms.fw.devtool.webres.gen.FormHtmlGenerator;
import jp.dataforms.fw.field.common.FlagField;
import jp.dataforms.fw.field.common.PresenceField;
import jp.dataforms.fw.menu.FunctionMap;
import jp.dataforms.fw.response.JsonResponse;
import jp.dataforms.fw.servlet.DataFormsServlet;
import jp.dataforms.fw.util.ClassNameUtil;
import jp.dataforms.fw.util.FileUtil;
import jp.dataforms.fw.util.MessagesUtil;
import jp.dataforms.fw.util.PathUtil;
import jp.dataforms.fw.util.StringUtil;
import jp.dataforms.fw.validator.ValidationError;

// TODO:validatorの生成の際attachメソッドの生成は不要。
/**
 * Webリソース作成フォームクラス。
 *
 */
public class WebResourceForm extends Form {
	/**
	 * Log.
	 */
	private static Logger logger = LogManager.getLogger(WebResourceForm.class.getName());

	/**
	 * フィールドレイアウト。
	 */
	private static String fieldLayout = null;

	/**
	 * フィールドレイアウトの設定値を取得します。
	 */
	static {
		String layout = DataFormsServlet.getConf().getDevelopmentTool().getFieldLayout();
		WebResourceForm.fieldLayout = layout;
		logger.debug("fieldLayout:" + layout);
		if (WebResourceForm.fieldLayout == null) {
			WebResourceForm.fieldLayout = "GRID";
		}
	}

	/**
	 * フィールドレイアウトを取得します。
	 * @return フィールドレイアウト。
	 */
	public static String getFieldLayout() {
		return WebResourceForm.fieldLayout;
	}

	/**
	 * コンストラクタ。
	 */
	public WebResourceForm() {
		super(null);
		this.addField(new FieldLayoutSelectField("fieldLayout"));
		this.addField(new WebSourcePathField());
		this.addField(new ClassNameField()).setReadonly(true);
		this.addField(new WebComponentTypeField()).setReadonly(true);
		this.addField(new PathNameField("htmlPath")).setReadonly(true);
		this.addField(new PresenceField("htmlStatus")).setReadonly(true);
		this.addField(new FlagField("outputFormHtml"));
		this.addField(new PathNameField("javascriptPath")).setReadonly(true);
		this.addField(new PresenceField("javascriptStatus")).setReadonly(true);
		this.addField(new JavascriptClassField()).setReadonly(true);
		this.addField(new FlagField("forceOverwrite"));
	}

	@Override
	public void init() throws Exception {
		super.init();
		this.setFormData("fieldLayout", WebResourceForm.getFieldLayout());
		this.setFormData("webSourcePath", DeveloperPage.getWebSourcePath());
	}

	/**
	 * HTMLを作成します。
	 * @param p パラメータ。
	 * @return 処理結果。
	 * @throws Exception 例外。
	 */
	@WebMethod
	public JsonResponse generateHtml(final Map<String, Object> p) throws Exception {
		Map<String, Object> data = this.convertToServerData(p);
		String message = "";
		List<ValidationError> errlist = new ArrayList<ValidationError>();
		String srcpath = generateHtmlFile(data);
		if (srcpath != null) {
			message = MessagesUtil.getMessage(this.getPage(), "message.htmlgenerated", srcpath);
		} else {
			errlist.add(new ValidationError("htmlPath", MessagesUtil.getMessage(this.getPage(), "error.htmlalreadyexists")));
		}
		if (errlist.size() == 0) {
			JsonResponse r = new JsonResponse(JsonResponse.SUCCESS, message);
			return r;
		} else {
			JsonResponse r = new JsonResponse(JsonResponse.INVALID, errlist);
			return r;
		}
	}

	/**
	 * Pageクラスに対応したhtmlを作成する。
	 * @param className クラス名。
	 * @param sourcePath 出力先フォルダ。
	 * @param outputFormHtml Form別ファイル出力フラグ。
	 * @param fieldLayout フィールドレイアウト。
	 * @return HTMLテキスト。
	 * @throws Exception 例外。
	 */
	private String getDataformsHtml(final String className, final String sourcePath, final String outputFormHtml, final String fieldLayout) throws Exception {
		String src = this.getStringResourse("template/Page.html.template");
		Class<?> pageClass = Class.forName(className);
		DataForms page = (DataForms) pageClass.getDeclaredConstructor().newInstance();
		StringBuilder sb = new StringBuilder();
		List<WebComponent> clist = page.getComponentList();
		for (WebComponent c: clist) {
			if (c instanceof Form) {
				Form f = (Form) c;
				if (isCommonForm(f)) {
					continue;
				}
				if ("0".equals(outputFormHtml)) {
//					String srcpath = sourcePath + "/" + f.getClass().getName().replaceAll("\\.", "/") + ".html";
					String srcpath = sourcePath + "/" + this.getWebResourcePath(f.getClass()) + ".html";
					File srcfile = new File(srcpath);
					srcfile.delete();
				}
				FormHtmlGenerator gen = FormHtmlGenerator.newFormHtmlGenerator(f, 3, fieldLayout);
				sb.append(gen.generateFormHtml(outputFormHtml));
			}
		}
		if (page instanceof Page) {
			Page p = (Page) page;
			src = src.replaceAll("\\$\\{pageName\\}", p.getPageName());
			src = src.replaceAll("\\$\\{description\\}", p.getPageDescription());
		}
		src = src.replaceAll("\\$\\{forms\\}", sb.toString());
		return src;
	}

	/**
	 * フォームを出力します。
	 * @param f フォーム。
	 * @param sourcePath ファイルの出力先。
	 * @param forceOverwrite 強制上書きフラグ。
	 * @param fieldLayout フィールドレイアウト。
	 * @return 出力ファイル。
	 * @throws Exception 例外。
	 */
	private String outputFormHtml(final Form f, final String sourcePath, final String forceOverwrite, final String fieldLayout) throws Exception {
		String src = this.getStringResourse("template/Form.html.template");
		FormHtmlGenerator gen = FormHtmlGenerator.newFormHtmlGenerator(f, 2, fieldLayout);
		StringBuilder sb = new StringBuilder();
		sb.append(gen.generateFormHtml("0"));
		String gensrc = src.replaceAll("\\$\\{form\\}", sb.toString());
//		String srcpath = sourcePath + "/" + f.getClass().getName().replaceAll("\\.", "/") + ".html";
		String srcpath = sourcePath + "/" + this.getWebResourcePath(f.getClass()) + ".html";
		File file = new File(srcpath);
		if ((!file.exists()) || "1".equals(forceOverwrite)) {
			if (!file.getParentFile().exists()) {
				file.getParentFile().mkdirs();
			}
			FileUtil.writeTextFileWithBackup(srcpath, gensrc, DataFormsServlet.getEncoding());
		} else {
			srcpath = null;
		}
		return srcpath;
	}

	/**
	 * ページまたはダイアログクラス中のフォームを別HTMLに出力します。
	 * @param className ページクラス名。
	 * @param sourcePath ファイルの出力パス。
	 * @param forceOverwrite 強制上書きフラグ。
	 * @param fieldLayout フィールドレイアウト。
	 * @throws Exception 例外。
	 */
	private void outputForms(final String className, final String sourcePath, final String forceOverwrite, final String fieldLayout) throws Exception {
		Class<?> pageClass = Class.forName(className);
		DataForms page = (DataForms) pageClass.getDeclaredConstructor().newInstance();
		List<WebComponent> clist = page.getComponentList();
		for (WebComponent c: clist) {
			if (c instanceof Form) {
				Form f = (Form) c;
				if (isCommonForm(f)) {
					continue;
				}
				logger.debug("f.getClass().getName()=" + f.getClass().getName());
				this.outputFormHtml(f, sourcePath, forceOverwrite, fieldLayout);
			}
		}
	}

	/**
	 * 生成対象外の共通フォームかどうかを判定します。
	 * @param f フォーム。
	 * @return 生成対象外の共通フォームの場合true。
	 */
	private boolean isCommonForm(final Form f) {
		return f instanceof LoginInfoForm || f instanceof SideMenuForm;
	}


	/**
	 * HTMLファイルを作成します。
	 * @param data データ。
	 * @return 出力されたファイル。
	 * @throws Exception 例外。
	 */
	private String generateHtmlFile(final Map<String, Object> data) throws Exception {
		String fieldLayout = (String) data.get("fieldLayout");
		String webComponentType = (String) data.get("webComponentType");
		String outputFormHtml = (String) data.get("outputFormHtml");
		String forceOverwrite = (String) data.get("forceOverwrite");
		String sourcePath = (String) data.get("webSourcePath");
		String fullClassName = (String) data.get("className");
		String htmlPath = (String) data.get("htmlPath");
		String gensrc = "";
		if ("page".equals(webComponentType) || "dialog".equals(webComponentType)) {
			gensrc = this.getDataformsHtml(fullClassName, sourcePath, outputFormHtml, fieldLayout);
			logger.debug("outputFormHtml=" + outputFormHtml);
			if ("1".equals(outputFormHtml)) {
				this.outputForms(fullClassName, sourcePath, forceOverwrite, fieldLayout);
			}
			String srcpath = sourcePath + htmlPath;
			File f = new File(srcpath);
			if ((!f.exists()) || "1".equals(forceOverwrite)) {
				if (!f.getParentFile().exists()) {
					f.getParentFile().mkdirs();
				}
				FileUtil.writeTextFileWithBackup(srcpath, gensrc, DataFormsServlet.getEncoding());
				return srcpath;
			} else {
				return null;
			}
		} else if ("form".equals(webComponentType)) {
			Class<?> fcls = Class.forName(fullClassName);
			Form f = (Form) fcls.getConstructor().newInstance();
			return 	this.outputFormHtml(f, sourcePath, forceOverwrite, fieldLayout);
		}
		return null;
	}


	/**
	 * Javascriptを作成します。
	 * @param p パラメータ。
	 * @return 処理結果。
	 * @throws Exception 例外
	 */
	@WebMethod
	public JsonResponse generateJavascript(final Map<String, Object> p) throws Exception {
		Map<String, Object> data = this.convertToServerData(p);
		String message = "";
		List<ValidationError> errlist = new ArrayList<ValidationError>();
		String srcpath = generateJavascriptFile(data);
		if (srcpath != null) {
			message = MessagesUtil.getMessage(this.getPage(), "message.javascriptgenerated", srcpath);
		} else {
			errlist.add(new ValidationError("javascriptPath", MessagesUtil.getMessage(this.getPage(), "error.javascriptalreadyexists")));
		}
		if (errlist.size() == 0) {
			JsonResponse r = new JsonResponse(JsonResponse.SUCCESS, message);
			return r;
		} else {
			JsonResponse r = new JsonResponse(JsonResponse.INVALID, errlist);
			return r;
		}
	}

	/**
	 * テンプレートファイルを取得します。
	 * @param name リソース名。
	 * @return テンプレートの内容。
	 * @throws Exception 例外。
	 */
	private String getTemplate(final String name) throws Exception {
		String ret = "";
		Class<?> cls = this.getClass();
		InputStream is = cls.getResourceAsStream(name);
		if (is != null) {
			try {
				ret = new String(FileUtil.readInputStream(is), DataFormsServlet.getEncoding());
			} finally {
				is.close();
			}
		}
		return ret;
	}

	/**
	 * Javascriptのソーステンプレートを取得します。
	 * @param cls Javaクラス。
	 * @return テンプレートの文字列。
	 * @throws Exception 例外。
	 */
	private String getTemplate(final Class<?> cls) throws Exception {
		String template = null;
		Class<?> superClass = cls.getSuperclass();
		while (true) {
			String classname = superClass.getSimpleName();
			logger.debug(() -> "template class=" + classname);
			String src = this.getTemplate("template/" + classname + ".js.template");
			logger.debug(() -> "src=" + src);
			if (!StringUtil.isBlank(src)) {
				template = src;
				break;
			}
			superClass = superClass.getSuperclass();
		}
		return template;
	}

	
	/**
	 * Javascriptのモジュールインポートの構文を作成します。
	 * @param cls クラス。
	 * @return Javascriptのモジュールインポートの構文。
	 */
	private String getImportModule(final Class<?> cls) {
		String superClassName = cls.getSuperclass().getName();
		String className = cls.getName();

		FunctionMap conv = WebComponent.getFunctionMap();
		String sscript = conv.getWebPath(superClassName) + ".js";
		String script = conv.getWebPath(className) + ".js";
		logger.debug("sscript=" + sscript);
		logger.debug("script=" + script);
		
		String jspath = PathUtil.getRelativePath(script, sscript);
		String ret = "import { " + cls.getSuperclass().getSimpleName() + " } from '" + jspath + "';";
		return ret;
	}
	
	/**
	 * javascriptファイルを作成します。
	 * @param data データ。
	 * @return 出力されたファイル。
	 * @throws Exception 例外。
	 *
	 */
	private String generateJavascriptFile(final Map<String, Object> data) throws Exception {
		String setFormData = this.getTemplate("template/setFormData.js.template");
		String validateForm = this.getTemplate("template/validateForm.js.template");
		String buttonHandler = this.getTemplate("template/buttonHandler.js.template");
		String callWebMethod = this.getTemplate("template/callWebMethod.js.template");
		String onCalc = this.getTemplate("template/onCalc.js.template");

		String forceOverwrite = (String) data.get("forceOverwrite");
		String sourcePath = (String) data.get("webSourcePath");
		String fullClassName = (String) data.get("className");
		String javascriptPath = (String) data.get("javascriptPath");
		Class<?> cls = Class.forName(fullClassName);
		cls.getSuperclass().getSimpleName();
		String superClassName = cls.getSuperclass().getSimpleName();
		String className = ClassNameUtil.getSimpleClassName(fullClassName);
		String src = this.getTemplate(cls);
		String gensrc = src.replaceAll("\\$\\{className\\}", className);
		String moduleName = this.getImportModule(cls);
		gensrc = gensrc.replaceAll("\\$\\{importModule\\}", moduleName);
		gensrc = gensrc.replaceAll("\\$\\{superClassName\\}", superClassName);
		gensrc = gensrc.replaceAll("\\$\\{setFormData\\}", setFormData);
		gensrc = gensrc.replaceAll("\\$\\{validateForm\\}", validateForm);
		gensrc = gensrc.replaceAll("\\$\\{buttonHandler\\}", buttonHandler);
		gensrc = gensrc.replaceAll("\\$\\{callWebMethod\\}", callWebMethod);
		gensrc = gensrc.replaceAll("\\$\\{onCalc\\}", onCalc);
		String srcpath = sourcePath + javascriptPath;
		File f = new File(srcpath);
		if ((!f.exists()) || "1".equals(forceOverwrite)) {
			if (!f.getParentFile().exists()) {
				f.getParentFile().mkdirs();
			}
			FileUtil.writeTextFileWithBackup(srcpath, gensrc, DataFormsServlet.getEncoding());
			return srcpath;
		} else {
			return null;
		}
	}

}
