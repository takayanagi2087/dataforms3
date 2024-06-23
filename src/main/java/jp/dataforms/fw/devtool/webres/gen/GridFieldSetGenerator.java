package jp.dataforms.fw.devtool.webres.gen;

/**
 * グリッドフィールドセット生成処理クラス。
 */
public class GridFieldSetGenerator extends DivFieldSetGenerator {

	/**
	 * コンストラクタ。
	 */
	public GridFieldSetGenerator() {
		
	}
	
	@Override
	protected String getDivClass() {
		return "gridLayout";
	}
}
