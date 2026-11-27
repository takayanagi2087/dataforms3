# dataforms3.jar Java web application framework.

## Description

dataforms2.jarからの変更点をまとめます。

* Tomcat10以上に対応。Tomcat9以下では動作しません。
* IE11, MS EDGE対応スクリプトを削除。(Chromium版EDGEはサポートします)
* 非推奨機能を削除。
* JavascriptのModule対応。
* func_tableを削除し、Javaのコードで対応。(webのpathとjavaのpackageの対応表を追加)
* 設定ファイルの形式をjsoncに変更し、運用環境用の設定ファイルを外部から提供する機能を追加。。
* 多要素認証(TOTP,Passkey)対応。
* Derbyのバージョンアップが止まったのでH2に対応。
* DBCP2を使用したデータベース接続をサポート。(JNDIデータソースも利用可能)。
* Queryクラスの機能追加。
* FileField階層が複雑で使いにくいのでUploadFieldクラスを追加。(最終的にFileFieldは非推奨にするつもりです)
　　

dataforms2.jarと考え方は同じですが、Javascriptをモジュール化したため互換性はありません。


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
		<!-- Source: https://mvnrepository.com/artifact/com.h2database/h2 -->
		<dependency>
			<groupId>com.h2database</groupId>
			<artifactId>h2</artifactId>
			<version>2.5.252</version>
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

* H2 Database Engine
* Apache Derby
* PostgreSQL
* MariaDB
* Oralce21c xe
* MS SQL Server


## Licence
[MIT](https://github.com/takayanagi2087/dataforms/blob/master/LICENSE)

## Application
以下のサイトはdataforms3.jarで作成されています。

* [ココ散歩](https://sv01.cocosampo.net/walking360/walking/page/ContentsListPage.html)　360度動画を使用した散歩システム


