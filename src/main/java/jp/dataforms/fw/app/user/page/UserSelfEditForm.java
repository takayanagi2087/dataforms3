package jp.dataforms.fw.app.user.page;

import java.util.List;
import java.util.Map;

import jp.dataforms.fw.app.user.dao.UserDao;
import jp.dataforms.fw.app.user.dao.UserInfoTable;
import jp.dataforms.fw.controller.EditForm;
import jp.dataforms.fw.field.base.FieldList;
import jp.dataforms.fw.util.UserInfoTableUtil;
import jp.dataforms.fw.validator.ValidationError;

/**
 * ユーザ情報編集フォームクラス。
 *
 *
 *
 */
public class UserSelfEditForm extends EditForm {

    /**
     * Logger。
     */
//    private static Logger log = Logger.getLogger(UserSelfEditForm.class.getName());

	/**
	 * コンストラクタ。
	 * <pre>
	 * 監理者モードの場合全ユーザの情報が更新可能です。
	 * </pre>
	 */
	public UserSelfEditForm() {
		UserInfoTable tbl = UserInfoTableUtil.newUserInfoTable();
		FieldList list = UserDao.getSelfUpdateFieldList(tbl);
		this.addFieldList(list);
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
		// 自分自身のユーザ情報を取得.
/*		Map<String, Object> userInfo = this.getPage().getUserInfo();
		UserDao dao = new UserDao(this);
		Map<String, Object> data = dao.getSelectedData(userInfo);
		this.setFormDataMap(data);*/
	}


	@Override
	public List<ValidationError> validate(final Map<String, Object> param) throws Exception {
		List<ValidationError> list =  super.validate(param);
		UserDao dao = new UserDao(this);
		Map<String, Object> data = this.convertToServerData(param);
		if (dao.existLoginId(data, this.isUpdate(data))) {
			String msg = this.getPage().getMessage("error.duplicate");
			ValidationError err = new ValidationError(UserInfoTable.Entity.ID_LOGIN_ID, msg);
			list.add(err);
		}
		return list;
	}
	/**
	 * {@inheritDoc}
	 * <pre>
	 * 指定されたユーザの情報を取得します。
	 * </pre>
	 */
	@Override
	protected Map<String, Object> queryData(final Map<String, Object> data) throws Exception {
		Map<String, Object> userInfo = this.getPage().getUserInfo();
		UserDao dao = new UserDao(this);
		Map<String, Object> ret = dao.getSelectedData(userInfo);
		return ret;
	}

	@Override
	protected boolean isUpdate(final Map<String, Object> data) throws Exception {
//		Long userId = (Long) data.get("userId");
/*		UserInfoTable.Entity e = new UserInfoTable.Entity(data);
		Long userId = e.getUserId();
		return (userId != null);*/
		return true;
	}

	@Override
	protected void insertData(final Map<String, Object> data) throws Exception {
		// 呼ばれることはない。
//		this.setUserInfo(data);
//		UserDao dao = new UserDao(this);
//		dao.insertUser(data);
	}

	@Override
	protected void updateData(final Map<String, Object> data) throws Exception {
		this.setUserInfo(data);
		UserInfoTable.Entity e = new UserInfoTable.Entity(data);
		e.setUserId(this.getPage().getUserId());
		UserDao dao = new UserDao(this);
		dao.updateSelfUser(data);
	}

	@Override
	public void deleteData(final Map<String, Object> data) throws Exception {

	}
}
