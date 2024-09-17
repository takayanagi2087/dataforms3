package jp.dataforms.fw.devtool.table.page;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Modifier;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jp.dataforms.fw.annotation.WebMethod;
import jp.dataforms.fw.controller.EditForm;
import jp.dataforms.fw.dao.Dao;
import jp.dataforms.fw.dao.Dao.TableInfoEntity;
import jp.dataforms.fw.dao.Entity;
import jp.dataforms.fw.dao.Table;
import jp.dataforms.fw.devtool.base.page.DeveloperPage;
import jp.dataforms.fw.devtool.db.dao.TableManagerDao;
import jp.dataforms.fw.devtool.entity.EntityGenerator;
import jp.dataforms.fw.devtool.field.DbTableNameField;
import jp.dataforms.fw.devtool.field.FunctionSelectField;
import jp.dataforms.fw.devtool.field.JavaSourcePathField;
import jp.dataforms.fw.devtool.field.OverwriteModeField;
import jp.dataforms.fw.devtool.field.PackageNameField;
import jp.dataforms.fw.devtool.field.TableClassNameField;
import jp.dataforms.fw.field.base.Field;
import jp.dataforms.fw.field.common.CreateTimestampField;
import jp.dataforms.fw.field.common.CreateUserIdField;
import jp.dataforms.fw.field.common.FlagField;
import jp.dataforms.fw.field.common.UpdateTimestampField;
import jp.dataforms.fw.field.common.UpdateUserIdField;
import jp.dataforms.fw.field.sqltype.VarcharField;
import jp.dataforms.fw.response.BinaryResponse;
import jp.dataforms.fw.response.JsonResponse;
import jp.dataforms.fw.response.Response;
import jp.dataforms.fw.servlet.DataFormsServlet;
import jp.dataforms.fw.util.ClassNameUtil;
import jp.dataforms.fw.util.ConfUtil.JndiDataSource;
import jp.dataforms.fw.util.FileUtil;
import jp.dataforms.fw.util.ImportUtil;
import jp.dataforms.fw.util.JsonUtil;
import jp.dataforms.fw.util.MessagesUtil;
import jp.dataforms.fw.util.StringUtil;
import jp.dataforms.fw.validator.RequiredValidator;
import jp.dataforms.fw.validator.ValidationError;

/**
 * 編集フォームクラス。
 *
 */
public class TableGeneratorEditForm extends EditForm {
	/**
	 * 更新情報フラグ。
	 */
	private static final String ID_UPDATE_INFO_FLAG = "updateInfoFlag";

	/**
	 * 主キー自動生成フラグ。
	 */
	private static final String ID_AUTO_INCREMENT_ID = "autoIncrementId";


	/**
	 * インポートテーブル名。
	 */
	public static final String ID_IMPORT_TABLE = "importTable";


	/**
	 * Logger.
	 */
	private Logger logger = LogManager.getLogger(TableGeneratorEditForm .class);

	/**
	 * コンストラクタ。
	 */
	public TableGeneratorEditForm() {
		this.addField(new JavaSourcePathField());
//		this.addField(new ExistingFolderField("javaSourcePath")).setReadonly(true).addValidator(new RequiredValidator());
		this.addField((new FunctionSelectField()).setPackageOption("dao"));
		this.addField(new PackageNameField()).addValidator(new RequiredValidator());
		this.addField(new VarcharField("tableComment", 256));
		this.addField(new TableClassNameField()).setComment("テーブルクラス名").setAutocomplete(false).addValidator(new RequiredValidator());
		this.addField(new OverwriteModeField());
		this.addField(new FlagField(ID_AUTO_INCREMENT_ID)).setComment("主キー自動生成フラグ");
		this.addField(new FlagField(ID_UPDATE_INFO_FLAG)).setComment("更新情報フィールド");
		this.addField(new DbTableNameField(ID_IMPORT_TABLE)).setAutocomplete(true);
		FieldListHtmlTable htmltbl = new FieldListHtmlTable();
		this.addHtmlTable(htmltbl);
//		this.addField(new FlagField(ID_NOT_UPDATE_CONSTRACTOR));
//		this.addField(new FlagField(ID_NOT_GENERATE_ENTITY));
		this.setFormData(htmltbl.getId(), new ArrayList<Map<String, Object>>());
	}

