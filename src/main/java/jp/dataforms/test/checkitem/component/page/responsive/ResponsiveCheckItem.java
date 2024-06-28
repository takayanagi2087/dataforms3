package jp.dataforms.test.checkitem.component.page.responsive;

import java.io.File;

import jp.dataforms.fw.controller.Page;
import jp.dataforms.test.checkitem.CheckItem;
import jp.dataforms.test.component.TestElement;
import lombok.Getter;
import lombok.Setter;

/**
 * ページサイズチック項目クラス。
 */
public abstract class ResponsiveCheckItem extends CheckItem {
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
	 * @param condition テスト条件。
	 * @param expected 期待値。
	 */
	public ResponsiveCheckItem(final String condition, final String expected) {
		super(condition, expected);
	}
	
	@Override
	protected String saveAttachFile(Page page, TestElement testElement, ResultType result) throws Exception {
		String imageFile =  this.getResultPath() + "/" + this.getFileName() + ".png";
		String path = testElement.getBrowser().saveScreenShot(imageFile);
		File f = new File(path);
		String ret = "<img src='" + f.getName() + "' width='1024'/>";
		return ret;
	}
}