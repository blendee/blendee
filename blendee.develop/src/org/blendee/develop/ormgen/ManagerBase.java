/*--*//*@formatter:off*//*--*/package /*++{0}++*//*--*/org.blendee.develop.ormgen/*--*/;

import javax.annotation.Generated;

import org.blendee.jdbc.TablePath;
import org.blendee.orm.DataAccessHelper;
import org.blendee.orm.DataObjectIterator;
import org.blendee.orm.QueryOption;
import org.blendee.orm.RowLockOption;
import org.blendee.orm.DataObject;
import org.blendee.selector.Optimizer;
import org.blendee.selector.SimpleOptimizer;
import org.blendee.sql.Condition;
import org.blendee.sql.OrderByClause;
import org.blendee.support.BEntityManager;
import org.blendee.support.EntityIterator;

/**
 * 自動生成された '{'@link BEntityManager'}' の実装クラスです。
 *
{3}
 */
@Generated(value = /*++'++*/{/*++'++*/"{4}"/*++'++*/}/*++'++*/)
public class /*++{1}Manager++*//*--*/ManagerBase/*--*/
	extends /*++{2}++*//*--*/Object/*--*/
	implements BEntityManager</*++{1}++*//*--*/EntityBase/*--*/> /*++'++*/{/*++'++*/

	private final DataAccessHelper helper = new DataAccessHelper(false);

	/**
	 * パラメータの条件にマッチするレコードを検索し、 '{'@link {1}Iterator'}' として返します。
	 * <br>
	 * '{'@link Optimizer} には '{'@link SimpleOptimizer'}' が使用されます。
	 *
	 * @param condition WHERE 句となる条件
	 * @param order  ORDER 句
	 * @param options 行ロックオプション '{'@link RowLockOption'}' 等
	 * @return '{'@link EntityIterator'}'
	 */
	public /*++{1}Iterator++*//*--*/IteratorBase/*--*/ select(
		Condition condition,
		OrderByClause order,
		QueryOption... options) /*++'++*/{/*++'++*/
		return select(
			new SimpleOptimizer(getTablePath()),
			condition,
			order,
			options);
	/*++'++*/}/*++'++*/

	/**
	 * パラメータの条件にマッチするレコードを検索し、 '{'@link {1}Iterator'}' として返します。
	 * <br>
	 * '{'@link Optimizer} には '{'@link SimpleOptimizer'}' が使用されます。
	 * <br>
	 * '{'@link RowLockOption'}' には '{'@link RowLockOption#NONE'}' が使用されます。
	 *
	 * @param condition WHERE 句となる条件
	 * @param order  ORDER 句
	 * @return '{'@link EntityIterator'}'
	 */
	public /*++{1}Iterator++*//*--*/IteratorBase/*--*/ select(
		Condition condition,
		OrderByClause order) /*++'++*/{/*++'++*/
		return select(
			condition,
			order,
			null,
			RowLockOption.NONE);
	/*++'++*/}/*++'++*/

	/**
	 * パラメータの条件にマッチするレコードを検索し、 '{'@link {1}Iterator'}' として返します。
	 *
	 * @param optimizer SELECT 句を制御する '{'@link Optimizer'}'
	 * @param condition WHERE 句となる条件
	 * @param order  ORDER 句
	 * @param options 行ロックオプション '{'@link RowLockOption'}' 等
	 * @return '{'@link EntityIterator'}'
	 */
	public /*++{1}Iterator++*//*--*/IteratorBase/*--*/ select(
		Optimizer optimizer,
		Condition condition,
		OrderByClause order,
		QueryOption... options) /*++'++*/{/*++'++*/
		return new /*++{1}Iterator++*//*--*/IteratorBase/*--*/(helper.getDataObjects(
			optimizer,
			condition,
			order,
			options));
	/*++'++*/}/*++'++*/

	/**
	 * パラメータの条件にマッチするレコードを検索し、 '{'@link {1}Iterator'}' として返します。
	 * <br>
	 * '{'@link RowLockOption'}' には '{'@link RowLockOption#NONE'}' が使用されます。
	 *
	 * @param optimizer SELECT 句を制御する '{'@link Optimizer'}'
	 * @param condition WHERE 句となる条件
	 * @param order  ORDER 句
	 * @return '{'@link EntityIterator'}'
	 */
	public /*++{1}Iterator++*//*--*/IteratorBase/*--*/ select(
		Optimizer optimizer,
		Condition condition,
		OrderByClause order) /*++'++*/{/*++'++*/
		return select(
			optimizer,
			condition,
			order,
			null,
			RowLockOption.NONE);
	/*++'++*/}/*++'++*/

	@Override
	public /*++{1}++*//*--*/EntityBase/*--*/ createEntity(DataObject data) /*++'++*/{/*++'++*/
		return new /*++{1}++*//*--*/EntityBase/*--*/(data);
	/*++'++*/}/*++'++*/

	@Override
	public TablePath getTablePath() /*++'++*/{/*++'++*/
		return /*++{1}++*//*--*/EntityBase/*--*/.$TABLE;
	/*++'++*/}/*++'++*/

	@Override
	public DataAccessHelper getDataAccessHelper() /*++'++*/{/*++'++*/
		return helper;
	/*++'++*/}/*++'++*/

	/**
	 * '{'@link {1}Manager'}' が使用する Iterator クラスです。
	 */
	public class /*++{1}Iterator++*//*--*/IteratorBase/*--*/
		extends /*++EntityIterator<{1}>++*//*--*/EntityIterator<EntityBase>/*--*/ /*++'++*/{/*++'++*/

		/**
		 * 唯一のコンストラクタです。
		 *
		 * @param iterator 
		 */
		private /*++{1}Iterator++*//*--*/IteratorBase/*--*/(
			DataObjectIterator iterator) /*++'++*/{/*++'++*/
			super(iterator);
		/*++'++*/}/*++'++*/

		@Override
		public /*++{1}++*//*--*/EntityBase/*--*/ next() /*++'++*/{/*++'++*/
			return createEntity(nextDataObject());
		/*++'++*/}/*++'++*/
	/*++'++*/}/*++'++*/
/*++'++*/}/*++'++*/
