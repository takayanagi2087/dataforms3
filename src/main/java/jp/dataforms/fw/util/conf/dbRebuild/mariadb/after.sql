-- DBの再構築後に実行するスクリプトを記述します。
-- view, function等の作成に使用します。
-- mariadb用
-- MariaDBにはREGEXP_LIKE関数が無いので作成する。
-- MySQLはREGEXP_LIKE関数をサポートしたらしい。
        CREATE OR REPLACE FUNCTION REGEXP_LIKE(
            source_str LONGTEXT,
            pattern LONGTEXT,
            flags VARCHAR(10)
        )
        RETURNS TINYINT(1)
        DETERMINISTIC
        BEGIN
            DECLARE final_pattern LONGTEXT;
            DECLARE lower_flags VARCHAR(10);
            DECLARE inline_flags VARCHAR(10) DEFAULT '';

            IF source_str IS NULL OR pattern IS NULL THEN
                RETURN NULL;
            END IF;

            SET lower_flags = LOWER(IFNULL(flags, ''));

            IF lower_flags LIKE '%c%' THEN
                SET inline_flags = CONCAT(inline_flags, '-i');
            ELSEIF lower_flags LIKE '%i%' THEN
                SET inline_flags = CONCAT(inline_flags, 'i');
            END IF;

            IF lower_flags LIKE '%m%' THEN
                SET inline_flags = CONCAT(inline_flags, 'm');
            END IF;

            IF inline_flags != '' THEN
                SET final_pattern = CONCAT('(?', inline_flags, ')', pattern);
            ELSE
                SET final_pattern = pattern;
            END IF;

            RETURN source_str REGEXP final_pattern;
        END;
