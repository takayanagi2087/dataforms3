package jp.dataforms.fw.dialog.image;

import jp.dataforms.fw.controller.Form;
import jp.dataforms.fw.field.common.ImageField;
/**
 * 画像表示フォーム。
 * @author takayanagi
 *
 */
public class ImageForm extends Form {
	/**
	 * コンストラクタ。
	 */
	public ImageForm() {
		super(null);
		ImageField f = new ImageField("image");
		f.setThumbnailWidth(960);
		f.setThumbnailHeight(540);
		f.setReducedThumbnail(false);
		this.addField(f);
	}
}
