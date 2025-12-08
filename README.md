# dataforms3.jar Java web application framework.

## Description

dataforms3.jarはまだ開発中です。


dataforms2.jarから以下の点を変更する予定です。

* Tomcat10以上に対応。Tomcat9以下では動作しません。
* IE11, MS EDGE対応スクリプトを削除。(Chromium版EDGEはサポートします)
* 非推奨機能を削除。
* javascriptのmodule対応。
* func_tableを削除し、Javaのコードで対応。(webのpathとjavaのpackageの対応表を追加)
* 設定ファイルの形式をjsoncにする。
* 認証のpasskey対応。

dataforms2.jarと考え方は同じですが、細かい点が異なるため互換性はありません。

特徴を以下にまとめます。

* Javaのクラスライブラリ、Javascriptのクラスライブラリ、開発ツール、ドキュメントが全て1つのjarファイルに入っています。
* 習得するのに必要な知識は、HTML,Java,Javascript,jQueryくらいです。SQLの基本を押さえておけば、Daoクラス関連の機能もすぐに理解できると思います。
* JSPを使用せず、HTMLをそのまま使用します。
* dataforms3.jarが自動生成するJavascriptが、HTML中のイベントハンドラを適切に設定します。そのため、HTMLにはJavascriptやonxxx等のイベントアトリビュートを一切記述しません。
* 開発ツールを装備し、とりあえず動作するJava,Javascript,HTMLを自動生成することができます。
* データベースのテーブルや問い合わせは、JavaのTable,Queryクラスで定義するため、ほとんどSQLの記述は不要です。
* データベースのテーブル作成やテーブル構造の変更は、開発ツールで簡単に行うことができます。
* 複数のベースサーバに対応し、データベースサーバに依存しないアプリケーションの構築が可能です。
(開発環境は組み込みApache Derby、運用はPostgreSQLというシステム開発の実績があります。)
* デフォルトのフレームはレスポンシブデザインになっており、1つのHTMLでPC,タブレット,スマートフォンの画面サイズに対応します。
* フレームデザインは単純なHTML,CSSで記述してあるので、簡単にカスタマイズすることができます。


## Install

* [Pleiades All in One ](https://willbrains.jp/)をダウンロードしインストール。
* EclipseのサーバービューにTomcat10(java21)を追加。
* Webパースペクティブを表示。
* 動的Webプロジェクトを作成。
* 作成したプロジェクトMavenプロジェクトに変換。
* pom.xmlに以下の依存関係を追加。

``` 
	<properties>
		<derby.version>10.17.1.0</derby.version>
	</properties>
	<repositories>
		<!--　リリースバージョンのリポジトリ -->
		<repository>
			<id>jp.dataforms.dataforms3</id>
			<url>https://takayanagi2087.github.io/dataforms3/</url>
		</repository>
		<!--　開発中バージョンのリポジトリ -->
		<repository>
			<id>jp.dataforms</id>
			<url>https://www.dataforms.jp/mvn/repository</url>
		</repository>
	</repositories>
	<dependencies>
		<!-- 利用するJDBCドライバーを追加 -->
		<!--　Apache derby JDBCドライバー　-->
		<!-- https://mvnrepository.com/artifact/org.apache.derby/derby -->
		<dependency>
			<groupId>org.apache.derby</groupId>
			<artifactId>derby</artifactId>
			<version>${derby.version}</version>
		</dependency>
		<!-- https://mvnrepository.com/artifact/org.apache.derby/derbyshared -->
		<dependency>
			<groupId>org.apache.derby</groupId>
			<artifactId>derbyshared</artifactId>
			<version>${derby.version}</version>
		</dependency>
		<!-- https://mvnrepository.com/artifact/org.apache.derby/derbytools -->
		<dependency>
			<groupId>org.apache.derby</groupId>
			<artifactId>derbytools</artifactId>
			<version>${derby.version}</version>
		</dependency>
		<!-- https://mvnrepository.com/artifact/org.apache.derby/derbyLocale_ja_JP -->
		<dependency>
			<groupId>org.apache.derby</groupId>
			<artifactId>derbyLocale_ja_JP</artifactId>
			<version>${derby.version}</version>
		</dependency>
		<!-- datafoms3.jarを追加 -->
		<dependency>
			<groupId>jp.dataforms</groupId>
			<artifactId>dataforms3</artifactId>
			<version>3.1.0-SNAPSHOT</version>
		</dependency>
	</dependencies>
``` 
 
* ビルドした後Tomcat10にWebアプリケーションを追加して実行し、ブラウザからアクセスするとプロジェクト初期化画面が表示されます。

詳しくは[dataforms3.jarドキュメント](https://www.dataforms.jp/sample3/dataforms/doc/page/DocFramePage.html)を参照してください。


## Requirement

主に、Eclipse(pleiades2024-06) + Java21 + Tomcat10 + Apache Derby,PostgreSQLでテストしています。

対応しているデータベースサーバは、以下の通りです。

* Apache Derby
* PostgreSQL
* MariaDB
* Oralce21c xe
* MS SQL Server


## Licence
[MIT](https://github.com/takayanagi2087/dataforms/blob/master/LICENSE)

## Application
以下のサイトはdataforms2.jarで作成されています。

* [ココ散歩](https://cocosampo.net/sampo/)　360度動画を使用した散歩システム

入口はWordPressですが、「登録された散歩コース一覧」等のメニューのリンク先はdataforms2.jarで作成されたアプリケーションになっています。
dataforms2.jarのサンプルは事務処理向けのアプリケーションの印象があるかもしれませんが、このようなWebアプリケーションにも応用されています。



