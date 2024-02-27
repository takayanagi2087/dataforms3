package jp.dataforms.fw.devtool.field;

import jp.dataforms.fw.field.common.ExistingFolderField;
import jp.dataforms.fw.validator.RequiredValidator;

/**
 * HTMLやjavascript等のWebリソースを出力するパスフィールドクラス。
 *
 *
 */
public class WebSourcePathField extends ExistingFolderField {
	/**
	 * コンストラクタ。
	 */
	public WebSourcePathField() {
		super(null);
	}

	/**
	 * コンストラクタ。
	 * @param id フィールドID。
	 */
	public WebSourcePathField(final String id) {
		super(id);
	}


	@Override
	protected void onBind() {
		super.onBind();
		this.setReadonly(true);
		this.addValidator(new RequiredValidator());
	}
}
