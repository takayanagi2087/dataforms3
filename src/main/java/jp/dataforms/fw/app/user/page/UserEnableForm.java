package jp.dataforms.fw.app.user.page;

import java.util.Map;

import jp.dataforms.fw.app.user.dao.UserDao;
import jp.dataforms.fw.app.user.dao.UserInfoTable;
import jp.dataforms.fw.controller.EditForm;
import jp.dataforms.fw.exception.ApplicationException;
import jp.dataforms.fw.util.CryptUtil;
import jp.dataforms.fw.util.MessagesUtil;
import jp.dataforms.fw.util.UserInfoTableUtil;

/**
 * ユーザ有効化フォームクラス。
 *
 */
public class UserEnableForm extends EditForm {

	/**
	 * コンストラクタ。
	 */
	public UserEnableForm() {
		UserInfoTable table = UserInfoTableUtil.newUserInfoTable(); //new UserInfoTable();
		this.addField(table.getUserNameField()).removeRequiredValidator();
		this.addField(table.getLoginIdField()).removeRequiredValidator();
		this.addField(table.getPasswordField());
		this.addField(table.getUpdateUserIdField());
		this.addField(table.getUpdateTimestampField());
	}

	@Override
	public void init() throws Exception {
		super.init();
		Map<String, Object> d = this.queryData(null);
		UserInfoTable.Entity e = new UserInfoTable.Entity(d);
		this.setFormData(UserInfoTable.Entity.ID_USER_NAME, e.getUserName());
		this.setFormData(UserInfoTable.Entity.ID_LOGIN_ID, e.getLoginId());
	}

	@Override
	protected Map<String, Object> queryData(final Map<String, Object> data) throws Exception {
		Long loginId = (Long) this.getPage().getRequest().getSession().getAttribute(UserInfoTable.Entity.ID_USER_ID);
		UserDao dao = new UserDao(this);
		Map<String, Object> ret = dao.queryUserInfo(loginId);
		return ret;
	}

	@Override
	protected boolean isUpdate(final Map<String, Object> data) throws Exception {
		return true;
	}

	@Override
	protected void insertData(final Map<String, Object> data) throws Exception {
		//
	}

	@Override
	protected void updateData(final Map<String, Object> data) throws Exception {
		Long userId = (Long) this.getPage().getRequest().getSession().getAttribute(UserInfoTable.Entity.ID_USER_ID);
		UserInfoTable.Entity e = new UserInfoTable.Entity(data);
		e.setUserId(userId);
		String cpass = CryptUtil.encryptUserPassword(e.getPassword());
		UserDao dao = new UserDao(this);
		String dbpass = dao.getUserPassword(userId);
		if (!cpass.equals(dbpass)) {
			throw new ApplicationException(this.getPage(), "error.invaliduserid");
		}
		dao.enableUser(data);
	}

	@Override
	public void deleteData(final Map<String, Object> data) throws Exception {

	}

	@Override
	protected String getSavedMessage(final Map<String, Object> data) {
		String msg = MessagesUtil.getMessage(this.getPage(), "message.userenabled");
		return msg;
	}

}
