# Blendee

Blendeeは、ORM機能を持った軽量なSQLエディタです。

## Description

ORMモードと集計モードについて

- ORMモード

- 集計モード

## Features

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
			- 副問合せとのJOIN
	- WHERE句
		- AND, ORの結合順、強弱により()を付与
		- (NOT) LIKE, (NOT) IN, (NOT) IN (副問い合わせ), (NOT) BETWEEN, IS (NOT) NULL
	- GROUP BY句
		- 集計モードでのみ使用可能
	- HAVING句
		- 集計モードでのみ使用可能
		- AVG, MAX, MIN, COUNT, COALESCE, その他任意の文字列による表現
		- (NOT) LIKE, (NOT) IN, (NOT) IN (副問い合わせ), (NOT) BETWEEN, IS (NOT) NULL
	- ORDER BY句
		- ASC, DESC, ASC NULLS FIRST, ASC NULLS LAST, DESC NULLS FIRST, DESC NULLS LAST

- INSERT文
	- ORMエンティティによるINSERT
	- SELECT INSER

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

MIT
