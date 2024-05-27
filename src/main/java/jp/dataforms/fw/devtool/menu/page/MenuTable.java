package jp.dataforms.fw.devtool.menu.page;

import java.util.List;

import jp.dataforms.fw.devtool.field.PathNameField;
import jp.dataforms.fw.field.base.FieldList;
import jp.dataforms.fw.field.base.TextField;
import jp.dataforms.fw.htmltable.EditableHtmlTable;
import jp.dataforms.fw.servlet.DataFormsServlet;
import jp.dataforms.fw.validator.RegexpValidator;
import jp.dataforms.fw.validator.RequiredValidator;

/**
 * メニュー編集テーブル。
 */
public class MenuTable extends EditableHtmlTable {
	/**
	 * メニューテーブルのID。
	 */
	public static final String ID_MENU_LIST = "menuList";

	/**
	 * パスフィールドのID。
	 */
	public static final String ID_PATH = "path";

	/**
	 * パッケージ名のフィールドID。
	 */
	public static final String ID_PACKAGE_NAME = "packageName";

	/**
	 * デフォルトの言語の名称。
	 */
	public static final String ID_DEFAULT_NAME = "defaultName";

	/**
	 * パスのバリテータ。
	 */
	public class PathValidator extends RegexpValidator {
		/**
		 * コンストラクタ。
		 */
		public PathValidator() {
			super("/[A-Ba-z0-9/]+");
		}
	}
	
	
	/**
	 * コンストラクタ。
	 */
	public MenuTable() {
		super(ID_MENU_LIST);
		FieldList flist = new FieldList();
		flist.addField(new PathNameField(ID_PATH))
			.addValidator(new RequiredValidator())
			.addValidator(new PathValidator());
		flist.addField(new TextField(ID_PACKAGE_NAME)).addValidator(new RequiredValidator());
		flist.addField(new TextField(ID_DEFAULT_NAME)).addValidator(new RequiredValidator());
		List<String> langList = DataFormsServlet.getSupportLanguage();
		for (String lang: langList) {
			flist.addField(new TextField(lang.trim() + "Name"));
		}
		this.setFieldList(flist);
	}
}
