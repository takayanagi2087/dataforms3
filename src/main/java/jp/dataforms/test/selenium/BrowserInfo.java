package jp.dataforms.test.selenium;

import lombok.Data;

/**
 * ブラウザ情報。
 *
 */
@Data
public class BrowserInfo {
	/**
	 * ブラウザ名称。
	 */
	private String name = null;
	/**
	 * Webドライバ。
	 */
	private String driver = null;
	/**
	 * クラス名。
	 */
	private String className = null;
	/**
	 * オプションクラス名。
	 */
	private String optionClassName = null;
	/**
	 * プロパティ名称。
	 */
	private String propName = null;
	/**
	 * コンストラクタ。
	 * @param name ブラウザ名称。
	 * @param driver ドライバのパス。
	 * @param className クラス名。
	 * @param optionClassName オプションクラス名。
	 * @param propName プロパティ名。
	 */
	public BrowserInfo(final String name, final String driver, final String className, final String optionClassName, final String propName) {
		this.name = name;
		this.driver = driver;
		this.className = className;
		this.optionClassName = optionClassName;
		this.propName = propName;
	}
	
	
}

