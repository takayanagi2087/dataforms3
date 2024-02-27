package jp.dataforms.fw.app.errorpage;

import jp.dataforms.fw.controller.Form;
import jp.dataforms.fw.field.sqltype.VarcharField;
import jp.dataforms.fw.util.MessagesUtil;

/**
 * 設定エラーフォームクラス。
 *
 */
public class ConfigErrorForm extends Form {

	/**
	 * エーメッセージキー。
	 */
	private String msgkey = null;

	/**
	 * コンストラクタ。
	 * @param msgkey メッセージキー。
	 */
	public ConfigErrorForm(final String msgkey) {
		super(null);
		this.addField(new VarcharField("message", 1024));
		this.msgkey = msgkey;
	}

	/**
	 * {@inheritDoc}
	 *
	 * 指定されたメッセージキーに対応したメッセージを取得します。
	 */
	@Override
	public void init() throws Exception {
		super.init();
		this.setFormData("message", MessagesUtil.getMessage(this.getPage(), this.msgkey));
	}
}
