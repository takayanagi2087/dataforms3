/**
 * @fileOverview  {@link createSubclass}メソッドを記述したファイルです。
 */

/**
 *
 * サブクラス作成メソッド。
 * <pre>
 * <code>
 * SubClass = createSubclass("SubClass", {"p1":"v1", "p2":2}, "SuperClass");
 * </code>
 * 上記呼び出しで、以下のコードを生成して実行します。
 * <code>
 * let SubClass = class extends SuperClass {
 *	constructor() {
 *		super();
 *		this.p1 = "v1";
 *		this.p2 = 2;
 *	}
 * }
 * SubClass;
 * </code>
 * </pre>
 *
 * @param {String} subClassName サブクラスの名前を文字列で指定します。
 * @param {Object} properties コンストラクタの中で初期化するプロパティを指定します。
 * @param {String} superClass スーパークラスの名前を文字列で指定します。
 * @returns {Function} 作成したサブクラスを返します。
 */
function createSubclass(subClassName, properties, superClass) {
	var script = "let " + subClassName + " = class extends " + superClass + " {\n";
	script += "\tconstructor() {\n\t\tsuper();\n";
	for (var key in properties) {
		script += "\\tt" + "this." + key + " = " + JSON.stringify(properties[key]) + ";\n";
	}
	script += "\t}\n";
	script += "}\n";
	script += subClassName +";";
	console.log("script\n" + script);
	let ret = eval(script);
	console.dir(ret);
	return ret;
}