	@Override
	public void init() throws Exception {
		super.init();
		this.setFormData("overwriteMode", OverwriteModeField.ERROR);
		this.setFormData("javaSourcePath", DeveloperPage.getJavaSourcePath());
		this.setFormData(ID_AUTO_INCREMENT_ID, "1");
		this.setFormData(ID_UPDATE_INFO_FLAG, "1");
	}

	@Override
	protected Map<String, Object> queryData(final Map<String, Object> data) throws Exception {
		Map<String, Object> ret = new HashMap<String, Object>();
		ret.putAll(data);
		String packageName = (String) data.get("packageName");
		String tableClassName = (String) data.get("tableClassName");
		String fullClassName = packageName + "." + tableClassName;
		ret.put("overwriteMode", OverwriteModeField.ERROR);
		Class<?> cls = Class.forName(fullClassName);
		Table tbl = (Table) cls.getDeclaredConstructor().newInstance();
		if (tbl.isAutoIncrementId()) {
			ret.put(ID_AUTO_INCREMENT_ID, "1");
		} else {
			ret.put(ID_AUTO_INCREMENT_ID, "0");
		}
		ret.put("tableComment", tbl.getComment());
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		int no = 1;
		for (Field<?> f: tbl.getFieldList()) {
			if (f instanceof CreateUserIdField
				|| f instanceof CreateTimestampField
				|| f instanceof UpdateUserIdField
				|| f instanceof UpdateTimestampField) {
				continue;
			}
			Map<String, Object> m = new HashMap<String, Object>();
			m.put("no", Integer.valueOf(no++));
			String className = f.getClass().getName();
			m.put("packageName", ClassNameUtil.getPackageName(className));
			m.put("fieldClassName", ClassNameUtil.getSimpleClassName(className));
			String baseClassName = f.getClass().getSuperclass().getName();
			m.put("superPackageName", ClassNameUtil.getPackageName(baseClassName));
			m.put("superSimpleClassName", ClassNameUtil.getSimpleClassName(baseClassName));
			String pkflg = "0";
			if (tbl.getPkFieldList().get(f.getId()) != null) {
				pkflg = "1";
			}
			m.put("isDataformsField", (Field.isDataformsField(className) ? "1" : "0"));
			m.put("pkFlag", pkflg);
			m.put("notNullFlag", (f.isNotNull() ? "1" : "0"));
			m.put("fieldLength", f.getLengthParameter());
			if (!f.idIsDefault()) {
				m.put("fieldId", f.getId());
			} else {
				m.put("fieldId", "");
			}
			m.put("comment", f.getComment());
			m.put("overwriteMode", OverwriteModeField.ERROR);
			list.add(m);
		}
		ret.put("fieldList", list);
		ret.put("javaSourcePath", DeveloperPage.getJavaSourcePath());


		if (tbl.getField(Entity.ID_UPDATE_TIMESTAMP) == null) {
			ret.put("updateInfoFlag", "0");
		} else {
			ret.put("updateInfoFlag", "1");
		}

		return ret;
	}


	@Override
	public void deleteData(final Map<String, Object> data) throws Exception {

	}

	/**
	 * 指定されたフィールドクラスの情報を返します。
	 * @param param パラメータ。
	 * @return 判定結果。
	 * @throws Exception 例外。
	 */
	@WebMethod
	public JsonResponse getFieldClassInfo(final Map<String, Object> param) throws Exception {
		try {
			String classname = (String) param.get("classname");
			Map<String, Object> ret = new HashMap<String, Object>();
			Boolean isDataformsField = Field.isDataformsField(classname);
			ret.put("isDataformsField", (isDataformsField ? "1" : "0"));
			@SuppressWarnings("unchecked")
			Class<? extends Field<?>> cls = (Class<? extends Field<?>>) Class.forName(classname);
			Class<?> scls = cls.getSuperclass();
			String superClassPackage = scls.getPackage().getName();
			String superClassSimpleName = scls.getSimpleName();
			ret.put("superClassPackage", superClassPackage);
			ret.put("superClassSimpleName", superClassSimpleName);
			Field<?> field = Field.newFieldInstance(cls);
			// dataforms3.jarの提供するFieldクラスを直接指定する場合。
			ret.put("fieldLength", field.getLengthParameter());
			ret.put("fieldComment", field.getComment());
			JsonResponse result = new JsonResponse(JsonResponse.SUCCESS, ret);
			return result;
		} catch (ClassNotFoundException ex) {
			// 未定義のフィールドの場合。
			Map<String, Object> ret = new HashMap<String, Object>();
			ret.put("isDataformsField", "0");
			JsonResponse result = new JsonResponse(JsonResponse.SUCCESS, ret);
			return result;
		}
	}


