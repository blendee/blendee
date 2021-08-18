package org.blendee.orm;

import java.math.BigInteger;

import org.blendee.internal.U;
import org.blendee.jdbc.BStatement;
import org.blendee.jdbc.BlendeeManager;
import org.blendee.jdbc.TablePath;
import org.blendee.sql.Bindable;
import org.blendee.sql.Criteria;
import org.blendee.sql.binder.StringBinder;

/**
 * 数値による連続した値を払い出すクラスです。<br>
 * 払い出される値は数値ですが、定義された桁数にあわせて先頭がゼロ埋めされています。
 * @author 千葉 哲嗣
 */
public class NumberSequenceGenerator implements SequenceGenerator {

	private static final BigInteger increment = new BigInteger("1");

	private static final char[] first = { '1' };

	private final TablePath path;

	private final String[] dependsColumnNames;

	private final String targetColumnName;

	private final int length;

	private final char[] myFirst;

	/**
	 * このクラスのインスタンスを生成します。
	 * @param path 対象テーブル
	 * @param dependsColumnNames 上位グループカラム
	 * @param targetColumnName 対象カラム名
	 * @param length サイズ
	 */
	public NumberSequenceGenerator(
		TablePath path,
		String[] dependsColumnNames,
		String targetColumnName,
		int length) {
		this.path = path;
		this.dependsColumnNames = dependsColumnNames.clone();
		this.targetColumnName = targetColumnName;
		this.length = length;
		myFirst = first;
	}

	/**
	 * このクラスのインスタンスを生成します。
	 * @param path 対象テーブル
	 * @param dependsColumnNames 上位グループカラム
	 * @param targetColumnName 対象カラム名
	 * @param length サイズ
	 * @param firstValue 初期値
	 */
	public NumberSequenceGenerator(
		TablePath path,
		String[] dependsColumnNames,
		String targetColumnName,
		int length,
		BigInteger firstValue) {
		this.path = path;
		this.dependsColumnNames = dependsColumnNames.clone();
		this.targetColumnName = targetColumnName;
		this.length = length;
		myFirst = firstValue.toString().toCharArray();
	}

	/**
	 * 対象となるテーブルを返します。
	 * @return 対象テーブル
	 */
	public TablePath getTablePath() {
		return path;
	}

	@Override
	public String[] getDependsColumnNames() {
		return dependsColumnNames.clone();
	}

	@Override
	public String getTargetColumnName() {
		return targetColumnName;
	}

	/**
	 * @throws SequenceOverflowException 連続値の最大を超えた場合
	 */
	@Override
	public Bindable next(Criteria depends) {
		BStatement statement;
		if (depends.isAvailable()) {
			statement = BlendeeManager.getConnection()
				.getStatement(
					"SELECT MAX("
						+ getTargetColumnName()
						+ ") FROM "
						+ path
						+ " WHERE "
						+ depends.toString(false).trim(),
					depends);
		} else {
			statement = BlendeeManager.getConnection()
				.getStatement("SELECT MAX(" + getTargetColumnName() + ") FROM " + path);
		}

		String max;
		try {
			try (var result = statement.executeQuery()) {
				result.next();
				max = result.getString(1);
			}
		} finally {
			statement.close();
		}

		char[] base;
		if (max == null) {
			base = myFirst;
		} else {
			base = new BigInteger(max).add(increment).toString().toCharArray();
		}
		if (base.length > length) throw new SequenceOverflowException(getTargetColumnName());

		var next = new char[length];
		var zeros = length - base.length;
		for (var i = 0; i < zeros; i++) {
			next[i] = '0';
		}
		System.arraycopy(base, 0, next, length - base.length, base.length);
		return new StringBinder(new String(next));
	}

	@Override
	public String toString() {
		return U.toString(this);
	}
}
