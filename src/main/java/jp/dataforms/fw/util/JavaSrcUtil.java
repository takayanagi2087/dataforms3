package jp.dataforms.fw.util;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;

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
	 * Javaソースからインナークラス情報を取得します。
	 * @param javasrc Javaヒースファイル。
	 * @return インナークラス情報。
	 * @throws Exception
	 */
	public static Map<String, String> getInnerClass(final String javasrc) throws Exception {
		Map<String, String> ret = new HashMap<String, String>();
		File file = new File(javasrc);
		CompilationUnit cu = StaticJavaParser.parse(file);
		List<ClassOrInterfaceDeclaration> classes = cu.findAll(ClassOrInterfaceDeclaration.class);
		for (ClassOrInterfaceDeclaration clazz : classes) {
			// 最上位のクラス（アウターステージ）はスキップし、インナークラスのみを対象にする
			if (clazz.isNestedType()) {
				ret.put(clazz.getNameAsString(), clazz.toString().replaceAll("(?m)^", "\t"));
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
			Map<String, String> map = JavaSrcUtil.getInnerClass(javasrc);
			for (String key: map.keySet()) {
				System.out.println("Inner class: " + key);
				String src = map.get(key);
				System.out.println(src);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}
}
