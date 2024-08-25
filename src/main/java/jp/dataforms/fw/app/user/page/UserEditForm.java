package jp.dataforms.fw.app.user.page;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jp.dataforms.fw.app.user.dao.UserAttributeTable;
import jp.dataforms.fw.app.user.dao.UserDao;
import jp.dataforms.fw.app.user.dao.UserInfoTable;
import jp.dataforms.fw.app.user.field.PasswordField;
import jp.dataforms.fw.controller.EditForm;
import jp.dataforms.fw.htmltable.EditableHtmlTable;
import jp.dataforms.fw.util.ConfUtil.UserEditFormConfig;
import jp.dataforms.fw.util.CryptUtil;
import jp.dataforms.fw.util.CryptUtil.UserPasswordType;
import jp.dataforms.fw.util.UserInfoTableUtil;
import jp.dataforms.fw.validator.DisplayedRequiredValidator;
import jp.dataforms.fw.validator.RequiredValidator;
import jp.dataforms.fw.validator.ValidationError;

/**
 * ユーザ情報編集フォームクラス。
 *
 *
 *
 */
public class UserEditForm extends EditForm {

    /**
     * Logger。
     */
//    private static Logger log = Logger.getLogger(UserEditForm.class.getName());

    /**
     * 管理者フラグ。
     */
    private boolean admin = false;

    /**
     * 設定情報。
     */
    private static UserEditFormConfig config = null;

    /**
     * 設定情報を設定します。
     * @param conf 設定情報。
     */
    public static void setConfig(final UserEditFormConfig conf) {
    	UserEditForm.config = conf;
    }

    /**
     * 設定情報を取得します。
     * @return 設定情報。
     */
    public static UserEditFormConfig getConfig() {
    	return UserEditForm.config;
    }
	/**
	 * コンストラクタ。
	 * <pre>
	 * 監理者モードの場合全ユーザの情報が更新可能です。
	 * </pre>
	 * @param isAdmin 監理者モードの場合true。
	 */
	public UserEditForm(final boolean isAdmin) {
		this.admin = isAdmin;
		UserInfoTable table = UserInfoTableUtil.newUserInfoTable(); // new UserInfoTable();
		table.getLoginIdField().addValidator(new RequiredValidator());
		table.getPasswordField().addValidator(new DisplayedRequiredValidator());
		table.getUserNameField().addValidator(new RequiredValidator());

		Boolean reqEmail = getConfig().getRequiredMailAddress();
		if (reqEmail) {
			table.getMailAddressField().addValidator(new RequiredValidator());
		}
		this.addTableFields(table);
		PasswordField pwck = new PasswordField("passwordCheck");
		pwck.addValidator(new DisplayedRequiredValidator());
		this.insertFieldAfter(pwck, "password");
		// ユーザ属性テーブルの追加。
		UserAttributeTable atbl = new UserAttributeTable();
		atbl.getUserAttributeTypeField().addValidator(new RequiredValidator());
		atbl.getUserAttributeValueField().addValidator(new RequiredValidator());
		EditableHtmlTable at = new EditableHtmlTable("attTable", atbl.getFieldList());
		this.addHtmlTable(at);
		// 初期データ設定.
		this.setFormData("attTable", new ArrayList<Map<String, Object>>());
	}


	/**
	 * {@inheritDoc}
	 * <pre>
	 * 監理者でない場合、ログインユーザの情報を取得します。
	 * </pre>
	 */
	@Override
	public void init() throws Exception {
		super.init();
		this.setFormData("externalUserFlag", "0");
		this.setFormData("enabledFlag", "1");
		if (!this.admin) {
			// 監理者でなければ、自分自身のユーザ情報を取得.
			Map<String, Object> userInfo = this.getPage().getUserInfo();
			UserDao dao = new UserDao(this);
			Map<String, Object> data = dao.getSelectedData(userInfo);
			this.setFormDataMap(data);
		}
	}

	/**
	 * {@inheritDoc}
	 * <pre>
	 * 指定されたユーザの情報を取得します。
	 * </pre>
	 */
	@Override
	protected Map<String, Object> queryData(final Map<String, Object> data) throws Exception {
		UserDao dao = new UserDao(this);
		Map<String, Object> ret = dao.getSelectedData(data);
		return ret;
	}

