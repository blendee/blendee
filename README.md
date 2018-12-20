# Blendee

Blendeeは、Javaのコード内にSQLを混ぜてコーディングすることを目的とするツールです。

## Description

- SQL > ORM

- データベース設計ドリブン
	- Javaのコードからデータベースの構築を行うのではなく、既存のデータベースのメタ情報からSQLを作成するためのクラスを自動生成
	- Eclipseのプラグインで生成することが可能

- タイプセーフSQL
	- 自動生成クラスでは、テーブル、カラム等データベースオブジェクトが全てJavaのシンボルとして扱える

- ラムダ式と高い親和性
	- Java8以降(ラムダ式、Optional)ベースのAPI

## Features

Blendeeの持っている機能は、主に検索（SELECT文の生成、実行）を行うためのものですが、それにはORMモードと集計モードの2つのモードがあります。

ORMモードと集計モードについて

- ORMモード
	検索結果は自動生成されたテーブルクラスのインスタンスとして扱う
	検索するテーブルの全カラムを取得するのではなく、指定したカラムのみ取得することが可能
	開発中の使用状況からSELECTするカラムを記録し、再現することができる

- 集計モード
	検索結果はResultSetとして扱う

- SELECT文

	- SELECT句
		- ORMモード
			- SELECTカラムの自動選択
		- 集計モード
			- AVG, MAX, MIN, COUNT, COALESCE, その他任意の文字列による表現
	- FROM句
		- ORMモード
			- 参照テーブルの自動JOIN
		- 集計モード
			- 各JOIN(INNER JOIN, LEFT OUTER JOIN, RIGHT OUTER JOIN, FULL OUTER JOIN, CROSS JOIN)
			- 副問合せ表とのJOIN
	- WHERE句
		- AND, ORの結合順、強弱により()を付与
		- (NOT) LIKE, (NOT) IN, (NOT) IN (副問い合わせ), (NOT) BETWEEN, IS (NOT) NULL, EXISTS
	- GROUP BY句
		- 集計モードでのみ使用可能(GROUP BY句を使用すると自動的に集計モードに変わる)
	- HAVING句
		- 集計モードでのみ使用可能(HAVING句を使用すると自動的に集計モードに変わる)
		- AVG, MAX, MIN, COUNT, COALESCE, その他任意の文字列による表現
		- (NOT) LIKE, (NOT) IN, (NOT) IN (副問い合わせ), (NOT) BETWEEN, IS (NOT) NULL
	- ORDER BY句
		- ASC, DESC, ASC NULLS FIRST, ASC NULLS LAST, DESC NULLS FIRST, DESC NULLS LAST

- INSERT文
	- ORMエンティティによるINSERT
	- SELECT INSERT

- UPDATE文
	- ORMエンティティによるUPDATE
	- 相関副問合せUPDATE

- DELETE文
	- ORMエンティティによるDELETE
	- 副問合せDELETE

## Requirement

Java8以降

## Usage

## Installation

## Anything Else

## Author

千葉 哲嗣

## License

This project is licensed under the MIT License.
