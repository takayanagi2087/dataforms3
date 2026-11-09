package jp.dataforms.fw.util;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.type.ClassOrInterfaceType;

/**
 * Javaソースユーティリティ。
 */
public class JavaSrcUtil {
	/**
	 * Logger.
	 */
	private static Logger logger = LogManager.getLogger(JavaSrcUtil.class);
	
	/**
	 * コンストラクタ。
	 */
	private JavaSrcUtil() {
	}

	/**
	 * 内部クラス情報。
	 */
	public record InnerClassInfo(String superClassName, String src) {}
	
	
	/**
	 * Javaソースからインナークラス情報を取得します。
	 * @param javasrc Javaヒースファイル。
	 * @return インナークラス情報。
	 * @throws Exception
	 */
	public static Map<String, InnerClassInfo> getInnerClass(final String javasrc) throws Exception {
		var ret = new HashMap<String, InnerClassInfo>();
		File file = new File(javasrc);
		CompilationUnit cu = StaticJavaParser.parse(file);
		List<ClassOrInterfaceDeclaration> classes = cu.findAll(ClassOrInterfaceDeclaration.class);
		for (ClassOrInterfaceDeclaration clazz : classes) {
			// 最上位のクラス（アウターステージ）はスキップし、インナークラスのみを対象にする
			if (clazz.isNestedType()) {
				String superClassName = null;
				NodeList<ClassOrInterfaceType> extendedTypes = clazz.getExtendedTypes();
				if (!extendedTypes.isEmpty()) {
					ClassOrInterfaceType superClass = extendedTypes.get(0);
					superClassName = superClass.getNameAsString();
					logger.debug("superClassName = " + superClassName);
				}
				String src = clazz.toString().replaceAll("(?m)^", "\t");
				src = src.replaceAll("    ", "\t");
				var info = new InnerClassInfo(superClassName, src);
				ret.put(clazz.getNameAsString(), info);
			}
		}
		return ret;
	}
	
	/**
	 * Javaソースを解析してクラス名を取得します。
	 * @param args　コマンドライン引数。
	 */
	public static void main(String[] args) {
		String javasrc = args[0];
		try {
			Map<String, InnerClassInfo> map = JavaSrcUtil.getInnerClass(javasrc);
			for (String key: map.keySet()) {
				System.out.println("Inner class: " + key);
				String src = map.get(key).src();
				System.out.println(src);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}
}
