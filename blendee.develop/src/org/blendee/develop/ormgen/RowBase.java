/*--*//*@formatter:off*//*--*/package /*++{0}.row++*//*--*/org.blendee.develop.ormgen/*--*/;

/*++{8}++*/
import org.blendee.jdbc.ContextManager;
import org.blendee.jdbc.TablePath;
import org.blendee.jdbc.Result;
import org.blendee.orm.ColumnNameDataObjectBuilder;
import org.blendee.orm.DataObject;
import org.blendee.sql.Binder;
import org.blendee.sql.Relationship;
import org.blendee.sql.RelationshipFactory;
import org.blendee.sql.ValueExtractor;
import org.blendee.sql.ValueExtractorsConfigure;
import org.blendee.support.Row;
import org.blendee.support.annotation.Resource;
import org.blendee.support.annotation.RowGetter;
/*--*/import org.blendee.support.annotation.RowRelationship;/*--*/
import org.blendee.support.annotation.RowSetter;

/**
 * 自動生成された '{'@link Row'}' の実装クラスです。
{6}
 */
@Resource(schema = "{1}", table = "{2}")
/*++{7}++*/public class /*++{2}++*//*--*/RowBase/*--*/
extends /*++{3}++*//*--*/Object/*--*/
	implements Row /*++'++*/{/*++'++*/

	/**
	 * この定数クラスのスキーマ名
	 */
	public static final String SCHEMA = "{1}";

	/**
	 * この定数クラスのテーブル名
	 */
	public static final String TABLE = "{2}";

	/**
	 * この定数クラスのテーブルを指す '{'@link TablePath'}'
	 */
	public static final TablePath $TABLE = new TablePath(SCHEMA, TABLE);

	private final DataObject $data;

	private final Relationship $relationship;

	/**
	 * 登録用コンストラクタです。
	 */
	public /*++{2}++*//*--*/RowBase/*--*/() /*++'++*/{/*++'++*/
		$relationship = ContextManager.get(RelationshipFactory.class).getInstance($TABLE);
		$data = new DataObject($relationship);
	/*++'++*/}/*++'++*/

	/**
	 * 参照、更新用コンストラクタです。
	 * @param data 値を持つ '{'@link DataObject'}'
	 */
	public /*++{2}++*//*--*/RowBase/*--*/(DataObject data) /*++'++*/{/*++'++*/
		$relationship = ContextManager.get(RelationshipFactory.class).getInstance($TABLE);
		this.$data = data;
	/*++'++*/}/*++'++*/

	/**
	 * 参照、更新用コンストラクタです。<br>
	 * aggregate の検索結果からカラム名により値を取り込みます。
	 * @param result 値を持つ '{'@link Result'}'
	 */
	public /*++{2}++*//*--*/RowBase/*--*/(Result result) /*++'++*/{/*++'++*/
		$relationship = ContextManager.get(RelationshipFactory.class).getInstance($TABLE);
		this.$data = ColumnNameDataObjectBuilder.build(result, $relationship, ContextManager.get(ValueExtractorsConfigure.class).getValueExtractors());
	/*++'++*/}/*++'++*/

	@Override
	public DataObject dataObject() /*++'++*/{/*++'++*/
		return $data;
	/*++'++*/}/*++'++*/

	@Override
	public TablePath tablePath() /*++'++*/{/*++'++*/
		return $TABLE;
	/*++'++*/}/*++'++*/

/*++{4}++*/
/*==RowPropertyAccessorPart==*/
	/**
{3}
	 */
	public static final String /*++{1}++*//*--*/columnName/*--*/ = "{1}";

	/**
	 * setter
{3}
	 * @param value {2}
	 */
	@RowSetter(column = "{1}", type = /*++{2}++*//*--*/Object/*--*/.class)
	public void set/*++{0}++*/(/*++{2}++*//*--*/Object/*--*/ value) /*++'++*/{/*++'++*/
		/*++{4}++*/ValueExtractor valueExtractor = ContextManager.get(ValueExtractorsConfigure.class).getValueExtractors().selectValueExtractor(
			$relationship.getColumn("{1}").getType());
		$data.setValue("{1}", valueExtractor.extractAsBinder(value));
	/*++'++*/}/*++'++*/

	/**
	 * getter
{3}
	 * @return {2}
	 */
	@RowGetter(column = "{1}", type = /*++{2}++*//*--*/Object/*--*/.class, optional = /*++{8}++*//*--*/false/*--*/)
	public /*++{5}++*/ /*--*/String/*--*/get/*++{0}++*/() /*++'++*/{/*++'++*/
		Binder binder = $data.getBinder("{1}");
		return /*++{6}++*/(/*++{2}++*//*--*/String/*--*/) binder.getValue()/*++{7}++*/;
	/*++'++*/}/*++'++*/

/*==RowPropertyAccessorPart==*/
/*++{5}++*/
/*==RowRelationshipPart==*/
	/**
	 * 参照先テーブル名 {0}<br>
	 * 外部キー名 {1}<br>
	 */
	public static final String /*++{0}++*/$/*++{1}++*/ = "{1}";

	/**
	 * このレコードが参照しているレコードの Row を返します。<br>
	 * 参照先テーブル名 {0}<br>
	 * 外部キー名 {1}<br>
	 * 項目名 {2}<br>
	 * @return 参照しているレコードの Row
	 */
	@RowRelationship(fk = "{1}", referenced = /*++{4}.row.{0}++*//*--*/RowBase/*--*/.class)
	public /*++{4}.row.{0}++*//*--*/RowBase/*--*/ /*++{3}++*//*--*/getRelationship/*--*/() /*++'++*/{/*++'++*/
		return new /*++{4}.row.{0}++*//*--*/RowBase/*--*/(
			$data.getDataObject(/*++{0}++*/$/*++{1}++*/));
	/*++'++*/}/*++'++*/

/*==RowRelationshipPart==*/
/*++'++*/}/*++'++*/
