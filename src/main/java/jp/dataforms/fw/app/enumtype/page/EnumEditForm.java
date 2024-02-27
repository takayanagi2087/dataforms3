package jp.dataforms.fw.app.enumtype.page;

import java.util.List;
import java.util.Map;

import jp.dataforms.fw.app.enumtype.dao.EnumDao;
import jp.dataforms.fw.app.enumtype.dao.EnumTable;
import jp.dataforms.fw.app.enumtype.field.EnumNameField;
import jp.dataforms.fw.controller.DataForms;
import jp.dataforms.fw.controller.EditForm;
import jp.dataforms.fw.dao.Table;
import jp.dataforms.fw.field.base.Field;
import jp.dataforms.fw.field.base.FieldList;
import jp.dataforms.fw.field.common.FileField;
import jp.dataforms.fw.htmltable.EditableHtmlTable;
import jp.dataforms.fw.servlet.DataFormsServlet;
import jp.dataforms.fw.validator.RequiredValidator;

/**
 * 編集フォームクラス。
 */
public class EnumEditForm extends EditForm {
	/**
	 * 選択肢テーブルのID。
	 */
	public static final String ID_OPTION_TABLE = "optionTable";


	/**
	 * コンストラクタ。
	 */
	public EnumEditForm() {
		this(DataForms.ID_EDIT_FORM);
	}

	/**
	 * コンストラクタ。
	 * @param id フォームID。
	 */
	public EnumEditForm(final String id) {
		super(id);
		List<String> langList = DataFormsServlet.getSupportLanguageList();
		EnumTable table = new EnumTable();
		table.getEnumCodeField().addValidator(new RequiredValidator());
		FieldList flist = new FieldList();
		flist.addAll(table.getFieldList());
		flist.addField(new EnumNameField()).addValidator(new RequiredValidator());
		for (String lang: langList) {
			flist.addField(new EnumNameField(lang + "EnumName"));
		}
		this.addFieldList(flist);
		EditableHtmlTable optionTable = new EditableHtmlTable(ID_OPTION_TABLE, flist);
		this.addHtmlTable(optionTable);
	}

	@Override
	public Map<String, Object> getProperties() throws Exception {
		Map<String, Object> prop = super.getProperties();
		List<String> langList = DataFormsServlet.getSupportLanguageList();
		prop.put("langList", langList);
		return prop;
	}

	/**
	 * フォームの初期化を行います。
	 * <pre>
	 * DBを使用した初期化処理はここに記述します。
	 * </pre>
	 */
	@Override
	public void init() throws Exception {
		super.init();
	}

	/**
	 * 編集対象のデータを取得します。
	 * <pre>
	 * 問い合わせ結果フォームに表示されたデータを選択した際に呼び出されます。
	 * dataには最低編集対象レコードのPKのマップが入ってきます。
	 * </pre>
	 * @param data 取得するデータのPKの値が入ってきます。
	 * @return 編集対象データ。
	 */
	@Override
	protected Map<String, Object> queryData(final Map<String, Object> data) throws Exception {
		EnumDao dao = new EnumDao(this);
		return dao.query(data);
	}

	/**
	 * 参照登録対象対象のデータを取得します。
	 * <pre>
	 * queryDataから取得したデータから、PK項目を削除します。
	 * </pre>
	 * @param data 取得するデータのPKの値が入ってきます。
	 * @return 編集対象データ。
	 */
	@Override
	protected Map<String, Object> queryReferData(final Map<String, Object> data) throws Exception {
		Table table = new EnumTable();
		Map<String, Object> ret = this.queryData(data);
		for (Field<?> f: table.getPkFieldList()) {
			ret.remove(f.getId());
		}
		for (Field<?> f: table.getFieldList()) {
			if (f instanceof FileField) {
				ret.remove(f.getId());
			}
		}
		return ret;
	}



	/**
	 * ポストされたデータが更新するのか新規追加するのかを判定します。
	 * <pre>
	 * 編集対象データにPKの入力があった場合、更新すべきと判断します。
	 * </pre>
	 * @param data 入力データ。
	 * @return 更新対象データの場合true。
	 */
	@Override
	protected boolean isUpdate(final Map<String, Object> data) throws Exception {
		Table table = new EnumTable();
		boolean ret = this.isUpdate(table, data);
		return ret;
	}

	/**
	 * データを新規追加します。
	 * @param data ポストされたデータ。
	 */
	@Override
	protected void insertData(final Map<String, Object> data) throws Exception {
		EnumDao dao = new EnumDao(this);
		this.setUserInfo(data); // 更新を行うユーザIDを設定する.
		dao.insert(data);
	}

	/**
	 * データを更新します。
	 * @param data ポストされたデータ。
	 */
	@Override
	protected void updateData(final Map<String, Object> data) throws Exception {
		EnumDao dao = new EnumDao(this);
		this.setUserInfo(data); // 更新を行うユーザIDを設定する.
		dao.update(data);
	}

	/**
	 * データを削除します。
	 * @param data ポストされたデータ。
	 */
	@Override
	public void deleteData(final Map<String, Object> data) throws Exception {
		EnumDao dao = new EnumDao(this);
		this.setUserInfo(data); // 更新を行うユーザIDを設定する.
		dao.delete(data);
	}
}
