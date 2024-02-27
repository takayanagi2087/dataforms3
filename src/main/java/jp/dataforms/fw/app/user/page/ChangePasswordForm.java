package jp.dataforms.fw.app.user.page;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.dataforms.fw.app.user.dao.UserDao;
import jp.dataforms.fw.app.user.dao.UserInfoTable;
import jp.dataforms.fw.app.user.field.LoginIdField;
import jp.dataforms.fw.app.user.field.PasswordField;
import jp.dataforms.fw.controller.EditForm;
import jp.dataforms.fw.exception.ApplicationException;
import jp.dataforms.fw.util.MessagesUtil;
import jp.dataforms.fw.util.NumberUtil;
import jp.dataforms.fw.validator.RequiredValidator;
import jp.dataforms.fw.validator.ValidationError;

/**
 * パスワード変更フォーム。
 *
 */
public class ChangePasswordForm extends EditForm {
	/**
	 * パスワードリセットモード。
	 */
	private boolean resetMode = false;
	/**
	 * コンストラクタ。
	 * @param reset trueの場合パスワードリセットモード。
	 */
	public ChangePasswordForm(final boolean reset) {
		this.resetMode = reset;
		this.addField(new LoginIdField());
		if (!this.resetMode) {
			this.addField(new PasswordField("oldPassword")).addValidator(new RequiredValidator());
		}
		this.addField(new PasswordField()).addValidator(new RequiredValidator());
		this.addField(new PasswordField("passwordCheck")).addValidator(new RequiredValidator());
	}

	@Override
	public void init() throws Exception {
		super.init();
/*
 		if (this.resetMode) {
			@SuppressWarnings("unchecked")
			Map<String, Object> resetInfo = (Map<String, Object>) this.getPage().getRequest().getSession().getAttribute(PasswordResetPage.PASSWORD_RESET_INFO);
			UserInfoTable.Entity e = new UserInfoTable.Entity(resetInfo);
			this.setFormData(UserInfoTable.Entity.ID_LOGIN_ID, e.getLoginId());
		} else {
			if (this.getPage().getUserInfo() != null) {
				String loginId = (String) this.getPage().getUserInfo().get(UserInfoTable.Entity.ID_LOGIN_ID);
				this.setFormData(UserInfoTable.Entity.ID_LOGIN_ID, loginId);
			}
		}
*/
	}

	@Override
	public List<ValidationError> validate(final Map<String, Object> param) throws Exception {
		List<ValidationError> list = super.validate(param);
		if (list.size() == 0) {
			if (!this.resetMode) {
				try {
						UserDao dao = new UserDao(this);
						String loginId = (String) this.getPage().getUserInfo().get(UserInfoTable.Entity.ID_LOGIN_ID);
						Map<String, Object> p = new HashMap<String, Object>();
						UserInfoTable.Entity e = new UserInfoTable.Entity(p);
						e.setLoginId(loginId);
						e.setPassword((String) param.get("oldPassword"));
						dao.login(p);
				} catch (ApplicationException e) {
					String msg = MessagesUtil.getMessage(this.getPage(), "error.oldpasswordnotmatch");
					list.add(new ValidationError("oldPassword", msg));
				}
			}

			String password = (String) param.get("password");
			String passwordCheck = (String) param.get("passwordCheck");
			if (!password.equals(passwordCheck)) {
				String msg = MessagesUtil.getMessage(this.getPage(), "error.passwordnotmatch");
				list.add(new ValidationError("passwordCheck", msg));
			}
		}
		return list;
	}


	@Override
	protected Map<String, Object> queryData(final Map<String, Object> data) throws Exception {
		UserInfoTable.Entity ret = new UserInfoTable.Entity();
		if (this.resetMode) {
			@SuppressWarnings("unchecked")
			Map<String, Object> resetInfo = (Map<String, Object>) this.getPage().getRequest().getSession().getAttribute(PasswordResetPage.PASSWORD_RESET_INFO);
			UserInfoTable.Entity e = new UserInfoTable.Entity(resetInfo);
			ret.setLoginId(e.getLoginId());
		} else {
			if (this.getPage().getUserInfo() != null) {
				String loginId = (String) this.getPage().getUserInfo().get(UserInfoTable.Entity.ID_LOGIN_ID);
				ret.setLoginId(loginId);
			}
		}
		return ret.getMap();
	}

	@Override
	protected boolean isUpdate(final Map<String, Object> data) throws Exception {
		return true;
	}


	@Override
	protected void updateData(final Map<String, Object> data) throws Exception {
		UserDao dao = new UserDao(this);
		this.setUserInfo(data);
		UserInfoTable.Entity e = new UserInfoTable.Entity(data);
		if (this.resetMode) {
			@SuppressWarnings("unchecked")
			Map<String, Object> resetInfo = (Map<String, Object>) this.getPage().getRequest().getSession().getAttribute(PasswordResetPage.PASSWORD_RESET_INFO);
			e.setUserId(NumberUtil.longValueObject(resetInfo.get("userId")));
		} else {
			e.setUserId(this.getPage().getUserId());
		}
		dao.updatePassword(data);
	}


	@Override
	public void deleteData(final Map<String, Object> data) throws Exception {

	}

	@Override
	protected void insertData(final Map<String, Object> data) throws Exception {

	}


}
