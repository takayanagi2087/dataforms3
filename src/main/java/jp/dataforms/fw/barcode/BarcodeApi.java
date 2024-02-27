package jp.dataforms.fw.barcode;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import jp.dataforms.fw.annotation.WebMethod;
import jp.dataforms.fw.controller.WebApi;
import jp.dataforms.fw.response.BinaryResponse;
import jp.dataforms.fw.response.Response;

/**
 * Barcode生成API。
 *
 */
public abstract class BarcodeApi extends WebApi {

	/**
	 * 認証を行います。
	 */
	@Override
	public boolean isAuthenticated(Map<String, Object> params) throws Exception {
		return true;
	}

	/**
	 * バーコードイメージを作成します。
	 * @param contents 出力する文字列。
	 * @param format Barcodeのフォーマット。
	 * @param chaeset キャラクターセット。
	 * @param width 幅。
	 * @param height 高さ。
	 * @return イメージ。
	 * @throws Exception 例外。
	 */
	protected byte[] createBarcodeImage(final String contents, final BarcodeFormat format, final Charset chaeset, final int width, final int height) throws Exception {
		byte[] ret = null;
		QRCodeWriter writer = new QRCodeWriter();
		Map<EncodeHintType, Object> hint = new HashMap<EncodeHintType, Object>();
		hint.put(EncodeHintType.CHARACTER_SET, chaeset);
		BitMatrix bitMatrix = writer.encode(contents, format, width, height, hint);
		BufferedImage image = MatrixToImageWriter.toBufferedImage(bitMatrix);
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		try {
			ImageIO.write(image, "png", os);
		} finally {
			os.close();
		}
		ret = os.toByteArray();
		return ret;
	}

	/**
	 * バーコードイメージを取得します。
	 * <pre>
	 * 指定されたパラメータに応じて、createBarcodeImageメソッドを呼出し、Barcodeイメージを作成するように実装してください。
	 * </pre>
	 * @param p パラメータ。
	 * @return バーコードイメージ。
	 * @throws Exception 例外。
	 */
	protected abstract byte[] getBarcodeImage(final Map<String, Object> p) throws Exception;

	@WebMethod
	@Override
	public Response exec(final Map<String, Object> p) throws Exception {
		return new BinaryResponse(this.getBarcodeImage(p), "image/png");
	}
}
