package jp.dataforms.fw.app.user.api;

import java.io.InputStream;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import dev.samstevens.totp.code.HashingAlgorithm;
import dev.samstevens.totp.qr.QrData;
import dev.samstevens.totp.qr.QrGenerator;
import dev.samstevens.totp.qr.ZxingPngQrGenerator;
import jp.dataforms.fw.annotation.WebMethod;
import jp.dataforms.fw.app.user.dao.UserDao;
import jp.dataforms.fw.app.user.dao.UserInfoTable;
import jp.dataforms.fw.controller.WebApi;
import jp.dataforms.fw.response.BinaryResponse;
import jp.dataforms.fw.response.Response;
import jp.dataforms.fw.util.FileUtil;
import jp.dataforms.fw.util.JsonUtil;

/**
 * TOTPのQR CODE取得APIを実装。
 */
public class TotpQrCodeApi extends WebApi {

	/**
	 * Logger.
	 */
	private Logger logger = LogManager.getLogger(TotpQrCodeApi.class);
	
	@Override
	public boolean isAuthenticated(Map<String, Object> params) throws Exception {
		return this.checkUserAttribute("userLevel", "admin")
				|| this.checkUserAttribute("userLevel", "developer")
				|| this.checkUserAttribute("userLevel", "user");
	}

	@WebMethod
	@Override
	public Response exec(Map<String, Object> p) throws Exception {
		String serverName = this.getRequest().getServerName();
		UserInfoTable.Entity ue = new UserInfoTable.Entity(this.getUserInfo());
		String label = ue.getLoginId() + "@" + serverName;
		String context = this.getRequest().getContextPath().substring(1);
		Response resp = null;
		UserDao dao = new UserDao(this);
		String secret = dao.queryTotpSecret(this.getUserId());
		if (secret != null) {
			QrData data = new QrData.Builder()
					   .label(label)
					   .secret(secret)
					   .issuer(context)
					   .algorithm(HashingAlgorithm.SHA1) // More on this below
					   .digits(6)
					   .period(30)
					   .build();
					logger.debug("qrdata=" + JsonUtil.encode(data));	
			QrGenerator generator = new ZxingPngQrGenerator();
			byte[] imageData = generator.generate(data);
			String mimeType = generator.getImageMimeType();
			resp = new BinaryResponse(imageData, mimeType);
		} else {
			byte[] imageData = null;
			try (InputStream is = this.getClass().getResourceAsStream("img/noqrcode.png")) {
				imageData = FileUtil.readInputStream(is);
			}
			resp = new BinaryResponse(imageData, "image/png");
		}
		return resp;
	}

}