	//
	/**
	 * 指定されたフィールドの親クラスの情報を取得します。
	 * @param param パラメータ。
	 * @return 判定結果。
	 * @throws Exception 例外。
	 */
	@WebMethod
	public JsonResponse getSuperFieldClassInfo(final Map<String, Object> param) throws Exception {
		JsonResponse result = null;
		Map<String, Object> ret = new HashMap<String, Object>();
		try {
			String classname = (String) param.get("superclassname");
			@SuppressWarnings("unchecked")
			Class<? extends Field<?>> cls = (Class<? extends Field<?>>) Class.forName(classname);
			Field<?> field = Field.newFieldInstance(cls);
			ret.put("fieldLength", field.getLengthParameter());
			result = new JsonResponse(JsonResponse.SUCCESS, ret);
		} catch (Exception ex) {
			result = new JsonResponse(JsonResponse.SUCCESS, ret);
		}
		return result;
	}

	/**
	 * ソースファイルの存在チェック。
	 * @param data データ。
	 * @return チェック結果。
	 * @throws Exception 例外。
	 */
	private List<ValidationError> validateSourceExistence(final Map<String, Object> data) throws Exception {
		List<ValidationError> ret = new ArrayList<ValidationError>();
		logger.debug("data=\n" + JsonUtil.encode(data, true));
		String packageName = (String) data.get("packageName");
		String javaSrc = (String) data.get("javaSourcePath");
		{
			String srcPath = javaSrc + "/" + packageName.replaceAll("\\.", "/");
			String tableClassName = (String) data.get("tableClassName");
			String overwriteMode = (String) data.get("overwriteMode");
			if (OverwriteModeField.ERROR.equals(overwriteMode)) {
				File tbl = new File(srcPath + "/" + tableClassName + ".java");
				if (tbl.exists()) {
					ret.add(new ValidationError("tableClassName", this.getPage().getMessage("error.sourcefileexist", tableClassName + ".java")));
				}
			}
		}
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> fieldList = (List<Map<String, Object>>) data.get("fieldList");
//		for (Map<String, Object> m: fieldList) {
		for (int i = 0; i < fieldList.size(); i++) {
			Map<String, Object> m = fieldList.get(i);
			String isDataformsField = (String) m.get("isDataformsField");
			if (!"1".equals(isDataformsField)) {
				String overwriteMode = (String) m.get("overwriteMode");
				if (OverwriteModeField.ERROR.equals(overwriteMode)) {
					String fieldPackageName = (String) m.get("packageName");
					String fieldClassName = (String) m.get("fieldClassName");
					String fldSrcPath = javaSrc + "/" + fieldPackageName.replaceAll("\\.", "/");
					File fld = new File(fldSrcPath + "/" + fieldClassName + ".java");
					if (fld.exists()) {
						ret.add(new ValidationError("fieldList[" + i + "].fieldClassName", this.getPage().getMessage("error.sourcefileexist", fieldClassName + ".java")));
					}
				}
			}
		}
		return ret;
	}

