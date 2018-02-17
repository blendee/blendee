/*--*//*@formatter:off*//*--*/package /*++{0}.manager++*//*--*/org.blendee.develop.ormgen/*--*/;

import org.blendee.jdbc.TablePath;
import org.blendee.orm.DataAccessHelper;
import org.blendee.orm.DataObject;
import org.blendee.orm.DataObjectIterator;
import org.blendee.orm.QueryOption;
import org.blendee.orm.RowLockOption;
import org.blendee.selector.Optimizer;
import org.blendee.selector.SimpleOptimizer;
import org.blendee.sql.Criteria;
import org.blendee.sql.OrderByClause;
import org.blendee.support.RowIterator;
import org.blendee.support.RowManager;

/**
 * 自動生成された '{'@link RowManager'}' の実装クラスです。
{3}
 */
public class /*++{1}Manager++*//*--*/ManagerBase/*--*/
	extends /*++{2}++*//*--*/Object/*--*/
	implements RowManager</*++{0}.row.{1}++*//*--*/RowBase/*--*/> /*++'++*/{/*++'++*/

	/**
	 * パラメータの条件にマッチするレコードを検索し、 '{'@link {1}Iterator'}' として返します。<br>
	 * '{'@link Optimizer} には '{'@link SimpleOptimizer'}' が使用されます。
	 * @param criteria WHERE 句となる条件
	 * @param order  ORDER 句
	 * @param options 行ロックオプション '{'@link RowLockOption'}' 等
	 * @return '{'@link RowIterator'}'
	 */
	public /*++{1}Iterator++*//*--*/IteratorBase/*--*/ select(
		Criteria criteria,
		OrderByClause order,
		QueryOption... options) /*++'++*/{/*++'++*/
		return select(
			new SimpleOptimizer(getTablePath()),
			criteria,
			order,
			options);
	/*++'++*/}/*++'++*/

	/**
	 * パラメータの条件にマッチするレコードを検索し、 '{'@link {1}Iterator'}' として返します。<br>
	 * '{'@link Optimizer} には '{'@link SimpleOptimizer'}' が使用されます。<br>
	 * '{'@link RowLockOption'}' には '{'@link RowLockOption#NONE'}' が使用されます。
	 * @param criteria WHERE 句となる条件
	 * @param order  ORDER 句
	 * @return '{'@link RowIterator'}'
	 */
	public /*++{1}Iterator++*//*--*/IteratorBase/*--*/ select(
		Criteria criteria,
		OrderByClause order) /*++'++*/{/*++'++*/
		return select(
			criteria,
			order,
			null,
			RowLockOption.NONE);
	/*++'++*/}/*++'++*/

	/**
	 * パラメータの条件にマッチするレコードを検索し、 '{'@link {1}Iterator'}' として返します。
	 * @param optimizer SELECT 句を制御する '{'@link Optimizer'}'
	 * @param criteria WHERE 句となる条件
	 * @param order  ORDER 句
	 * @param options 行ロックオプション '{'@link RowLockOption'}' 等
	 * @return '{'@link RowIterator'}'
	 */
	public /*++{1}Iterator++*//*--*/IteratorBase/*--*/ select(
		Optimizer optimizer,
		Criteria criteria,
		OrderByClause order,
		QueryOption... options) /*++'++*/{/*++'++*/
		return new /*++{1}Iterator++*//*--*/IteratorBase/*--*/(new DataAccessHelper().getDataObjects(
			optimizer,
			criteria,
			order,
			options));
	/*++'++*/}/*++'++*/

	/**
	 * パラメータの条件にマッチするレコードを検索し、 '{'@link {1}Iterator'}' として返します。<br>
	 * '{'@link RowLockOption'}' には '{'@link RowLockOption#NONE'}' が使用されます。
	 * @param optimizer SELECT 句を制御する '{'@link Optimizer'}'
	 * @param criteria WHERE 句となる条件
	 * @param order  ORDER 句
	 * @return '{'@link RowIterator'}'
	 */
	public /*++{1}Iterator++*//*--*/IteratorBase/*--*/ select(
		Optimizer optimizer,
		Criteria criteria,
		OrderByClause order) /*++'++*/{/*++'++*/
		return select(
			optimizer,
			criteria,
			order,
			null,
			RowLockOption.NONE);
	/*++'++*/}/*++'++*/

	@Override
	public /*++{0}.row.{1}++*//*--*/RowBase/*--*/ createRow(DataObject data) /*++'++*/{/*++'++*/
		return new /*++{0}.row.{1}++*//*--*/RowBase/*--*/(data);
	/*++'++*/}/*++'++*/

	@Override
	public TablePath getTablePath() /*++'++*/{/*++'++*/
		return /*++{0}.row.{1}++*//*--*/RowBase/*--*/.$TABLE;
	/*++'++*/}/*++'++*/

	/**
	 * '{'@link {1}Manager'}' が使用する Iterator クラスです。
	 */
	public class /*++{1}Iterator++*//*--*/IteratorBase/*--*/
		extends /*++RowIterator<{0}.row.{1}>++*//*--*/RowIterator<RowBase>/*--*/ /*++'++*/{/*++'++*/

		/**
		 * 唯一のコンストラクタです。
		 * @param iterator
		 */
		private /*++{1}Iterator++*//*--*/IteratorBase/*--*/(
			DataObjectIterator iterator) /*++'++*/{/*++'++*/
			super(iterator);
		/*++'++*/}/*++'++*/

		@Override
		public /*++{0}.row.{1}++*//*--*/RowBase/*--*/ next() /*++'++*/{/*++'++*/
			return createRow(nextDataObject());
		/*++'++*/}/*++'++*/
	/*++'++*/}/*++'++*/
/*++'++*/}/*++'++*/
