package jp.dataforms.fw.app.user.page;

import java.util.Map;

import jp.dataforms.fw.app.base.page.AdminPage;
import jp.dataforms.fw.dao.Dao;
import jp.dataforms.fw.util.CryptUtil;


/**
 * ページクラス。
 */
public class PasswordReencryptPage extends AdminPage {
	/**
	 * コンストラクタ。
	 */
	public PasswordReencryptPage() {
		this.addForm(new PasswordRecenryptForm());
		if (CryptUtil.getUserPasswordType() == CryptUtil.UserPasswordType.IRREVERSIBLE_PASSWORD) {
			this.setMenuItem(false);
		}
	}

	@Override
	public boolean isAuthenticated(Map<String, Object> params) throws Exception {
		boolean ret = super.isAuthenticated(params);
		if (CryptUtil.getUserPasswordType() == CryptUtil.UserPasswordType.IRREVERSIBLE_PASSWORD) {
			ret = false;
		}
		return ret;
	}


	/**
	 * Pageが配置されるパスを返します。
	 *
	 * @return Pageが配置されるパス。
	 */
	public String getFunctionPath() {
		return "/dataforms/app";
	}

	/**
	 * テーブルを操作するためのDaoクラスを取得します。
	 * <pre>
	 * ページjavaクラス作成用のメソッドです。
	 * </pre>
	 * @return Daoクラス。
	 */
	public Class<? extends Dao> getDaoClass() {
		return Dao.class;
	}

}
