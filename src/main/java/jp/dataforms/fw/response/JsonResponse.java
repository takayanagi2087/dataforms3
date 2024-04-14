package jp.dataforms.fw.response;

import java.io.PrintWriter;

import jakarta.servlet.http.HttpServletResponse;
import jp.dataforms.fw.servlet.DataFormsServlet;
import jp.dataforms.fw.util.JsonUtil;

/**
 * Jsonの応答情報クラスです。
 *
 */
public class JsonResponse extends Response {
	/**
     * Logger.
     */
   // private static Logger logger = Logger.getLogger(JsonResponse.class.getName());

	/**
	 * 処理が正常終了したことを示します。
	 */
	public static final int SUCCESS = 0;

	/**
	 * バリデーション等のエラーが発生したことを示します。
	 */
	public static final int INVALID = 1;

	/**
	 * アプリケーション例外。
	 */
	public static final int APPLICATION_EXCEPTION = 2;

	/**
	 * 処理の状態。
	 */
	private int status = SUCCESS;

	/**
	 * JSONの送信モード。
	 *
	 */
	private enum Mode {
		/**
		 * 処理のSTATUS情報を送る。
		 */
		WITH_STATUS,
		/**
		 * 結果のみを送る。
		 */
		RESULT_ONLY
	};

	/**
	 * 送信モード。
	 */
	private Mode mode = Mode.WITH_STATUS;

	/**
	 * コンストラクタ。
	 * @param status 処理の状態。
	 * @param result 処理結果。
	 */
	public JsonResponse(final int status, final Object result) {
		this.mode = Mode.WITH_STATUS;
		this.setStatus(status);
		this.setResult(result);
		this.setContentType("application/json; charset=" + DataFormsServlet.getEncoding());
	}

	/**
	 * コンストラクタ。
	 * <pre>
	 * resultの内容をJSONに展開し、そのまま応答します。
	 * </pre>
	 * @param result 処理結果。
	 */
	public JsonResponse(final Object result) {
		this.mode = Mode.RESULT_ONLY;
		this.setResult(result);
		this.setContentType("application/json; charset=" + DataFormsServlet.getEncoding());
	}


	/**
	 * {@inheritDoc}
	 * <pre>
	 * jsonを送信します。
	 * </pre>
	 */
	@Override
	public void send(final HttpServletResponse resp) throws Exception {
		resp.setContentType(this.getContentType());
		Object obj = this;
		if (mode == Mode.RESULT_ONLY) {
			obj = this.getResult();
		}
		PrintWriter out = resp.getWriter();
		try {
			if (obj != null) {
				String json = JsonUtil.encode(obj);
				out.print(json);
			}
		} finally {
			out.flush();
			out.close();
		}
	}

	/**
	 * 処理の状態を取得します。
	 * @return 処理の状態。
	 */
	public final int getStatus() {
		return status;
	}

	/**
	 * 処理の状態を設定します。
	 * @param status 処理の状態。
	 */
	public final void setStatus(final int status) {
		this.status = status;
	}

	@Override
	public String toString() {
		String ret = super.toString() + ": " + JsonUtil.encode(this, DataFormsServlet.isJsonDebug());
		return ret;
	}
}
