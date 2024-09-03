package jp.dataforms.fw.app.user.field;

import jp.dataforms.fw.field.sqltype.IntegerField;


/**
 * MfaLoginCountFieldフィールドクラス。
 *
 */
public class MfaLoginCountField extends IntegerField {

	/**
	 * フィールドコメント。
	 */
	private static final String COMMENT = "多要素認証回数";
	/**
	 * コンストラクタ。
	 */
	public MfaLoginCountField() {
		this(null);
	}
	/**
	 * コンストラクタ。
	 * @param id フィールドID。
	 */
	public MfaLoginCountField(final String id) {
		super(id);
		this.setComment(COMMENT);
		this.setDefaultValue(0);
	}

	@Override
	protected void onBind() {
		super.onBind();

	}

	// 独自のWebメソッドを作成する場合は、以下のコードを参考にしてください。
	/**
	 * Webメソッドのサンプル。
	 * @param p 入力パラメータ。
	 * @return 応答情報。
	 * @throws Exception 例外。
	 */
/*
	@WebMethod
	public Response webMethod(final Map<String, Object> p) throws Exception {
		Object obj = p; // TODO:必要な処理を行い、結果オブジェクトを作成してください。
		Response resp = new JsonResponse(JsonResponse.SUCCESS, obj);
		return resp;
	}
*/

}
