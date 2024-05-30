package jp.dataforms.fw.util;

import java.util.Random;

/**
 * WebAuthn関連ユーティリティクラス。
 */
public final class WebAuthnUtil {

	/**
	 * コンストラクタ。
	 */
	private WebAuthnUtil() {
		
	}
	
	/**
	 * ランダムなchallengeを生成する。
	 * @return ランダムなchallenge。
	 */
	public static String generateChallenge() {
		long seed = (new java.util.Date()).getTime();
		Random r = new Random(seed);
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 16; i++) {
			int v = r.nextInt(10);
			sb.append(Integer.toString(v));
		}
		return sb.toString();
	}

}
