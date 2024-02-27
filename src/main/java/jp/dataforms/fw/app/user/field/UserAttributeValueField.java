package jp.dataforms.fw.app.user.field;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jp.dataforms.fw.annotation.WebMethod;
import jp.dataforms.fw.app.enumtype.dao.EnumDao;
import jp.dataforms.fw.field.common.EnumOptionSingleSelectField;
import jp.dataforms.fw.response.JsonResponse;
import jp.dataforms.fw.util.StringUtil;

/**
 * ユーザ属性値フィールドクラス。
 *
 */
public class UserAttributeValueField extends EnumOptionSingleSelectField {

	/**
	 * フィールドコメント。
	 */
    private static final String COMMENT = "ユーザ属性値";

	/**
	 * コンストラクタ。
	 */
	public UserAttributeValueField() {
//		super(null, null);
//		this.setComment(COMMENT);
		this(null);
	}


	/**
	 * コンストラクタ。
	 * @param id フィールドID。
	 */
	public UserAttributeValueField(final String id) {
		super(id, null);
		this.setComment(COMMENT);
		this.setBlankOption(true);
	}

	/**
	 * typeに対応した選択肢を取得します。
	 * @param param パラメータ。
	 * @return 選択肢リスト。
	 * @throws Exception 例外。
	 */
	@WebMethod
	public JsonResponse getTypeOption(final Map<String, Object> param) throws Exception {
		EnumDao dao = new EnumDao(this);
		String type = (String) param.get("type");
		String lang = this.getPage().getCurrentLanguage();
		List<Map<String, Object>> list = null;
		if (!StringUtil.isBlank(type)) {
			list = dao.getOptionList(type, lang);
		} else {
			list = new ArrayList<Map<String, Object>>();
		}
		this.setOptionList(list);
		JsonResponse result = new JsonResponse(JsonResponse.SUCCESS, this.getOptionList());
		return result;
	}
}