	/**
	 * 長さパラメータのチェックパターンを取得します。
	 * @param className クラス名。
	 * @return チェックパターン。
	 * @throws Exception 例外。
	 */
	private String getLengthParameterPattern(final String className) throws Exception {
		String pat = null;
		@SuppressWarnings("unchecked")
		Class<? extends Field<?>> fcls = (Class<? extends Field<?>>) Class.forName(className);
		if ((fcls.getModifiers() & Modifier.ABSTRACT) == 0) {
			Field<?> field = Field.newFieldInstance(fcls);
			// アプリケーション用のフィールドを更新
			if (Field.hasLengthParameter(fcls)) {
				pat = field.getLengthParameterPattern();
			}
		}
		return pat;
	}

	/**
	 * フィールドのオプションパラメータのチェックを行います。
	 * @param data フォームデータ。
	 * @return チェック結果。
	 * @throws Exception 例外。
	 */
	private List<ValidationError> validateFieldOption(final Map<String, Object> data) throws Exception {
		List<ValidationError> ret = new ArrayList<ValidationError>();
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> fieldList = (List<Map<String, Object>>) data.get("fieldList");
		for (int i = 0; i < fieldList.size(); i++) {
			Map<String, Object> m = (Map<String, Object>) fieldList.get(i);
			String pat = null;
			String isDataformsField = (String) m.get("isDataformsField");
			if ("1".equals(isDataformsField)) {
				String fieldPackageName = (String) m.get("packageName");
				String fieldClassSimpleName = (String) m.get("fieldClassName");
				String className = fieldPackageName + "." + fieldClassSimpleName;
				pat = this.getLengthParameterPattern(className);
			} else {
				// アプリケーション用フィールドの新規作成時の対応。
				// 親クラスに長さパラメータがある場合チェックパターンを取得する。
				String superPackageName = (String) m.get("superPackageName");
				String superSimpleClassName = (String) m.get("superSimpleClassName");
				String className = superPackageName + "." + superSimpleClassName;
				pat = this.getLengthParameterPattern(className);
			}
			logger.debug("pattern=" + pat);
			if (!StringUtil.isBlank(pat)) {
				String fieldLength = (String) m.get("fieldLength");
				if (!Pattern.matches(pat, fieldLength)) {
					ret.add(new ValidationError("fieldList[" + i + "].fieldLength", this.getPage().getMessage("error.regexp")));
				}
			} else {
				String fieldLength = (String) m.get("fieldLength");
				if (!StringUtil.isBlank(fieldLength)) {
					ret.add(new ValidationError("fieldList[" + i + "].fieldLength", this.getPage().getMessage("error.regexp")));
				}
			}
		}
		return ret;
	}

	@Override
	public List<ValidationError> validate(final Map<String, Object> param) throws Exception {
		List<ValidationError> ret = super.validate(param);
		if (ret.size() == 0) {
			// 追加のバリデーション。
			Map<String, Object> data = this.convertToServerData(param);
			ret.addAll(this.validateFieldOption(data));
			if (ret.size() == 0) {
				ret.addAll(this.validateSourceExistence(data));

			}
		}
		return ret;
	}

