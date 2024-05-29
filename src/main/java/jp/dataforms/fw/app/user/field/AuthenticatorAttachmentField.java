package jp.dataforms.fw.app.user.field;

import jp.dataforms.fw.field.sqltype.VarcharField;
import jp.dataforms.fw.validator.MaxLengthValidator;


/**
 * AuthenticatorAttachmentFieldフィールドクラス。
 *
 */
public class AuthenticatorAttachmentField extends VarcharField {
	/**
	 * フィールド長。
	 */
	private static final int LENGTH = 64;

	/**
	 * フィールドコメント。
	 */
	private static final String COMMENT = "AuthenticatorAttachment";
	/**
	 * コンストラクタ。
	 */
	public AuthenticatorAttachmentField() {
		this(null);
	}
	/**
	 * コンストラクタ。
	 * @param id フィールドID。
	 */
	public AuthenticatorAttachmentField(final String id) {
		super(id, LENGTH);
		this.setComment(COMMENT);
	}

	@Override
	protected void onBind() {
		super.onBind();
		this.addValidator(new MaxLengthValidator(this.getLength()));

	}

////////////////////////////////////////////////////////////////////////////////////
// Autocomplete機能と関連データ取得機能を使用する場合以下のメソッドを実装します。
// 以下のコードのコメントを外して適切に実装してください。
////////////////////////////////////////////////////////////////////////////////////
	/**
	 * 関連フィールドリスト。
	 */
/*
	private static final String[] IDLIST = {
		// このフィールドのIDを指定します
		MasterTable.Entity.ID_SELF_FIELD_ID
		// 確定時にフォームに設定するフィールドIDを複数しています。
		, MasterTable.Entity.ID_RELATION_FIELD01
		, MasterTable.Entity.ID_RELATION_FIELD01
	};
*/
	/**
	 * Autocomplete用の一覧を取得するメソッドを実装します。
	 * @param data パラメータ。
	 * @return 関連データのマップ。
	 * @throws Exception 例外。
	 */
/*
	@Override
	protected List<Map<String, Object>> queryAutocompleteSourceList(final Map<String, Object> data) throws Exception {
		SingleTableQuery query = new SingleTableQuery(new MasterTable()); // 一覧を取得する問合せを作成。
		List<Map<String, Object>> list = this.queryAutocompleteSourceList(
			data					// フォームのデータ
			, query					// 問合せ
			, (Map<String, Object> map, String ... ids) -> {
				return (String) map.get(ids[0]) + ":" + (String) map.get(ids[1]);
			}						// ラベルの構築処理を指定します。
			, IDLIST
		);
		return list;
	}
*/
	/**
	 * 関連データを取得します。
	 * @param data パラメータ。
	 * @return 関連データのマップ。
	 * @throws Exception 例外。
	 */
/*
	@Override
	protected Map<String, Object> queryRelationData(final Map<String, Object> data) throws Exception {
		SingleTableQuery query = new SingleTableQuery(new MasterTable()); // 関連データを取得する問合せを作成。
		Map<String, Object> ret = this.queryRelationData(
			data					// フォームのデータ
			, query					// 問合せ
			, IDLIST
		);
		return ret;
	}
*/

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
