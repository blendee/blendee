package jp.ats.blendee.orm;

import java.math.BigInteger;

import jp.ats.blendee.internal.U;
import jp.ats.blendee.jdbc.BContext;
import jp.ats.blendee.jdbc.BlendeeManager;
import jp.ats.blendee.jdbc.BResultSet;
import jp.ats.blendee.jdbc.BStatement;
import jp.ats.blendee.jdbc.ResourceLocator;
import jp.ats.blendee.sql.Bindable;
import jp.ats.blendee.sql.Condition;
import jp.ats.blendee.sql.binder.StringBinder;

/**
 * 数値による連続した値を払い出すクラスです。
 * <br>
 * 払い出される値は数値ですが、定義された桁数にあわせて先頭がゼロ埋めされています。
 *
 * @author 千葉 哲嗣
 */
public class NumberSequenceGenerator implements SequenceGenerator {

	private static final BigInteger increment = new BigInteger("1");

	private static final char[] first = { '1' };

	private final ResourceLocator locator;

	private final String[] dependsColumnNames;

	private final String targetColumnName;

	private final int length;

	private final char[] myFirst;

	/**
	 * このクラスのインスタンスを生成します。
	 *
	 * @param locator 対象テーブル
	 * @param dependsColumnNames 上位グループカラム
	 * @param targetColumnName 対象カラム名
	 * @param length サイズ
	 */
	public NumberSequenceGenerator(
		ResourceLocator locator,
		String[] dependsColumnNames,
		String targetColumnName,
		int length) {
		this.locator = locator;
		this.dependsColumnNames = dependsColumnNames.clone();
		this.targetColumnName = targetColumnName;
		this.length = length;
		myFirst = first;
	}

	/**
	 * このクラスのインスタンスを生成します。
	 *
	 * @param locator 対象テーブル
	 * @param dependsColumnNames 上位グループカラム
	 * @param targetColumnName 対象カラム名
	 * @param length サイズ
	 * @param firstValue 初期値
	 */
	public NumberSequenceGenerator(
		ResourceLocator locator,
		String[] dependsColumnNames,
		String targetColumnName,
		int length,
		BigInteger firstValue) {
		this.locator = locator;
		this.dependsColumnNames = dependsColumnNames.clone();
		this.targetColumnName = targetColumnName;
		this.length = length;
		myFirst = firstValue.toString().toCharArray();
	}

	/**
	 * 対象となるテーブルを返します。
	 *
	 * @return 対象テーブル
	 */
	public ResourceLocator getResourceLocator() {
		return locator;
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
	public Bindable next(Condition depends) {
		BlendeeManager manager = BContext.get(BlendeeManager.class);

		BStatement statement;
		if (depends.isAvailable()) {
			statement = manager.getConnection().getStatement(
				"SELECT MAX("
					+ getTargetColumnName()
					+ ") FROM "
					+ locator
					+ " WHERE "
					+ depends.toString(false).trim(),
				depends.getComplementer());
		} else {
			statement = manager.getConnection()
				.getStatement("SELECT MAX(" + getTargetColumnName() + ") FROM " + locator);
		}

		String max;
		try {
			try (BResultSet result = statement.executeQuery()) {
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

		char[] next = new char[length];
		int zeros = length - base.length;
		for (int i = 0; i < zeros; i++) {
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
