-- DBの再構築前に実行するスクリプトを記述します。
-- view,function等の削除に使用します。
-- apache derby用
-- 正規表現マッチング関数
DROP FUNCTION REGEXP_LIKE;
DROP FUNCTION REGEXP_REPLACE;
DROP FUNCTION EXTRACT_FIELD;

