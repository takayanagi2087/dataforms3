/**
 * @fileOverview {@link FieldValidator}クラスを記述したファイルです。
 */
import { WebComponent } from '../controller/WebComponent.js';
/**
 * @class FieldValidator
 * フィールドバリデータ基本クラス。
 * @extends WebComponent
 */
export declare class FieldValidator extends WebComponent {
    /**
     * エラーメッセージのキー。
     */
    messageKey: string;
    /**
     * コンストラクタ。
     */
    constructor();
    /**
     * 初期化を行います。
     */
    init(): void;
    /**
     * バリデーションを行ないます。
     * @param v 値
     * @returns バリデーション結果
     */
    validate(v: string | null | undefined): boolean;
    /**
     * エラーメッセージを取得します。
     * @param dspname 表示名
     * @returns エラーメッセージ
     */
    getMessage(dspname: string): string | undefined;
    /**
     * 未入力かどうかをチェックします。
     * @param v 値
     * @returns 未入力の場合true
     */
    isBlank(v: string | null | undefined): boolean;
}
//# sourceMappingURL=FieldValidator.d.ts.map