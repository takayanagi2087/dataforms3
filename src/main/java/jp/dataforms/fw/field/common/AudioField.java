package jp.dataforms.fw.field.common;

import jp.dataforms.fw.dao.file.AudioData;
import jp.dataforms.fw.dao.file.FileObject;

/**
 * 音声フィールドクラス。
 *
 */
public class AudioField extends StreamingField<AudioData> {

	/**
	 * コンストラクタ。
	 *
	 */
	public AudioField() {
		super(null);
	}

	/**
	 * コンストラクタ。
	 *
	 * @param id フィールドID。
	 */
	public AudioField(final String id) {
		super(id);
	}

	@Override
	protected void onBind() {
		super.onBind();
	}

	@Override
	public void init() throws Exception {
		super.init();
		this.setAdditionalHtml(this.getPage().getPageFramePath() + "/AudioField.html");
	}

	@Override
	protected FileObject newFileObject() {
		return new AudioData();
	}



}
