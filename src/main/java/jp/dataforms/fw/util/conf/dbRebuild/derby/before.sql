-- DBの再構築前に実行するスクリプトを記述します。
-- view,function等の削除に使用します。
-- apache derby用
-- 正規表現マッチング関数
DROP FUNCTION REGEXP_LIKE;
--　正規表現リプレース関数
DROP FUNCTION REGEXP_REPLACE;
-- 日付フィールド抽出関数
DROP FUNCTION EXTRACT_FIELD;
-- 日付加算関数
DROP FUNCTION DATE_ADD_FIELD;

