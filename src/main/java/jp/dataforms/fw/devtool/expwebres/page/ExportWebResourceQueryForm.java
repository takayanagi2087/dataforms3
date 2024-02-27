package jp.dataforms.fw.devtool.expwebres.page;

import jp.dataforms.fw.controller.QueryForm;
import jp.dataforms.fw.field.common.FlagField;
import jp.dataforms.fw.field.sqltype.VarcharField;

/**
 * Webリソースの問い合わせフォームクラス。
 */
public class ExportWebResourceQueryForm extends QueryForm {
	/**
	 * コンストラクタ。
	 */
	public ExportWebResourceQueryForm() {
		super();
		this.addField(new VarcharField("fileName", 1024)).setComment("ファイル名");
		this.addField(new FlagField("regexpFlag")).setComment("正規表現を使用する。");
	}
}
