/*--*//*@formatter:off*//*--*/package /*++{0}++*//*--*/org.blendee.develop.ormgen/*--*/;

import javax.annotation.Generated;

import org.blendee.support.Row;
import org.blendee.support.annotation.RowGetter;
/*--*/import org.blendee.support.annotation.RowRelationship;/*--*/
import org.blendee.support.annotation.RowSetter;
import org.blendee.support.annotation.Resource;
/*++{8}++*/
import org.blendee.jdbc.BlendeeContext;
import org.blendee.jdbc.TablePath;
import org.blendee.orm.DataObject;
import org.blendee.sql.ValueExtractorsConfigure;
import org.blendee.sql.Binder;
import org.blendee.sql.Relationship;
import org.blendee.sql.RelationshipFactory;
import org.blendee.sql.ValueExtractor;

/**
 * 自動生成された '{'@link Row'}' の実装クラスです。
 *
{6}
 */
@Resource(schema = "{1}", table = "{2}")
@Generated(value = /*++'++*/{/*++'++*/"{9}"/*++'++*/}/*++'++*/)
/*++{7}++*/public class RowBase
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
	public RowBase() /*++'++*/{/*++'++*/
		$relationship = BlendeeContext.get(RelationshipFactory.class).getInstance($TABLE);
		$data = new DataObject($relationship);
	/*++'++*/}/*++'++*/

	/**
	 * 参照、更新用コンストラクタです。
	 *
	 * @param data 値を持つ '{'@link DataObject'}'
	 */
	public RowBase(DataObject data) /*++'++*/{/*++'++*/
		$relationship = BlendeeContext.get(RelationshipFactory.class).getInstance($TABLE);
		this.$data = data;
	/*++'++*/}/*++'++*/

	@Override
	public DataObject getDataObject() /*++'++*/{/*++'++*/
		return $data;
	/*++'++*/}/*++'++*/

/*++{4}++*/
/*==RowPropertyAccessorPart==*/
	/**
{3}
	 */
	public static final String /*++{1}++*//*--*/columnName/*--*/ = "{1}";

	/**
	 * setter
	 *
{3}
	 *
	 * @param value {2}
	 */
	@RowSetter(column = "{1}", type = /*++{2}++*//*--*/Object/*--*/.class)
	public void set/*++{0}++*/(/*++{2}++*//*--*/Object/*--*/ value) /*++'++*/{/*++'++*/
		/*++{4}++*/ValueExtractor valueExtractor = BlendeeContext.get(ValueExtractorsConfigure.class).getValueExtractors().selectValueExtractor(
			$relationship.getColumn("{1}").getType());
		$data.setValue("{1}", valueExtractor.extractAsBinder(value));
	/*++'++*/}/*++'++*/

	/**
	 * getter
	 *
{3}
	 *
	 * @return {2}
	 */
	@RowGetter(column = "{1}", type = /*++{2}++*//*--*/Object/*--*/.class, optional = /*++{8}++*//*--*/false/*--*/)
	public /*++{5}++*/ /*--*/String/*--*/get/*++{0}++*/() /*++'++*/{/*++'++*/
		Binder binder = $data.getBinder("{1}");
		if (binder == null) return null;
		return /*++{6}++*/(/*++{2}++*//*--*/String/*--*/) binder.getValue()/*++{7}++*/;
	/*++'++*/}/*++'++*/

/*==RowPropertyAccessorPart==*/
/*++{5}++*/
/*==RowRelationshipPart==*/
	/**
	 * 参照先テーブル名 {0}<br>
	 * 外部キー名 {1}<br>
	 */
	public static final String /*++{0}++*/_BY_/*++{1}++*/ = "{1}";

	/**
	 * このレコードが参照しているレコードの Row を返します。<br>
	 *
	 * 参照先テーブル名 {0}<br>
	 * 外部キー名 {1}<br>
	 * 項目名 {2}<br>
	 *
	 * @return 参照しているレコードの Row
	 */
	@RowRelationship(fk = "{1}", referenced = /*++{0}++*//*--*/RowBase/*--*/.class)
	public /*++{0}++*//*--*/RowBase/*--*/ /*++get{3}++*//*--*/getRelationship/*--*/() /*++'++*/{/*++'++*/
		return new /*++{0}++*//*--*/RowBase/*--*/(
			$data.getDataObject(/*++{0}++*/_BY_/*++{1}++*/));
	/*++'++*/}/*++'++*/

/*==RowRelationshipPart==*/
/*++'++*/}/*++'++*/
