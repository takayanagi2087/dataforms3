package jp.dataforms.fw.app.menu.page;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jp.dataforms.fw.annotation.WebMethod;
import jp.dataforms.fw.controller.Form;
import jp.dataforms.fw.controller.Page;
import jp.dataforms.fw.controller.WebComponent;
import jp.dataforms.fw.menu.Menu;
import jp.dataforms.fw.response.JsonResponse;
import jp.dataforms.fw.servlet.DataFormsServlet;
import jp.dataforms.fw.util.JsonUtil;

/**
 * メニューフォームクラス。
 */
public class MenuForm extends Form {
    /**
     * Logger.
     */
    private static Logger logger = LogManager.getLogger(MenuForm.class.getName());

    /**
     * メニュー。
     */
    private Menu menu = null;

	/**
	 * コンストラクタ。
	 */
	public MenuForm() {
		super(null);
		this.menu = this.newMenuComponent();
		this.addComponent(this.menu);
	}


	/**
	 * コンストラクタ。
	 * @param id ID。
	 */
	public MenuForm(final String id) {
		super(id);
		this.menu = this.newMenuComponent();
		this.addComponent(this.menu);
	}

	@Override
	public void init() throws Exception {
		super.init();
		this.menu.setPageList(this.getMenuList());
	}

	/**
	 *　メニューを取得します。
	 * @return メニュー。
	 */
	public Menu getMenu() {
		return menu;
	}

	/**
	 * メニューを設定します。
	 * @param menu メニュー。
	 */
	public void setMenu(final Menu menu) {
		this.menu = menu;
	}


	/**
	 * メニューのインスタンスを作成します。
	 * @return メニュー。
	 */
	protected  Menu newMenuComponent() {
		return new Menu();
	}

	/**
	 * メニューリストを取得します。
	 * <pre>
	 * 機能のリストを取得し、各機能に対応したパッケージ内のPageクラスの一覧を作成し、
	 * ログインユーザが使用可能なページをリストアップします。
	 * </pre>
	 * @return メニュリスト。
	 * @throws Exception 例外。
	 */
	protected List<Map<String, Object>> getMenuList() throws Exception {
		List<Map<String, Object>> list = WebComponent.getFunctionMap().getPageList(this.getPage());
		logger.debug(() -> "menu list=" + JsonUtil.encode(list, true));
		List<Map<String, Object>> mlist = new ArrayList<Map<String, Object>>();
		for (Map<String, Object> m: list) {
			String classname = DataFormsServlet.convertPageClassName((String) m.get("pageClass"));
			@SuppressWarnings("unchecked")
			Class<? extends Page> clazz = (Class<? extends Page>) Class.forName(classname);
			try {
				Page page = clazz.getDeclaredConstructor().newInstance();
				page.setRequest(this.getPage().getRequest());
				if (page.isMenuItem()) {
					if (page.isAuthenticated(new HashMap<String, Object>())) {
						String menuName = (String) m.get("menuName");
						logger.debug(() -> "menuName=" + menuName);
						m.put("menuName", page.decorateMenuName(menuName));
						String menuUrl = page.getMenuUrl();
						if (menuUrl != null) {
							m.put("menuUrl", menuUrl);
						}
						String menuTarget = page.getMenuTarget();
						if (menuTarget != null) {
							m.put("menuTarget", menuTarget);
						}
						mlist.add(m);
					}
				}
			} catch (Error e) {
				logger.error(() -> e.getMessage(), e);
			}
		}
		logger.debug(() -> "menuList=" + JsonUtil.encode(mlist, true));
		return mlist;
	}


	/**
	 * メニューを取得します。
	 * @param param パラメータ。
	 * @return メニュー情報。
	 * @throws Exception 例外。
	 */
	@WebMethod(everyone = true)
	public JsonResponse getMenu(final Map<String, Object> param) throws Exception {
		//List<Map<String, Object>> pageList = this.getMenuList();
		this.menu.setPageList(this.getMenuList());
    	JsonResponse ret = new JsonResponse(JsonResponse.SUCCESS, this.menu.getProperties());
    	return ret;
    }


	@Override
	public Map<String, Object> getProperties() throws Exception {
		Map<String, Object> ret =  super.getProperties();
		Menu menu = this.getMenu();
		ret.put(menu.getId(), menu.getProperties());
		return ret;
	}

}
