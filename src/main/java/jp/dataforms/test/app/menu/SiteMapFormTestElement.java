package jp.dataforms.test.app.menu;


import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import jp.dataforms.test.component.FormTestElement;
import jp.dataforms.test.component.TestElement;
import jp.dataforms.test.selenium.Browser;

/**
 * サイトマップフォーム要素。 
 */
public class SiteMapFormTestElement extends FormTestElement {
	/**
	 * フォームID。
	 */
	public static final String ID = "siteMapForm";
	
	/**
	 * コンストラクタ。
	 * @param browser ブラウザ。
	 * @param parent 親コンポーネント。
	 * @param webElement FormのWebElement。
	 */
	public SiteMapFormTestElement(final Browser browser, final TestElement parent, final WebElement webElement) {
		super(browser, parent, webElement);
	}
	
	/**
	 * リンク先のリストを取得します。
	 * @return リンク先のリスト。
	 */
	public List<String> getLinkList() {
		List<String> ret = new ArrayList<String>();
		List<WebElement> list = this.findWebElements(By.xpath("//a"));
		for (WebElement e: list) {
			String id = e.getAttribute("data-id");
			if (id.indexOf("menu[") >= 0) {
				ret.add(e.getAttribute("href"));
			}
		}
		return ret;
	}
}
