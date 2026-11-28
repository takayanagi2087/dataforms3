/**
 * @fileOverview {@link SimpleDateFormat}クラスを記述したファイルです。
 */

export class SimpleDateFormat {
	/** フォーマット文字列。 */
	private readonly formatString: string;

	/**
	 * コンストラクタ。
	 * @param f フォーマット文字列。
	 */
	public constructor(f: string) {
		this.formatString = f;
	}

	/**
	 * 日付をフォーマットした文字列を取得します。
	 * @param date 日付。
	 * @returns フォーマットした文字列。
	 */
	public format(date: Date): string {
		let ret = this.formatString;

		ret = ret.replace(/yyyy/g, date.getFullYear().toString());
		ret = ret.replace(/yy/g, (date.getFullYear() % 100).toString());
		ret = ret.replace(/MM/g, ('0' + (date.getMonth() + 1)).slice(-2));
		ret = ret.replace(/dd/g, ('0' + date.getDate()).slice(-2));
		ret = ret.replace(/HH/g, ('0' + date.getHours()).slice(-2));
		ret = ret.replace(/mm/g, ('0' + date.getMinutes()).slice(-2));
		ret = ret.replace(/ss/g, ('0' + date.getSeconds()).slice(-2));

		const matches = ret.match(/S/g);
		if (matches) {
			const milliSeconds = ('00' + date.getMilliseconds()).slice(-3);

			for (let i = 0; i < matches.length; i++) {
				ret = ret.replace(/S/, milliSeconds.substring(i, i + 1));
			}
		}

		return ret;
	}

	/**
	 * 指定したパターンに対応した文字列を取得します。
	 * @param dstr 日付フォーマットの文字列。
	 * @param pat パターン。
	 * @returns 切り出した文字列。
	 */
	private getValue(dstr: string, pat: string): string | null {
		let ret: string | null = null;
		const idx = this.formatString.indexOf(pat);

		if (idx >= 0) {
			ret = dstr.substring(idx, idx + pat.length);
		}

		return ret;
	}

	/**
	 * 年を取得します。
	 * @param dstr 日付文字列。
	 * @returns 年の文字列。
	 */
	private getYear(dstr: string): string {
		let year = this.getValue(dstr, 'yyyy');

		if (year == null) {
			const yy = this.getValue(dstr, 'yy');

			if (yy != null) {
				year = '20' + yy;
			} else {
				year = '1970';
			}
		}

		return year;
	}

	/**
	 * 月を取得します。
	 * @param dstr 日付文字列。
	 * @returns 月の文字列。
	 */
	private getMonth(dstr: string): string {
		return this.getValue(dstr, 'MM') ?? '01';
	}

	/**
	 * 日を取得します。
	 * @param dstr 日付文字列。
	 * @returns 日の文字列。
	 */
	private getDate(dstr: string): string {
		return this.getValue(dstr, 'dd') ?? '01';
	}

	/**
	 * 時を取得します。
	 * @param dstr 日付文字列。
	 * @returns 時の文字列。
	 */
	private getHour(dstr: string): string {
		return this.getValue(dstr, 'HH') ?? '00';
	}

	/**
	 * 分を取得します。
	 * @param dstr 日付文字列。
	 * @returns 分の文字列。
	 */
	private getMin(dstr: string): string {
		return this.getValue(dstr, 'mm') ?? '00';
	}

	/**
	 * 秒を取得します。
	 * @param dstr 日付文字列。
	 * @returns 秒の文字列。
	 */
	private getSec(dstr: string): string {
		return this.getValue(dstr, 'ss') ?? '00';
	}

	/**
	 * ミリ秒を取得します。
	 * @param dstr 日付文字列。
	 * @returns ミリ秒の文字列。
	 */
	private getMSec(dstr: string): string {
		return this.getValue(dstr, 'SSS') ?? '000';
	}

	/**
	 * 文字列をフォーマットにしたがって解析します。
	 * @param dstr 日付文字列。
	 * @returns 日付。
	 */
	public parse(dstr: string): Date | null {
		let ret: Date | null = null;
		const validPattern = '^' + this.formatString.replace(/[yMdHmsS]/g, '[0-9]') + '$';
		const regexp = new RegExp(validPattern);

		if (dstr.match(regexp)) {
			const year = parseInt(this.getYear(dstr), 10);
			const month = parseInt(this.getMonth(dstr), 10);
			const date = parseInt(this.getDate(dstr), 10);
			const hour = parseInt(this.getHour(dstr), 10);
			const min = parseInt(this.getMin(dstr), 10);
			const sec = parseInt(this.getSec(dstr), 10);
			const msec = parseInt(this.getMSec(dstr), 10);

			ret = new Date(year, month - 1, date, hour, min, sec, msec);

			if (
				year === ret.getFullYear()
				&& month === (ret.getMonth() + 1)
				&& date === ret.getDate()
				&& hour === ret.getHours()
				&& min === ret.getMinutes()
				&& sec === ret.getSeconds()
				&& msec === ret.getMilliseconds()
			) {
				// この場合は正常
			} else {
				ret = null;
			}
		}

		return ret;
	}
}