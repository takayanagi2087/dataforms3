package jp.dataforms.fw.devtool.webres.gen;

/**
 * フレックスフィールドセット生成処理クラス。
 */
public class FlexFieldSetGenerator extends DivFieldSetGenerator {

	/**
	 * コンストラクタ。
	 */
	public FlexFieldSetGenerator() {
		
	}
	
	@Override
	protected String getDivClass() {
		return "flexLayout";
	}
}
