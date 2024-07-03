package jp.dataforms.test.testitem.page.responsive;

import java.io.File;

import jp.dataforms.fw.controller.Page;
import jp.dataforms.fw.controller.WebComponent;
import jp.dataforms.test.selenium.Browser;
import jp.dataforms.test.testitem.TestItem;
import lombok.Getter;
import lombok.Setter;

/**
 * ページサイズチック項目クラス。
 */
public abstract class ResponsiveTestItem extends TestItem {
	/**
	 * グループ名。
	 */
	public static final String GROUP = "responsive";
	
	/**
	 * PCレイアウトの最小幅。
	 */
	public static final int PC_MIN_WIDTH = 1025;
	/**
	 * タブレットの最大幅。
	 */
	public static final int TAB_MAX_WIDTH = 1024;
	
	/**
	 * タブレットの最小幅。
	 */
	public static final int TAB_MIN_WIDTH = 769;
	/**
	 * スマートフォンの最大幅。
	 */
	public static final int SP_WIDTH = 700;

	/**
	 * ページの高さ。
	 */
	@Getter
	@Setter
	private static int height = 800;
	
	/**
	 * コンストラクタ。
	 * @param pageClass ページクラス。
	 * @param compClass ページクラス。
	 * @param condition テスト条件。
	 * @param expected 期待値。
	 */
	public ResponsiveTestItem(final Class<? extends Page> pageClass, final Class<? extends WebComponent> compClass, final String condition, final String expected) {
		super(pageClass, compClass, condition, expected);
	}
	
	@Override
	protected String saveAttachFile(final Page page, final Browser browser, final ResultType result) throws Exception {
		String imageFile =  this.getTestItemPath() + "/" + this.getFileName() + ".png";
		String path = browser.saveScreenShot(imageFile);
		File f = new File(path);
		String ret = "<img src='" + f.getName() + "' width='1024'/>";
		return ret;
	}
}
