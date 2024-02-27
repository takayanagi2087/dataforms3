package jp.dataforms.fw.devtool.field;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jp.dataforms.fw.field.base.Field;
import jp.dataforms.fw.field.common.SelectField;
import jp.dataforms.fw.field.common.VarcharSingleSelectField;
import jp.dataforms.fw.util.ClassFinder;

/**
 * 集計フィールド選択フィールドクラス。
 *
 */
public class DbColumnSelectField extends VarcharSingleSelectField {

	/**
	 * Logger.
	 */
	private static Logger logger = LogManager.getLogger(DbColumnSelectField.class);

	/**
	 * 項目長。
	 */
	private static final int LENGTH = 1024;

	/**
	 * コンストラクタ。
	 */
	public DbColumnSelectField() {
		this(null);
	}

	/**
	 * コンストラクタ。
	 * @param id フィールドID。
	 */
	public DbColumnSelectField(final String id) {
		super(id, LENGTH);
		this.setBlankOption(true);
		this.setRelationDataAcquisition(true);
		this.setRelationDataEvent(RelationDataEvent.CHANGE);
	}

	/**
	 * 選択肢リストを取得ます。
	 * @return 選択肢リスト。
	 * @throws Exception 例外。
	 */
	private List<Map<String, Object>> queryOptionList() throws Exception {
		ClassFinder finder = new ClassFinder();
		List<Class<?>> classList = (List<Class<?>>) finder.findClasses("dataforms.field.sqltype", Field.class);
		List<Map<String, Object>> optlist = new ArrayList<Map<String, Object>>();
		logger.debug(() -> "optlist=" + optlist.size());
		for (Class<?> c: classList) {
			SelectField.OptionEntity e = new SelectField.OptionEntity();
			e.setValue(c.getName());
			e.setName(c.getSimpleName());
			optlist.add(e.getMap());
		}
		return optlist;
	}

	@Override
	public void init() throws Exception {
		super.init();
		this.setOptionList(this.queryOptionList());
	}

	@Override
	protected Map<String, Object> queryRelationData(final Map<String, Object> p) throws Exception {
		logger.debug("queryRelationData");
		String id = (String) p.get(ID_CURRENT_FIELD_ID);
		String fc = (String) p.get(id);
		@SuppressWarnings("unchecked")
		Class<? extends Field<?>> fclass = (Class<? extends Field<?>>) Class.forName(fc);
		Field<?> field = Field.newFieldInstance(fclass);
		p.put("fieldLength", field.getLengthParameter());
		return p;
	}


}
