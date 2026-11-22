package jp.dataforms.fw.util;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.ThisExpr;
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
	 * 呼び出しの引数の一覧を取得します。
	 * @param call　呼び出し。
	 * @return 引数の型一覧。
	 */
	private static List<String> getArgList(MethodCallExpr call) {
		List<String> ret = new ArrayList<>();
		for (Expression arg : call.getArguments()) {
			ret.add(arg.toString());
		}
		return ret;
	}

	/**
	 * 特殊Join引数が含まれていることをチェックします。
	 * @param arglist 引数リスト。
	 * @return　特殊Join引数が含まれている場合true。
	 */
	private static boolean isSpecialJoin(final List<String> arglist) {
		boolean ret = false;
		for (String arg: arglist) {
			if (arg.indexOf("->") >= 0) {
				ret = true;
				break;
			}
		}
		return ret;
	}
	
	/**
	 * 特殊ジョイン情報。
	 */
	public record SpecialJoinInfo(String methodName, List<String> arglist) {}
	
	
	/**
	 * Javaのソースファイルから、コンストラクタが呼び出しているメソッドを取得します。
	 * @param javasrc JavaSrc。
	 * @return 特殊Joinリスト。
	 * @throws Exception 例外。
	 */
	public static List<SpecialJoinInfo> getSpecialJoinMethod(final String javasrc) throws Exception {
		List<SpecialJoinInfo> ret = new ArrayList<>();
		CompilationUnit cu = StaticJavaParser.parse(new File(javasrc));
		cu.findAll(ConstructorDeclaration.class).forEach(constructor -> {
			constructor.findAll(MethodCallExpr.class).forEach(call -> {
				// スコープが存在し、かつ "this" であるか判定
				boolean isThisCall = call.getScope()
						.map(scope -> scope instanceof ThisExpr)
						.orElse(false);
				if (isThisCall) {
					String methodName = call.getNameAsString();
					if (Pattern.matches("add((Inner)|(Left)|(Right))Join", methodName)) {
						List<String> arglist = JavaSrcUtil.getArgList(call);
						if (JavaSrcUtil.isSpecialJoin(arglist)) {
							ret.add(new SpecialJoinInfo(methodName, arglist));
						}
					}
				}
			});
		});
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
