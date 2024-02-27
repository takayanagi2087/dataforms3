package jp.dataforms.fw.field.common;

import jp.dataforms.fw.validator.ExistingFolderValidator;


/**
 * 存在するフォルダフィールドクラス。
 *
 */
public class ExistingFolderField extends FolderField {

	/**
	 * コンストラクタ。
	 */
	public ExistingFolderField() {
	}

	/**
	 * コンストラクタ。
	 * @param id フィールドID。
	 */
	public ExistingFolderField(final String id) {
		super(id);
	}
	
	@Override
	protected void onBind() {
		super.onBind();
		this.addValidator(new ExistingFolderValidator());
	}
}
