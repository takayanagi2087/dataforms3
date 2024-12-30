package jp.dataforms.fw.field.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jakarta.mail.Part;
import jp.dataforms.fw.dao.file.FileObject;

/**
 * 複数ファイルアップロードフィールド。
 * <pre>
 * このフィールドの値はForm#convertToServerDataメソッドでは常にnullになります。
 * </pre>
 */
public class MultiFileUploadField extends FileObjectField {
	/**
	 * コンストラクタ。
	 * @param id フィールドID。
	 */
	public MultiFileUploadField(final String id) {
		super(id);
		this.setEnableFileReceiver(true);
	}
	
	@Override
	public void init() throws Exception {
		super.init();
		this.setAdditionalHtml(this.getPage().getPageFramePath() + "/MultiFileUploadField.html");
	}
	
	/**
	 * 複数のファイルはFileObjectに変換できないため、常にnullを返す。
	 * @return 常にnull。
	 */
	@Override
	public FileObject getValue() {
		return null;
	}
	
	/**
	 * アップロードされたファイルリストを取得します。
	 * @param p POSTされたパラメータ。
	 * @return アップロードファイルリスト。
	 */
	@SuppressWarnings("unchecked")
	public List<Part> getPartList(final Map<String, Object> p) {
		List<Part> ret = new ArrayList<Part>();
		Object obj = p.get(this.getId());
		if (obj instanceof Part) {
			ret.add((Part) obj);
		} else if (obj instanceof List) {
			ret = (List<Part>) obj;
		}
		return ret;
	}
}
