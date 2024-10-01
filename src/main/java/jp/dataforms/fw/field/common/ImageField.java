package jp.dataforms.fw.field.common;

import java.io.File;
import java.util.Map;

import jp.dataforms.fw.annotation.WebMethod;
import jp.dataforms.fw.dao.file.FileObject;
import jp.dataforms.fw.dao.file.FileStore;
import jp.dataforms.fw.dao.file.ImageData;
import jp.dataforms.fw.response.BinaryResponse;
import jp.dataforms.fw.response.ImageResponse;

/**
 * 画像フィールドクラス。
 *
 */
public class ImageField extends FileField<ImageData> {
	
	/**
	 * Logger.
	 */
//	private static Logger logger = LogManager.getLogger(ImageField.class);
	
	/**
	 * サムネイル幅。
	 */
	private int thumbnailWidth = 64;

	/**
	 * サムネイル高さ。
	 */
	private int thumbnailHeight = 64;

	/**
	 * 縮小サムネイル。
	 */
	private Boolean reducedThumbnail = true;

	/**
	 * コンストラクタ。
	 *
	 */
	public ImageField() {
		super(null);
	}

	/**
	 * コンストラクタ。
	 *
	 * @param id フィールドID。
	 */
	public ImageField(final String id) {
		super(id);
	}


	@Override
	protected void onBind() {
		super.onBind();
	}

	/**
	 * 縮小サムネイルフラグを取得します。
	 * @return 縮小サムネイルフラグ。
	 */
	public Boolean getReducedThumbnail() {
		return reducedThumbnail;
	}

	/**
	 * 縮小サムネイルフラグを設定します。
	 * @param reducedThumbnail 縮小サムネイルフラグ。
	 */
	public void setReducedThumbnail(final boolean reducedThumbnail) {
		this.reducedThumbnail = reducedThumbnail;
	}

	@Override
	public void init() throws Exception {
		super.init();
		this.setAdditionalHtml(this.getPage().getPageFramePath() + "/ImageField.html");
	}

	/**
	 * サムネイル幅を取得します。
	 * @return サムネイル幅。
	 */
	public int getThumbnailWidth() {
		return thumbnailWidth;
	}

	/**
	 * サムネイル幅を設定します。
	 * @param thumbnailWidth サムネイル幅。
	 * @return 設定したフィールド。
	 */
	public ImageField setThumbnailWidth(final int thumbnailWidth) {
		this.thumbnailWidth = thumbnailWidth;
		return this;
	}

	/**
	 * サムネイル高さを取得します。
	 * @return サムネイル高さ。
	 */
	public int getThumbnailHeight() {
		return thumbnailHeight;
	}

	/**
	 * サムネイル高さを設定します。
	 * @param thumbnailHeight サムネイル高さ。
	 * @return 設定したフィールド。
	 */
	public ImageField setThumbnailHeight(final int thumbnailHeight) {
		this.thumbnailHeight = thumbnailHeight;
		return this;
	}

	@Override
	protected FileObject newFileObject() {
		return new ImageData();
	}


	/**
	 * 画像データを読み込みます。
	 * @param p 読み込みのパラメータ。
	 * @return 読み込み結果。
	 * @throws Exception 例外。
	 */
	protected ImageData readImageData(final Map<String, Object> p) throws Exception {
		Map<String, Object> param = p;
		String key = (String) p.get("key");
		if (key != null) {
			 param = FileStore.decryptDownloadParameter(key);
			 // logger.debug("readImageData=" + JsonUtil.encode(param, true));
		}
		FileStore store = this.newFileStore(param);
		FileObject fobj = store.readFileObject(param);
		ImageData ret = (ImageData) this.newFileObject();
		ret.copy(fobj);
		if (fobj.getTempFile() != null) {
			ret.readContents(fobj.getTempFile());
		} else {
			ret.setContents(fobj.getContents());
		}
		File temp = store.getTempFile(fobj);
		if (temp != null) {
			temp.delete();
		}
		return ret;
	}

	/**
	 * サムネイル画像をダウンロードします。
	 * @param param パラメータ。
	 * @return 画像応答。
	 * @throws Exception 例外。
	 */
	@WebMethod(useDB = true)
	public ImageResponse downloadThumbnail(final Map<String, Object> param) throws Exception {
//		logger.debug("downloadThumbnail=" + JsonUtil.encode(param, true));
		ImageData image = this.readImageData(param);
		ImageResponse resp = new ImageResponse(image.getReducedImage(thumbnailWidth, thumbnailHeight));
		return resp;
	}

	/**
	 * 完全な画像をダウンロードします。
	 * @param param パラメータ。
	 * @return 画像応答。
	 * @throws Exception 例外。
	 */
	@WebMethod(useDB = true)
	public ImageResponse downloadFullImage(final Map<String, Object> param) throws Exception {
		ImageData image = this.readImageData(param);
		ImageResponse resp = new ImageResponse(image);
		return resp;
	}

	@WebMethod(useDB = true)
	@Override
	public BinaryResponse download(Map<String, Object> p) throws Exception {
		return this.downloadFullImage(p);
	}

	@Override
	public Map<String, Object> getProperties() throws Exception {
		Map<String, Object> ret = super.getProperties();
		ret.put("thumbnailWidth", this.getThumbnailWidth());
		ret.put("thumbnailHeight", this.getThumbnailHeight());
		ret.put("reducedThumbnail", this.getReducedThumbnail());
		return ret;
	}

}
