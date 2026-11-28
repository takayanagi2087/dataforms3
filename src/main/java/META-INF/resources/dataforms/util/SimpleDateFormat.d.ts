/**
 * @fileOverview {@link SimpleDateFormat}クラスを記述したファイルです。
 */
/**
 * @class SimpleDateFormat
 *
 * 日付フォーマットクラス。
 *
 * java.text.SimpleDateFormatのサブセットで、以下の書式をサポートします。
 * yyyy 年(4桁).
 * yy 年(2桁).
 * MM 月
 * dd 日
 * HH 時
 * mm 分
 * ss 秒
 * SSS ミリ秒
 *
 */
export declare class SimpleDateFormat {
    /** フォーマット文字列。 */
    private readonly formatString;
    /**
     * コンストラクタ。
     * @param f フォーマット文字列。
     */
    constructor(f: string);
    /**
     * 日付をフォーマットした文字列を取得します。
     * @param date 日付。
     * @returns フォーマットした文字列。
     */
    format(date: Date): string;
    /**
     * 指定したパターンに対応した文字列を取得します。
     * @param dstr 日付フォーマットの文字列。
     * @param pat パターン。
     * @returns 切り出した文字列。
     */
    private getValue;
    /**
     * 年を取得します。
     * @param dstr 日付文字列。
     * @returns 年の文字列。
     */
    private getYear;
    /**
     * 月を取得します。
     * @param dstr 日付文字列。
     * @returns 月の文字列。
     */
    private getMonth;
    /**
     * 日を取得します。
     * @param dstr 日付文字列。
     * @returns 日の文字列。
     */
    private getDate;
    /**
     * 時を取得します。
     * @param dstr 日付文字列。
     * @returns 時の文字列。
     */
    private getHour;
    /**
     * 分を取得します。
     * @param dstr 日付文字列。
     * @returns 分の文字列。
     */
    private getMin;
    /**
     * 秒を取得します。
     * @param dstr 日付文字列。
     * @returns 秒の文字列。
     */
    private getSec;
    /**
     * ミリ秒を取得します。
     * @param dstr 日付文字列。
     * @returns ミリ秒の文字列。
     */
    private getMSec;
    /**
     * 文字列をフォーマットにしたがって解析します。
     * @param dstr 日付文字列。
     * @returns 日付。
     */
    parse(dstr: string): Date | null;
}
//# sourceMappingURL=SimpleDateFormat.d.ts.map