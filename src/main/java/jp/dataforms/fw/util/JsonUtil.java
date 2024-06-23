package jp.dataforms.fw.util;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

/**
 * JSON変換ユーティリティ。
 */
public class JsonUtil {
	
	/**
	 * Logger.
	 */
//	private static Logger logger = LogManager.getLogger(JsonUtil.class);
	
	/**
	 * コンストラクタ。
	 */
	private JsonUtil() {
		
	}
	
	/**
	 * オブジェクトをJSONに変換します。
	 * @param object 変換するオブジェクト。
	 * @param prettyPrinting 成形フラグ。
	 * @return JSON。
	 */
	public static String encode(final Object object, final boolean prettyPrinting) {
		Gson gson = null;
		if (prettyPrinting) {
			gson = new GsonBuilder().setPrettyPrinting().create();
		} else {
			gson = new Gson();
		}
		return gson.toJson(object);
	}
	
	/**
	 * オブジェクトをJSONに変換します。
	 * @param object 変換するオブジェクト。
	 * @return JSON。
	 */
	public static String encode(final Object object) {
		return JsonUtil.encode(object, false);
	}

	/**
	 * JSONからオブジェクトに変換します。
	 * @param json JSON文字列。
	 * @param clazz 変換先クラス。
	 * @return オブジェクト。
	 */
	public static Object decode(final String json, Class<?> clazz) {
		Gson gson = new Gson();
		return gson.fromJson(json, clazz);
	}

	/**
	 * 入力ストリームからJsonを読み込みオブジェクトに変換します。
	 * @param is 入力ストリーム。
	 * @param clazz 変換するクラス。
	 * @return 県間結果のオブジェクト。
	 * @throws Exception 例外。
	 */
	public static Object decode(final InputStream is, Class<?> clazz) throws Exception  {
		Gson gson = new Gson();
		try (InputStreamReader r = new InputStreamReader(is)) {
			try (JsonReader jr = new JsonReader(r)) {
				return gson.fromJson(jr, clazz);
			}
		}
	}
	
	/**
	 * JsonWriterを取得します。
	 * @param out 出力先。
	 * @return JsonWriterのインスタンス。
	 * @throws Exception 例外。
	 */
	public static JsonWriter getJsonWriter(final OutputStream out) throws Exception {
		 JsonWriter writer = new JsonWriter(new OutputStreamWriter(out, "UTF-8"));
		 writer.setIndent("\t");
		 return writer;
	}

	/**
	 * JsonReaderを取得します。
	 * @param in 入力ストリーム。
	 * @return JsonReader。
	 * @throws Exception 例外。
	 */
	public static JsonReader getJsonReader(final InputStream in) throws Exception {
		 JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
		 return reader;
	}
	
	
	/**
	 * Jsonのシーケンス例外。
	 */
	public static class JsonSequenceException extends Exception {
		/**
		 * コンストラクタ。
		 */
		public JsonSequenceException() {
			
		}
		
	}
	
	/**
	 * オブジェクトを取得する。
	 * @param r JsonReader。
	 * @return 読み込んだオブジェクト。
	 * @throws Exception 例外
	 */
	public static Map<String, Object> readObject(final JsonReader r) throws Exception {
		Map<String, Object> ret = new HashMap<String, Object>();
		while (true) {
			JsonToken token = r.peek();
			if (token == JsonToken.END_OBJECT) {
				r.endObject();
				break;
			} else {
				if (token == JsonToken.NAME) {
					String name = r.nextName();
					Object value = JsonUtil.readValue(r);
					ret.put(name, value);
				} else {
					throw new JsonSequenceException();
				}
			}
		}
		return ret;
	}
	
	
	/**
	 * 配列を取得します。
	 * @param r JsonReader。
	 * @return 読み込んだ配列。
	 * @throws Exception 例外。
	 */
	public static List<Object> readArray(final JsonReader r) throws Exception {
		List<Object> ret = new ArrayList<Object>();
		while (true) {
			JsonToken token = r.peek();
			if (token == JsonToken.END_ARRAY) {
				r.endArray();
				break;
			} else {
				Object value = JsonUtil.readValue(r);
				ret.add(value);
			}
		}
		return ret;
	}
	
