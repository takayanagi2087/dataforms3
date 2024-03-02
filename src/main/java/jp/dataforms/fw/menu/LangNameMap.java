package jp.dataforms.fw.menu;

import java.util.HashMap;

/**
 * 各言語毎の名称マップ。
 */
public class LangNameMap extends HashMap<String, String> {
	/**
	 * デフォルト言語コード。
	 */
	public static final String LANG_DEFAULT = "default";

	/**
	 * コンストラクタ。
	 * @param names 言語毎の名称リスト。
	 * <pre>
	 *  国コードを指定する場合次のように\tで区切って指定	"ja\tname"
	 *  デフォルト言語の場合名称のみを指定					"name"
	 * </pre>
	 */
	public LangNameMap(final String... names) {
		for (int i = 0; i < names.length; i++) {
			String[] sp = names[i].split("\t");
			if (sp.length == 2) {
				this.put(sp[0], sp[1]);
			} else {
				this.put(LANG_DEFAULT, sp[0]);
			}
		}
	}
}

