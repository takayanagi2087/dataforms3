package jp.dataforms.fw.devtool.field;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jp.dataforms.fw.controller.WebComponent;
import jp.dataforms.fw.field.base.Field;
import jp.dataforms.fw.field.common.SelectField;
import jp.dataforms.fw.field.common.SingleSelectField;
import jp.dataforms.fw.menu.FunctionMap.Menu;
import jp.dataforms.fw.menu.FunctionMap.PathPackage;

/**
 * 機能選択フィールドクラス。
 *
 */
public class FunctionSelectField extends SingleSelectField<Long> {

	/**
	 * Logger.
	 */
	private static Logger logger = LogManager.getLogger(FunctionSelectField.class);

	/**
	 * フィールドコメント。
	 */
	private static final String COMMENT = "機能";
	/**
	 * パッケージオプション。
	 */
	private String[] packageOption = {""};

	/**
	 * パッケージ名を展開するフィールドID。
	 */
	private String[] packageFieldId = {"packageName"};

	/**
	 * コンストラクタ。
	 */
	public FunctionSelectField() {
		this(null);
	}

	/**
	 * コンストラクタ。
	 * @param id フィールドID。
	 */
	public FunctionSelectField(final String id) {
		super(id);
		this.setComment(COMMENT);
		this.setBlankOption(true);
	}


	@Override
	public void init() throws Exception {
		super.init();
		String lang = this.getPage().getRequest().getLocale().getLanguage();
		List<Menu> list = WebComponent.getFunctionMap().getMenuList();
		logger.debug("function list.size()=" + list.size());
		List<Map<String, Object>> options = new ArrayList<Map<String, Object>>();
		for (Menu m: list) {
			String value = m.getPath();
			String name = m.getName(lang);
			Map<String, Object> opt = new HashMap<String, Object>();
			opt.put(SelectField.OptionEntity.ID_VALUE, value);
			opt.put(SelectField.OptionEntity.ID_NAME, name);
			options.add(opt);
		}
		this.setOptionList(options);
	}

	@Override
	protected void onBind() {
		super.onBind();
//		this.addValidator(new RequiredValidator());
	}

	/**
	 * パッケージオプションを取得します。
	 * @return パッケージオプション。
	 */
	public String[] getPackageOption() {
		return packageOption;
	}

	/**
	 * パッケージオプションを設定します。
	 * @param packageOption パッケージオプション。
	 * @return 設定したフィールド。
	 */
	public Field<?> setPackageOption(final String... packageOption) {
		this.packageOption = packageOption;
		return this;
	}


	/**
	 * パッケージ名を展開するフィールドIDを取得します。
	 * @return パッケージ名を展開するフィールドID。
	 */
	public String[] getPackageFieldId() {
		return packageFieldId;
	}

	/**
	 * パッケージ名を展開するフィールドを設定します。
	 * @param packageFieldId パッケージ名を展開するフィールド。
	 * @return 設定したフィールド。
	 */
	public FunctionSelectField setPackageFieldId(final String... packageFieldId) {
		this.packageFieldId = packageFieldId;
		return this;
	}

	@Override
	public Map<String, Object> getProperties() throws Exception {
		Map<String, Object> ret = super.getProperties();
		ret.put("packageFieldId", this.getPackageFieldId());
		ret.put("packageOption", this.getPackageOption());
		Map<String, String> pathPackageMap = new HashMap<String, String>();
		for (PathPackage pp: WebComponent.getFunctionMap().getPathPackageList()) {
			pathPackageMap.put(pp.getPath(), pp.getBasePackage());
		}
		ret.put("pathPackageMap", pathPackageMap);
		return ret;
	}
}
