/**
 * プルダウンやラジオボタンの選択肢を記録するテーブル群のパッケージです。
 * <pre>
 * EnumTable		列挙型とその選択肢を記録するテーブル。
 * 		親ID(parentId)がnullのレコードは列挙型のコードを示し、
 * 		親ID(parentId)がnot nullのレコードはその親IDが示す列挙型内の選択肢を示します。
 *
 * EnumNameTable	各言語毎の列挙型の名称を記録します。
 *
 * </pre>
 *
 */
package jp.dataforms.fw.app.enumtype.dao;