package jp.dataforms.fw.dao;

import java.util.Map;

/**
 * レコード処理クラス。
 * <pre>
 * 問い合わせ結果のレコードを一件ずつ処理するためのクラスです。
 * </pre>
 */
@FunctionalInterface
public interface RecordProcessor {
	/**
	 * 1レコード処理します。
	 * @param rec レコード。
	 * @return 処理継続フラグ。
	 * @throws Exception 例外。
	 */
	public abstract boolean process(final Map<String, Object> rec) throws Exception;
}

