package jp.dataforms.fw.devtool.entity;

import java.util.List;
import java.util.Map;

import jp.dataforms.fw.controller.WebComponent;
import jp.dataforms.fw.devtool.util.FieldListUtil;
import jp.dataforms.fw.devtool.util.FieldListUtil.GetClassNameFunctionalInterface;
import jp.dataforms.fw.devtool.util.FieldListUtil.GetFieldIdFunctionalInterface;
import jp.dataforms.fw.util.ImportUtil;

/**
 * エンティティ生成クラス。
 */
public class EntityGenerator extends WebComponent {
	/**
	 * Logger.
	 */
//	private Logger logger = LogManager.getLogger(EntityGenerator.class);

	/**
	 * フィールドリスト。
	 */
	private List<Map<String, Object>> fieldList = null;

	/**
	 * コンストラクタ。
	 * @param fieldList フィールドリスト。
	 */
	public EntityGenerator(final List<Map<String, Object>> fieldList) {
		this.fieldList = fieldList;
	}

	/**
	 * Entityを生成します。
	 * @param implist インポートリスト。
	 * @param implistfg インポートリスト(Query生成時に指定する)。
	 * @param func a
	 * @param cfunc a
	 * @return 生成したソース文字列。
	 * @throws Exception 例外。
	 */
	public String generate(final ImportUtil implist, final ImportUtil implistfg, final GetFieldIdFunctionalInterface func, final GetClassNameFunctionalInterface cfunc) throws Exception {
		String javasrc = this.getStringResourse("template/Entity.java.template");
		javasrc = javasrc.replaceAll("\\$\\{idConstants\\}", FieldListUtil.generateFieldIdConstant(this.fieldList, func));
		javasrc = javasrc.replaceAll("\\$\\{valueGetterSetter\\}", FieldListUtil.generateFieldValueGetterSetter(fieldList, func, cfunc, implist));
		javasrc = javasrc.replaceAll("\\$\\{fieldGetter\\}", FieldListUtil.generateFieldGetter(fieldList, func, implistfg));
		return javasrc;
	}
}