	/**
	 * {@inheritDoc}
	 * パスワードの一致チェックや属例指定のチェックを行います。
	 */
	@Override
	public List<ValidationError> validate(final Map<String, Object> param) throws Exception {
		List<ValidationError> list = super.validate(param);
		if (list.size() == 0) {
			String password = (String) param.get("password");
			String passwordCheck = (String) param.get("passwordCheck");
			if (!password.equals(passwordCheck)) {
				String msg = this.getPage().getMessage("error.passwordnotmatch");
				ValidationError err = new ValidationError("passwordCheck", msg);
				list.add(err);
			}

			UserDao dao = new UserDao(this);
			Map<String, Object> data = this.convertToServerData(param);
			if (dao.existLoginId(data, this.isUpdate(data))) {
				String msg = this.getPage().getMessage("error.duplicate");
				ValidationError err = new ValidationError(UserInfoTable.Entity.ID_LOGIN_ID, msg);
				list.add(err);
			}
			// 多要素認証関連チェック
			UserInfoTable.Entity e = new UserInfoTable.Entity(data);
			if ("1".equals(e.getMfaRequiredFlag())) {
				Long userId = e.getUserId();
				if (userId == null) {
					// 新規ユーザは多要素認証は必須にできない。
					String msg = this.getPage().getMessage("error.mfanotsetup");
					ValidationError err = new ValidationError(UserInfoTable.Entity.ID_LOGIN_ID, msg);
					list.add(err);
				} else {
					if (!dao.isMfaEnabled(userId)) {
						// 多要素認証が設定されていない場合、多要素認証を必須にできない。
						String msg = this.getPage().getMessage("error.mfanotsetup");
						ValidationError err = new ValidationError(UserInfoTable.Entity.ID_LOGIN_ID, msg);
						list.add(err);
					}
				}
			}
			@SuppressWarnings("unchecked")
			List<Map<String, Object>> attList = (List<Map<String, Object>>) data.get("attTable");
			if (attList.size() != 0) {
				int levelCount = 0;
				for (int i = 0; i < attList.size(); i++) {
					Map<String, Object> m0 = attList.get(i);
					UserAttributeTable.Entity e0 = new UserAttributeTable.Entity(m0);
					String type0 = e0.getUserAttributeType();  // (String) m0.get("userAttributeType");
					if ("userLevel".equals(type0)) {
						levelCount++;
					}
					String value0 = e0.getUserAttributeValue();  //(String) m0.get("userAttributeValue");
					for (int j = i + 1; j < attList.size(); j++) {
						Map<String, Object> m1 = attList.get(j);
						UserAttributeTable.Entity e1 = new UserAttributeTable.Entity(m1);
						String type1 =   e1.getUserAttributeType(); // (String) m1.get("userAttributeType");
						String value1 = e1.getUserAttributeValue();  // (String) m1.get("userAttributeValue");
						if (type1.equals(type0) && value1.equals(value0)) {
							String msg = this.getPage().getMessage("error.duplicateuserattr");
							ValidationError err = new ValidationError("attTable", msg);
							list.add(err);
						}
					}
				}
				if (levelCount == 0) {
					String msg = this.getPage().getPage().getMessage("error.requireduserlevel");
					ValidationError err = new ValidationError("attTable", msg);
					list.add(err);
				} else if (levelCount > 1) {
					String msg = this.getPage().getPage().getMessage("error.duplicateuserlevel");
					ValidationError err = new ValidationError("attTable", msg);
					list.add(err);
				}
			} else {
				String msg = this.getPage().getPage().getMessage("error.requireduserattribute");
				ValidationError err = new ValidationError("attTable", msg);
				list.add(err);
			}
		}
		return list;
	}

	@Override
	protected boolean isUpdate(final Map<String, Object> data) throws Exception {
		Long userId = (Long) data.get("userId");
		return (userId != null);
	}

	@Override
	protected void insertData(final Map<String, Object> data) throws Exception {
		this.setUserInfo(data);
		UserDao dao = new UserDao(this);
		dao.insertUser(data);
	}

	@Override
	protected void updateData(final Map<String, Object> data) throws Exception {
		this.setUserInfo(data);
		UserDao dao = new UserDao(this);
		dao.updateUser(data);
	}

	@Override
	public void deleteData(final Map<String, Object> data) throws Exception {
		UserDao dao = new UserDao(this);
		dao.deleteUser(data);
	}

	@Override
	public Map<String, Object> getProperties() throws Exception {
		Map<String, Object> ret = super.getProperties();
		UserPasswordType type = CryptUtil.getUserPasswordType();
		ret.put("userPasswordType", type);
		return ret;
	}
}
