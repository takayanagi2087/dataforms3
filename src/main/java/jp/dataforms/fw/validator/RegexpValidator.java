package jp.dataforms.fw.validator;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jp.dataforms.fw.util.MessagesUtil;


/**
 * 正規表現パターンバリデータクラス。
 *
 */
public class RegexpValidator extends FieldValidator {

    /**
     * Logger.
     */
//    private static Logger log = Logger.getLogger(RegexpValidator.class.getName());

	/**
	 * 正規表現パターン。
	 */
	private String pattern = null;

	/**
	 * 請求フラグ。
	 */
	private int flags = 0;

	/**
	 * コンストラクタ。
	 * @param pattern 正規表現パターン。
	 * <pre>
	 * クライアントのjavascriptでチェックする場合は"^...$"の形式で記述する。
	 * </pre>
	 */
	public RegexpValidator(final String pattern) {
		super("error.regexp");
		this.pattern = pattern;
	}

	/**
	 * コンストラクタ。
	 * @param msgkey メッセージキー。
	 * @param pattern 正規表現パターン。
	 */
	public RegexpValidator(final String msgkey, final String pattern) {
		super(msgkey);
		this.pattern = pattern;
	}

	/**
	 * コンストラクタ。
	 * @param msgkey メッセージキー。
	 * @param pattern 正規表現パターン。
	 * @param flags パターンフラグ。
	 * <pre>
	 * Pattern.MULTILINE, Pattern.CASE_INSENSITIVE, Pattern.DOTALLに対応しています。
	 * </pre>
	 */
	public RegexpValidator(final String msgkey, final String pattern, final int flags) {
		super(msgkey);
		this.pattern = pattern;
		this.flags = flags;
	}

	/**
	 * 正規表現パターンを取得します。
	 * @return 正規表現パターン。
	 */
	public final String getPattern() {
		return pattern;
	}

	@Override
	public boolean validate(final Object value) throws Exception {
		if (this.isBlank(value)) {
			return true;
		}
		String str = (String) value;
		if (this.flags == 0) {
			return Pattern.matches(this.pattern, str);
		} else {
			Pattern p = Pattern.compile(this.pattern, this.flags);
			Matcher m = p.matcher(str);
			return m.find();
		}
	}

	@Override
	public String getMessage() {
		return MessagesUtil.getMessage(this.getPage(), this.getMessageKey());
	}

	@Override
	public  Map<String, Object> getProperties() throws Exception {
		Map<String, Object> map = super.getProperties();
		map.put("pattern", this.pattern);
		map.put("multiline", (this.flags & Pattern.MULTILINE) != 0);
		map.put("caseInsensitive", (this.flags & Pattern.CASE_INSENSITIVE) != 0);
		map.put("dotAll", (this.flags & Pattern.DOTALL) != 0);
		return map;
	}
}
