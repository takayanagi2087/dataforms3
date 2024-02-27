package jp.dataforms.fw.validator;

import java.io.File;

import jp.dataforms.fw.util.StringUtil;

/**
 * フォルダ存在チェックパリデータクラス。
 *
 */
public final class ExistingFolderValidator extends FieldValidator {
	/**
	 * コンストラクタ。
	 */
	public ExistingFolderValidator() {
		super("error.foldernotexist");
	}

	@Override
	public boolean validate(final Object value) throws Exception {
		if (StringUtil.isBlank(value)) {
			return true;
		}
		File file = new File(value.toString());
		if (file.exists()) {
			if (file.isDirectory()) {
				return true;
			}
		}
		return false;
	}

}
