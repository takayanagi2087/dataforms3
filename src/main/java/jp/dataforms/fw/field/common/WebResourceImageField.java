package jp.dataforms.fw.field.common;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jp.dataforms.fw.dao.file.ImageData;
import jp.dataforms.fw.dao.file.WebResource;
import jp.dataforms.fw.dao.file.WebResourceFileStore;
import jp.dataforms.fw.exception.ApplicationError;

/**
 * HTTPでアクセス可能な画像フィールドクラス。
 *
 */
public class WebResourceImageField extends ImageField implements WebResource {
	/**
	 * Log.
	 */
	private Logger logger = LogManager.getLogger(BlobStoreImageField.class);

	/**
	 * URL.
	 */
	private String url = null;

	/**
	 * コンストラクタ。
	 */
	public WebResourceImageField() {
		super(null);
	}

	/**
	 * コンストラクタ。
	 * @param id フィールドID。
	 */
	public WebResourceImageField(final String id) {
		super(id);
	}

	/**
	 * コンストラクタ。
	 * @param id フィールドID。
	 * @param url URL。
	 */
	public WebResourceImageField(final String id, final String url) {
		super(id);
		this.setUrl(url);
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getUrl() {
		return url;
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setUrl(final String url) {
		this.url = url;
	}


	/**
	 * {@inheritDoc}
	 * <pre>
	 * 読み取り専用フィールドなので、URLに対応した画像を作成し、それを返します。
	 * </pre>
	 */
	@Override
	public ImageData getValue() {
		ImageData val = new ImageData();
		try {
			WebResourceFileStore store = (WebResourceFileStore) this.newFileStore();
			val.copy(store.readWebResource(this.url));
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new ApplicationError(e);
		}
		return val;
	}

	@Override
	public Object getValueObject() {
		return super.getValue();
	}
}
