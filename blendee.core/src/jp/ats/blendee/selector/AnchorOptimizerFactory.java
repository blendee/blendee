package jp.ats.blendee.selector;

import jp.ats.blendee.internal.U;
import jp.ats.blendee.jdbc.ManagementSubject;
import jp.ats.blendee.jdbc.ResourceLocator;

/**
 * {@link AnchorOptimizer} を生成するためのファクトリクラスです。
 *
 * @author 千葉 哲嗣
 */
public class AnchorOptimizerFactory implements ManagementSubject {

	private boolean canAddNewEntries = false;

	private Class<? extends ColumnRepositoryFactory> columnRepositoryFactoryClass;

	/**
	 * {@link ColumnRepository} に対する新しく使用された要素の追加を行うかどうかを設定します。
	 * <br>
	 * flag が true の場合、未使用の要素が使用されても {@link ColumnRepository} に新しい要素の追加は行われず、 {@link IllegalValueException} がスローされるようになります。
	 *
	 * @param adds 新しく使用された要素の追加を行うかどうか
	 */
	public synchronized void setCanAddNewEntries(boolean adds) {
		canAddNewEntries = adds;
	}

	/**
	 * {@link ColumnRepository} を生成するファクトリクラスを設定します。
	 *
	 * @param columnRepositoryFactoryClass ファクトリクラス
	 */
	public synchronized void setColumnRepositoryFactoryClass(
		Class<? extends ColumnRepositoryFactory> columnRepositoryFactoryClass) {
		this.columnRepositoryFactoryClass = columnRepositoryFactoryClass;
	}

	/**
	 * {@link ColumnRepository} に対する新しく使用されたカラムの追加を行うかどうかを返します。
	 *
	 * @return 新しく使用された要素の追加を行うかどうか
	 */
	public synchronized boolean canAddNewEntries() {
		return canAddNewEntries;
	}

	@Override
	public String toString() {
		return U.toString(this);
	}

	synchronized ColumnRepository createColumnRepository() {
		try {
			return columnRepositoryFactoryClass.newInstance().createColumnRepository();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * ID を直接指定してこのクラスのインスタンスを生成します。
	 *
	 * @param id パラメータで渡されたクラスに存在するこのインスタンスを格納したフィールド名
	 * @return このクラスのインスタンス
	 */
	public AnchorOptimizer getInstance(String id) {
		return new AnchorOptimizer(
			this,
			id,
			null,
			getUsingClass(new Throwable().getStackTrace()[0]),
			canAddNewEntries());
	}

	/**
	 * ID を直接指定してこのクラスのインスタンスを生成します。
	 *
	 * @param id パラメータで渡されたクラスに存在するこのインスタンスを格納したフィールド名
	 * @param hint リポジトリにまだ登録されていない場合使用されるテーブル
	 * @return このクラスのインスタンス
	 */
	public AnchorOptimizer getInstance(String id, ResourceLocator hint) {
		return new AnchorOptimizer(
			this,
			id,
			hint,
			getUsingClass(new Throwable().getStackTrace()[0]),
			canAddNewEntries());
	}

	/**
	 * ID を直接指定してこのクラスのインスタンスを生成します。
	 *
	 * @param id パラメータで渡されたクラスに存在するこのインスタンスを格納したフィールド名
	 * @param using 使用されるクラス
	 * @return このクラスのインスタンス
	 */
	public AnchorOptimizer getInstance(String id, Class<?> using) {
		return new AnchorOptimizer(this, id, null, using, canAddNewEntries());
	}

	/**
	 * ID を直接指定してこのクラスのインスタンスを生成します。
	 *
	 * @param id パラメータで渡されたクラスに存在するこのインスタンスを格納したフィールド名
	 * @param hint リポジトリにまだ登録されていない場合使用されるテーブル
	 * @param using 使用されるクラス
	 * @return このクラスのインスタンス
	 */
	public AnchorOptimizer getInstance(String id, ResourceLocator hint, Class<?> using) {
		return new AnchorOptimizer(this, id, hint, using, canAddNewEntries());
	}

	private static Class<?> getUsingClass(StackTraceElement first) {
		Class<?> using;
		try {
			using = Class.forName(first.getClassName());
		} catch (Exception e) {
			throw new IllegalStateException(e.toString());
		}

		return using;
	}
}