	/**
	 * Jsonを読みこみます。
	 * @param r JsonReader。
	 * @return 読み込んだObject。
	 * @throws Exception 例外。
	 */
	public static Object readValue(final JsonReader r) throws Exception {
		Object ret = null;
		JsonToken token = r.peek();
		if (token == JsonToken.END_DOCUMENT) {
		} else if (token == JsonToken.BEGIN_OBJECT) {
			r.beginObject();
			ret = JsonUtil.readObject(r);
		} else if (token == JsonToken.BEGIN_ARRAY) {
			r.beginArray();
			ret = JsonUtil.readArray(r);
		} else if (token == JsonToken.STRING) {
			ret = r.nextString();
		} else if (token == JsonToken.BOOLEAN) {
			ret  = r.nextBoolean();
		} else if (token == JsonToken.NUMBER) {
			ret = r.nextDouble();
		} else if (token == JsonToken.NULL) {
			r.nextNull();
			ret = null;
		} else {
			throw new JsonSequenceException();
		}
		return ret;
	}
	
	
	
	/**
	 * オブジェクト処理関数インターフェース。
	 */
	@FunctionalInterface
	public interface ProcessObject {
		/**
		 * オブジェクトの処理メソッドです。
		 * @param obj オブジェクト。
		 * @throws Exception 例外。
		 */
		void process(Object obj) throws Exception;
	}


	/**
	 * Json読み込みます。
	 * @param is 入力ストリーム。
	 * @return 変換結果のオブジェクト。
	 * @throws Exception 例外。
	 */
	public static Object readObject(final InputStream is) throws Exception {
		try (JsonReader r = JsonUtil.getJsonReader(is)) {
			Object ret = JsonUtil.readValue(r);
			return ret;
		}
	}
	
	/**
	 * 巨大なJSON配列を処理します。
	 * <pre>
	 * テーブルのバックアップデータ等巨大なJson配列を読み込む場合readObjectで
	 * 一気にメモリに展開するとOutOfMemoryが発生することがあります。
	 * このような場合このreadBigArrayメソッドを使用し、1レコード単位に処理します。
	 * </pre>
	 * @param is 入力ストリーム。
	 * @param po 配列中のオブジェクト。
	 * @throws Exception 例外。
	 */
	public static void readBigArray(final InputStream is, final ProcessObject po) throws Exception {
		try (JsonReader r = JsonUtil.getJsonReader(is)) {
			JsonToken token = r.peek();
			if (token == JsonToken.BEGIN_ARRAY) {
				r.beginArray();
				while (true) {
					token = r.peek();
					if (token == JsonToken.END_ARRAY) {
						r.endArray();
						break;
					} else {
						Object value = JsonUtil.readValue(r);
						po.process(value);
					}
				}
			} else {
				// 配列から開始されない場合例外を投げる。
				throw new JsonSequenceException();
			}
		}
	}
	
	
	/**
	 * テスト用メインメソッド。
	 * @param args コマンドライン引数。
	 */
	public static void main(String[] args) {
		System.out.println("srart");
		try {
			String file = "C:\\eclipse\\workspaceDataforms3\\dataforms3app\\src\\main\\webapp\\WEB-INF\\initialdata\\jp\\dataforms\\fw\\app\\func\\dao\\FuncInfoTable.data.json";
			try (FileInputStream is = new FileInputStream(file)) {
					Object ret = JsonUtil.readObject(is);
					String json = JsonUtil.encode(ret, true);
					System.out.println("json=" + json);
			}

			try (FileInputStream is = new FileInputStream(file)) {
				List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
				JsonUtil.readBigArray(is, (obj) -> {
					@SuppressWarnings("unchecked")
					Map<String, Object> map = (Map<String, Object>) obj;
					list.add(map);
				});
				String json = JsonUtil.encode(list, true);
				System.out.println("json=" + json);
			}
	
		} catch (Exception  e) {
			e.printStackTrace();
		}
		System.out.println("finish");
	}
}
