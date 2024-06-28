package jp.dataforms.test.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * テストターゲット。
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface TestItemInfo {
	/**
	 * テスト項目のグルーブ名を取得します。
	 * @return テスト項目のグルーブ名。
	 */
	String group() default "";
	
	/**
	 * グループ内のテスト順を取得します。
	 * @return グループ内のテスト順。
	 */
	String seq() default "";
	/**
	 * 回帰テスト用項目かどうか。
	 * @return 回帰テスト用項目の場合true。
	 */
	boolean regression() default false;
	
	/**
	 * テストタイプ。
	 */
	public static enum Type {
		/**
		 * 正常。
		 */
		NORMAL,
		/**
		 * エラー。
		 */
		ERROR,
		/**
		 * 境界。
		 */
		BOUNDARY
	}

	/**
	 * テストタイプを所得します。
	 * @return テストタイプ。
	 */
	Type type() default Type.NORMAL;
}
