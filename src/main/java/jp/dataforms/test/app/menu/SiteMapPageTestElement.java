package jp.dataforms.test.app.menu;

import org.openqa.selenium.WebElement;

import jp.dataforms.test.component.PageTestElement;
import jp.dataforms.test.selenium.Browser;

/**
 * サイトマップページのテスト要素。
 */
public class SiteMapPageTestElement extends PageTestElement {
	/**
	 * コンストラクタ。
	 * @param browser ブラウザ。
	 * @param element WebElement。
	 */
	public SiteMapPageTestElement(final Browser browser, final WebElement element) {
		super(browser, null, element);
	}
	
	/**
	 * サイトマップフォームを取得します。
	 * @return サイトマップフォームのテスト要素。
	 */
	public SiteMapFormTestElement getSiteMapForm() {
		return (SiteMapFormTestElement) this.getForm(SiteMapFormTestElement.ID, SiteMapFormTestElement.class);
	}
}
