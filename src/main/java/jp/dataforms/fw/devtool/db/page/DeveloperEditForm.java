package jp.dataforms.fw.devtool.db.page;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jp.dataforms.fw.app.user.dao.UserDao;
import jp.dataforms.fw.app.user.dao.UserInfoTable;
import jp.dataforms.fw.app.user.field.PasswordField;
import jp.dataforms.fw.controller.EditForm;
import jp.dataforms.fw.controller.Page;
import jp.dataforms.fw.controller.WebComponent;
import jp.dataforms.fw.devtool.db.dao.TableManagerDao;
import jp.dataforms.fw.field.common.FlagField;
import jp.dataforms.fw.util.MessagesUtil;
import jp.dataforms.fw.util.StringUtil;
import jp.dataforms.fw.util.UserInfoTableUtil;
import jp.dataforms.fw.validator.RequiredValidator;
import jp.dataforms.fw.validator.ValidationError;

/**
 * 開発者登録フォームクラス。
 * <pre>
 * 動作するため必要な最小限のテーブルを作成し開発者ユーザを登録するためのフォームです。
 * </pre>
 */
public class DeveloperEditForm extends EditForm {

    /**
     * Logger.
     */
    private static Logger logger = LogManager.getLogger(DeveloperEditForm.class.getName());

    /**
     * ユーザインポートフラグの初期値。
     */
    private static Boolean checkUserImport = false;

    /**
	 * ユーザインポートフラグを取得します。
     * @return ユーザインポートフラグ。
     */
	public static Boolean getCheckUserImport() {
		return checkUserImport;
	}

	/**
	 * ユーザインポートフラグを設定します。
	 * @param checkUserImport ユーザインポートフラグ。
	 */
	public static void setCheckUserImport(final Boolean checkUserImport) {
		DeveloperEditForm.checkUserImport = checkUserImport;
	}

	/**
	 * コンストラクタ。
	 */
	public DeveloperEditForm() {
		this.addField(new FlagField("userImportFlag"));
		UserInfoTable table = UserInfoTableUtil.newUserInfoTable(); // new UserInfoTable();
		table.getLoginIdField().addValidator(new RequiredValidator());
		table.getPasswordField().addValidator(new RequiredValidator());
		this.addTableFields(table);
		table.getLoginIdField().setAutocomplete(false).setRelationDataAcquisition(false);
		table.getUserNameField().setAutocomplete(false).setRelationDataAcquisition(false).addValidator(new RequiredValidator());
		this.insertFieldAfter(new PasswordField("passwordCheck"), "password").addValidator(new RequiredValidator());
	}

	/**
	 * 初期化時に作成するユーザのレベルを取得します。
	 * @return 初期化時に作成するユーザのレベル。
	 */
	private String getInitializeUserLvel() {
		String userLevel = getServlet().getServletContext().getInitParameter("initialize-user-level");
		if (userLevel == null) {
			userLevel = "admin";
		}
		return userLevel;

	}

	/**
	 * {@inheritDoc}
	 * 各フィールドのデフォルト値を設定します。
	 *
	 */
	@Override
	public void init() throws Exception {
		super.init();
		String userLevel = this.getInitializeUserLvel();
		this.setFormData("loginId", userLevel);
		this.setFormData("userName", userLevel);
	}

	@Override
	public Map<String, Object> getProperties() throws Exception {
		Map<String, Object> ret = super.getProperties();
		boolean exists = this.userInfoDataExists();
		logger.debug(() -> "userInfoDataExists=" + exists);
		ret.put("userInfoDataExists", exists);
		return ret;
	}


	/**
	 * {@inheritDoc}
	 * <pre>
	 * 実装が必要なので適当に作っておきます。
	 * </pre>
	 */
	@Override
	protected Map<String, Object> queryData(final Map<String, Object> data) throws Exception {
		Map<String, Object> ret = new HashMap<String, Object>();
		String userLevel = this.getInitializeUserLvel();
		ret.put("loginId", userLevel);
		ret.put("userName", userLevel);
		logger.debug(() -> "checkUserImport=" + DeveloperEditForm.checkUserImport);
		boolean exists = this.userInfoDataExists();
		if (exists && DeveloperEditForm.checkUserImport) {
			ret.put("userImportFlag", "1");
		} else {
			ret.put("userImportFlag", "0");
		}
		return ret;
	}

	@Override
	public List<ValidationError> validate(final Map<String, Object> param) throws Exception {
		String userImportFlag = (String) param.get("userImportFlag");
		if ("1".equals(userImportFlag)) {
			return new ArrayList<ValidationError>();
		}
		List<ValidationError> list = super.validate(param);
		if (list.size() == 0) {
			String password = (String) param.get("password");
			String passwordCheck = (String) param.get("passwordCheck");
			if (!password.equals(passwordCheck)) {
				String msg = this.getPage().getPage().getMessage("error.passwordnotmatch");
				ValidationError err = new ValidationError("passwordCheck", msg);
				list.add(err);
			}
			TableManagerDao tmdao = new TableManagerDao(this);
			if (tmdao.isDatabaseInitialized()) {
				UserDao dao = new UserDao(this);
				Map<String, Object> data = this.convertToServerData(param);
				if (dao.existLoginId(data, this.isUpdate(data))) {
					String msg = this.getPage().getPage().getMessage("error.duplicate");
					ValidationError err = new ValidationError("userId", msg);
					list.add(err);
				}
			}
		}
		return list;
	}