	@Override
	protected boolean isUpdate(final Map<String, Object> data) throws Exception {
		return false;
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
	 * フィールドクラスに応じたテンプレートを取得します。
	 * @param superClassName スーパークラス名。
	 * @return テンプレート。
	 * @throws Exception 例外。
	 */
	private String getFieldTemplate(final String superClassName) throws Exception {
		Class<?> c = Class.forName(superClassName);
		String ret = "";
		while (!"WebComponent".equals(c.getSimpleName())) {
			ret = this.getTemplate("template/" + c.getSimpleName() + ".java.template");
			if (!StringUtil.isBlank(ret)) {
				break;
			}
			c = c.getSuperclass();
		}
		return ret;
	}

	/**
	 * フィールドクラスの作成を行います。
	 * @param m フィールド情報。
	 * @return 生成されたソース。
	 * @throws Exception 例外。
	 *
	 */
	private String generateFieldClass(final Map<String, Object> m) throws Exception {
//		String fsrc = this.getTemplate("template/Field.java.template");
		String superPackageName = (String) m.get("superPackageName");
		String superSimpleClassName = (String) m.get("superSimpleClassName");
		String fsrc = this.getFieldTemplate(superPackageName + "." + superSimpleClassName);
		String fieldPackageName = (String) m.get("packageName");
		String fieldClassSimpleName = (String) m.get("fieldClassName");
		String fieldLength = (String) m.get("fieldLength");
		String importList = "";
		String constList = "";
		String validators = "";
		if (!StringUtil.isBlank(fieldLength)) {
			if (fieldLength.indexOf(",") < 0) {
				constList = "\t/**\n";
				constList += "\t * フィールド長。\n";
				constList += "\t */\n";
				constList += "\tprivate static final int LENGTH = " + fieldLength + ";\n";
				validators = "\t\tthis.addValidator(new MaxLengthValidator(this.getLength()));\n";
				importList = "import jp.dataforms.fw.validator.MaxLengthValidator;\n";
				fieldLength = ", LENGTH";
			} else {
				fieldLength = ", " + fieldLength.replaceAll(",", ", ");
			}
		}
		String fieldComment = (String) m.get("comment");
		String superClassName = superPackageName + "." + superSimpleClassName;
		String isDataformsField = (String) m.get("isDataformsField");
		if (!"1".equals(isDataformsField)) {
			String webMethod = this.getTemplate("template/webMethod.java.template");
			fsrc = fsrc.replaceAll("\\$\\{fieldPackageName\\}", fieldPackageName);
			fsrc = fsrc.replaceAll("\\$\\{superClassName\\}", superClassName);
			fsrc = fsrc.replaceAll("\\$\\{importList\\}", importList);
			fsrc = fsrc.replaceAll("\\$\\{constList\\}", constList);
			fsrc = fsrc.replaceAll("\\$\\{fieldClassSimpleName\\}", fieldClassSimpleName);
			fsrc = fsrc.replaceAll("\\$\\{superSimpleClassName\\}", superSimpleClassName);
			fsrc = fsrc.replaceAll("\\$\\{fieldComment\\}", fieldComment);
			fsrc = fsrc.replaceAll("\\$\\{fieldLength\\}", fieldLength);
			fsrc = fsrc.replaceAll("\\$\\{validators\\}", validators);
			fsrc = fsrc.replaceAll("\\$\\{webMethod\\}", webMethod);
			logger.debug("fsrc=\n" + fsrc);
		} else {
			fsrc = null;
		}
		return fsrc;
	}

	@Override
	protected void insertData(final Map<String, Object> data) throws Exception {
		logger.debug("data=" + JsonUtil.encode(data, true));
		this.writeTableJavaSource(data);
	}

	/**
	 * フィールドIDを取得します。
	 * @param m フィールドリスト。
	 * @return フィールドID。
	 */
	private String getFieldId(final Map<String, Object> m) {
		String fieldId = (String) m.get("fieldId");
		if (StringUtil.isBlank(fieldId)) {
			String fclass = (String) m.get("fieldClassName");
			StringBuilder fsb = new StringBuilder(fclass.replaceAll("Field$", ""));
			char c = fsb.charAt(0);
			fsb.setCharAt(0, Character.toLowerCase(c));
			fieldId = fsb.toString();
		}
		return fieldId;
	}


	/**
	 * フィールドIdの定数を展開します。
	 * @param list フィールドリスト。
	 * @return フィールドIDの定数値。
	 */
//	private String generateFieldIdConstant(final List<Map<String, Object>> list) {
//		StringBuilder sb = new StringBuilder();
//		for (Map<String, Object> m: list) {
//			String fieldId = getFieldId(m);
//			String comment = (String) m.get("comment");
//			sb.append("\t\t/** " + comment + "のフィールドID。 */\n");
//			sb.append("\t\tpublic static final String ID_");
//			sb.append(StringUtil.camelToUpperCaseSnake(fieldId));
//			sb.append(" = \"");
//			sb.append(fieldId);
//			sb.append("\";\n");
//		}
//		return sb.toString();
//	}




	/**
	 * Table.javaのソースを作成します。
	 * @param data POSTされたデータ。
	 * @throws Exception 例外。
	 */
	public void writeTableJavaSource(final Map<String, Object> data) throws Exception {
		String tsrc = this.getTemplate("template/Table.java.template");
		String tableOverwriteMode = (String) data.get("overwriteMode");
		String packageName = (String) data.get("packageName");
		String tableClassName = (String) data.get("tableClassName");
		String autoIncrementId = (String) data.get(ID_AUTO_INCREMENT_ID);
		String tableComment = (String) data.get("tableComment");
		tsrc = tsrc.replaceAll("\\$\\{packageName\\}", packageName);
		tsrc = tsrc.replaceAll("\\$\\{tableComment\\}", tableComment);
		tsrc = tsrc.replaceAll("\\$\\{TableClassShortName\\}", tableClassName);
		ImportUtil implist = new ImportUtil();
		StringBuilder constructor = new StringBuilder();
		if ("1".equals(autoIncrementId)) {
			constructor.append("\t\tthis.setAutoIncrementId(true);\n");
		}
		constructor.append("\t\tthis.setComment(\"" + tableComment + "\");\n");
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> fieldList = (List<Map<String, Object>>) data.get("fieldList");
		Map<String, String> srcmap = new HashMap<String, String>();
		for (Map<String, Object> m: fieldList) {
			String isDataformsField = (String) m.get("isDataformsField");
			if (!"1".equals(isDataformsField)) {
				String overwriteMode = (String) m.get("overwriteMode");
				if (!OverwriteModeField.SKIP.equals(overwriteMode)) {
					String fldsrc = this.generateFieldClass(m);
					String fieldPackageName = (String) m.get("packageName");
					String fieldClassSimpleName = (String) m.get("fieldClassName");
					srcmap.put(fieldPackageName + "." + fieldClassSimpleName, fldsrc);
				}
			}
			String fpackage = (String) m.get("packageName");
			String fclass = (String) m.get("fieldClassName");
			String pkFlag = (String) m.get("pkFlag");
			String notNullFlag = (String) m.get("notNullFlag");
			String fieldId = (String) m.get("fieldId");
			String fieldLength = (String) m.get("fieldLength");
			implist.add(fpackage + "." + fclass);
			if ("1".equals(pkFlag)) {
				constructor.append("\t\tthis.addPkField(new ");
			} else {
				constructor.append("\t\tthis.addField(new ");
			}
			constructor.append(fclass);
			constructor.append("(");
			if (!StringUtil.isBlank(fieldId)) {
				constructor.append("\"" + fieldId + "\"");
			}
			if ("1".equals(isDataformsField)) {
				if (!StringUtil.isBlank(fieldLength)) {
					constructor.append(", ");
					constructor.append(fieldLength);
				}
			}
			String comment = (String) m.get("comment");
			if ("0".equals(notNullFlag)) {
				constructor.append(")); //" + comment + "\n");
			} else {
				constructor.append(")).setNotNull(true); //" + comment + "\n");
			}
		}
		String flg = (String) data.get(ID_UPDATE_INFO_FLAG);
		if ("1".equals(flg)) {
			constructor.append("\t\tthis.addUpdateInfoFields();");
		}
		tsrc = tsrc.replaceAll("\\$\\{constructor\\}", constructor.toString());
		EntityGenerator gen = new EntityGenerator(fieldList);
		String entityClass = gen.generate(
			implist, null
			, (Map<String, Object> m) -> {
				return this.getFieldId(m);
			}
			, (Map<String, Object> m) -> {
				String superPackageName = (String) m.get("superPackageName");
				String superSimpleClassName = (String) m.get("superSimpleClassName");
				return superPackageName + "." + superSimpleClassName;
			}
		);
		tsrc = tsrc.replaceAll("\\$\\{entity\\}", entityClass);
		tsrc = tsrc.replaceAll("\\$\\{importList\\}", implist.getImportText());
		logger.debug("tsrc=\n{}", tsrc);
		if (!OverwriteModeField.SKIP.equals(tableOverwriteMode)) {
			srcmap.put(packageName + "." + tableClassName, tsrc);
		}
		String rclass = packageName + "." + tableClassName + "Relation";

		String path = (String) data.get("javaSourcePath");
		File relationFile = new File(path + "/" + rclass.replaceAll("\\.", "/") + ".java");
		if (!relationFile.exists()) {
			srcmap.put(rclass, this.generateRelationJavaSource(data));
		}
		logger.debug("srcmap=" + JsonUtil.encode(srcmap));
		this.writeJavaSource(path, srcmap);
	}


	/**
	 * Relation.javaのソースを作成します。
	 * @param data データ。
	 * @return Relation.javaのソース。
	 * @throws Exception 例外。
	 */
	private String generateRelationJavaSource(final Map<String, Object> data) throws Exception {
		String rsrc = this.getTemplate("template/Relation.java.template");
		String packageName = (String) data.get("packageName");
		String tableClassName = (String) data.get("tableClassName");
		rsrc = rsrc.replaceAll("\\$\\{packageName\\}", packageName);
		rsrc = rsrc.replaceAll("\\$\\{TableClassShortName\\}", tableClassName);
		return rsrc;
	}


	/**
	 * javaソース出力。
	 * @param path javaソースパス。
	 * @param srcmap ソースのマップ情報。
	 * @throws Exception 例外。
	 */
	private void writeJavaSource(final String path, final Map<String, String> srcmap) throws Exception {
		for (String key: srcmap.keySet()) {
			String srcPath = path + "/" + key.replaceAll("\\.", "/") + ".java";
			FileUtil.writeTextFileWithBackup(srcPath, srcmap.get(key), DataFormsServlet.getEncoding());
		}
	}

	@Override
	protected void updateData(final Map<String, Object> data) throws Exception {

	}

	@Override
	protected String getSavedMessage(final Map<String, Object> data) {
		return MessagesUtil.getMessage(this.getPage(), "message.javasourcecreated");
	}

	/**
	 * テーブル定義書を作成します。
	 * @param param パラメータ。
	 * @return テーブル定義書Excelイメージ。
	 * @throws Exception 例外。
	 */
	@WebMethod
	public Response print(final Map<String, Object> param) throws Exception {
		Response ret = null;
		File template = TableReport.makeTemplate(this);
		try {
			logger.debug("template path=" + template.getAbsolutePath());
			TableReport rep = new TableReport(template.getAbsolutePath(), 0);
			rep.removeSheet(0);
			rep.setSheetName(0, ((String) param.get("tableClassName")));
			rep.setSystemHeader(MessagesUtil.getMessage(this.getPage(), "message.systemname"));
			Map<String, Object> spec = rep.getTableSpec(param, new Dao(this));
			if (spec != null) {
				ret = new BinaryResponse(rep.print(spec), "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", ((String) param.get("tableClassName")) +".xlsx");
			} else {
				ret = new JsonResponse(JsonResponse.INVALID, MessagesUtil.getMessage(this.getPage(), "error.classnotfound"));
			}
		} finally {
			template.delete();
		}
		return ret;
	}

	/**
	 * 既存テーブルのインポートを行います。
	 * @param param パラメータ。
	 * @return テーブル構造情報。
	 * @throws Exception 例外。
	 */
	@WebMethod
	public Response importTable(final Map<String, Object> param) throws Exception {
		String importTable = (String) param.get(ID_IMPORT_TABLE);
		logger.debug("importTable=" + importTable);
		String func = (String) param.get("functionSelect");
		logger.debug("func=" + func);
		JndiDataSource ds = DataFormsServlet.getConf().getOriginalJndiDataSource();
		TableManagerDao dao = new TableManagerDao(ds);
		try (Connection conn = dao.getConnection()) {
			Map<String, Object> m = dao.queryTableInfo(importTable);
			TableInfoEntity e = new TableInfoEntity(m);
			String tblname = importTable + "_table";
			m.put("tableClassName", StringUtil.firstLetterToUpperCase(StringUtil.snakeToCamel(tblname)));
			m.put("tableComment", e.getRemarks());
			m.put("updateInfoFlag", "0");
			List<Map<String, Object>> fieldList = dao.getTableColumnList(func, importTable);
			m.put("fieldList", fieldList);
			logger.debug("json=" + JsonUtil.encode(m, true));
			Response ret = new JsonResponse(JsonResponse.SUCCESS, m);
			return ret;
		}
	}

}
