-- DBの再構築後に実行するスクリプトを記述します。
-- view, function等の作成に使用します。
-- apache derby用
--　正規表現マッチング関数
CREATE FUNCTION REGEXP_LIKE(
    SOURCE VARCHAR(32672), 
    PATTERN VARCHAR(32672), 
    FLAGS VARCHAR(10)
)
RETURNS BOOLEAN
PARAMETER STYLE JAVA
NO SQL
LANGUAGE JAVA
EXTERNAL NAME 'jp.dataforms.fw.dao.sqlgen.derby.DerbyFunctions.regexpLike';


--　正規表現リプレース関数
CREATE FUNCTION REGEXP_REPLACE(
    SOURCE LONG VARCHAR, 
    PATTERN VARCHAR(32672), 
    REPLACEMENT VARCHAR(32672)
)
RETURNS VARCHAR
PARAMETER STYLE JAVA
NO SQL
LANGUAGE JAVA
EXTERNAL NAME 'jp.dataforms.fw.dao.sqlgen.derby.DerbyFunctions.regexpReplace';