	@Override
	protected boolean isUpdate(final Map<String, Object> data) throws Exception {
		Long userId = (Long) data.get("userId");
		return (!StringUtil.isBlank(userId));
	}


	/**
	 * 初期化するパッケージリストを取得します。
	 * @return 初期化するパッケージリスト。
	 */
	private List<String> getInitializePackageList() {
		List<String> ret = new ArrayList<String>();
		String plist = Page.getServlet().getServletContext().getInitParameter("initialize-package-list");
		String[] a = plist.split(",");
		for (String pkg: a) {
			ret.add(pkg.trim());
		}
		return ret;
	}

	/**
	 * ユーザ情報の初期化データが存在するかチェックします。
	 * @return 存在する場合true。
	 * @throws Exception 例外。
	 */
	private boolean userInfoDataExists() throws Exception {
		UserInfoTable table = UserInfoTableUtil.newUserInfoTable(); // new UserInfoTable();
		String path = Page.getServlet().getServletContext().getRealPath("/WEB-INF/initialdata");
		String userInitialFile = table.getImportData(path);
		logger.debug(() -> "userInitialFile=" + userInitialFile);
		if (userInitialFile != null) {
			return true;
		} else {
			return false;
		}
	}


	/**
	 * データベースの初期化を行います。
	 * <pre>
	 * jp.dataforms.app以下のTableクラスに対応したテーブルをDBに作成し
	 * 初期データを登録します。
	 * </pre>
	 * @param userImportFlag ユーザ情報インポートフラグ。
	 * @throws Exception 例外。
	 *
	 *
	 */
	private void initDb(final String userImportFlag) throws Exception {
		TableManagerDao tmdao = new TableManagerDao(this);
		tmdao.executeBeforeRebuildSql();
		List<String> plist = this.getInitializePackageList();
		for (String pkg: plist) {
			Map<String, Object> p = new HashMap<String, Object>();
			p.put("packageName", pkg);
			List<Map<String, Object>> list  = tmdao.queryTableClass(p);
			for (Map<String, Object> m: list) {
				String className = (String) m.get("className");
				if (className.indexOf(WebComponent.BASE_PACKAGE + ".app.dao.user") == 0) {
					if ("1".equals(userImportFlag)) {
						// ユーザ関連テーブルを作成しデータをインポートする。
						tmdao.updateTable(className);
					} else {
						// 入力されたユーザを登録するので、テーブルを作成するのみ。
						tmdao.createTable(className);
						//Table tbl = Table.newInstance(className);
						//tmdao.createIndex(tbl);
					}
				} else {
					tmdao.updateTable(className);
				}
			}
		}
		tmdao.executeAfterRebuildSql();
	}

	/**
	 * {@inheritDoc}
	 * <pre>
	 * 開発者ユーザを登録します。
	 * </pre>
	 */
	@Override
	protected void insertData(final Map<String, Object> data) throws Exception {
		String userImportFlag = (String) data.get("userImportFlag");
		this.initDb(userImportFlag);
		if (!"1".equals(userImportFlag)) {
			long userid = this.getPage().getUserId();
			List<Map<String, Object>> attTable = new ArrayList<Map<String, Object>>();
			Map<String, Object> att = new HashMap<String, Object>();
			att.put("userAttributeType", "userLevel");
			att.put("userAttributeValue", this.getInitializeUserLvel());
			att.put("createUserId", userid);
			att.put("updateUserId", userid);
			att.put("deleteFlag", "0");
			attTable.add(att);
			data.put("attTable", attTable);
			UserInfoTable.Entity e = new UserInfoTable.Entity(data);
			e.setEnabledFlag("1");
			this.setUserInfo(data);
			UserDao dao = new UserDao(this);
			dao.insertUser(data);
		}
		TableManagerDao tmdao = new TableManagerDao(this);
		tmdao.createAllForeignKeys(); // 全外部キー作成。
	}

	/**
	 * {@inheritDoc}
	 * <pre>
	 * 実装が必要なので適当に作っておきます。呼ばれることはありません。
	 * </pre>
	 */
	@Override
	protected void updateData(final Map<String, Object> data) throws Exception {
		this.setUserInfo(data);
		UserDao dao = new UserDao(this);
		dao.updateUser(data);
	}

	/**
	 * {@inheritDoc}
	 * <pre>
	 * 実装が必要なので適当に作っておきます。呼ばれることはありません。
	 * </pre>
	 */
	@Override
	public void deleteData(final Map<String, Object> data) throws Exception {

	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getSavedMessage(final Map<String, Object> data) {
		return MessagesUtil.getMessage(this.getPage(), "message.databasecreated");
	}
}
