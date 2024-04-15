package jp.dataforms.fw.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * アプリケーションの機能マップのクラスを示します。
 * <pre>
 * DataFormsServletはjp.dataforms.fw.menu.FunctionMapから派生したクラスの中で
 * このアノテーションを持つクラスを検索しメニューに表示します。
 * この条件のクラスはアプリケーション中に唯一のクラスとしてください。
 * 複数定義した場合、そのうちの1つを選択し使用します。
 * </pre>
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ApplicationFunctionMap {

}
