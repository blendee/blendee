/*--*//*@formatter:off*//*--*/package /*++{0}++*//*--*/org.blendee.develop.ormgen/*--*/;

import javax.annotation.Generated;

import org.blendee.support.BEntity;
import org.blendee.support.annotation.EntityGetter;
/*--*/import org.blendee.support.annotation.EntityRelationship;/*--*/
import org.blendee.support.annotation.EntitySetter;
import org.blendee.support.annotation.Resource;
/*++{8}++*/
import org.blendee.jdbc.BlendeeContext;
import org.blendee.jdbc.ResourceLocator;
import org.blendee.orm.DataObject;
import org.blendee.sql.ValueExtractorsConfigure;
import org.blendee.sql.Binder;
import org.blendee.sql.Relationship;
import org.blendee.sql.RelationshipFactory;
import org.blendee.sql.ValueExtractor;

/**
 * 自動生成された '{'@link BEntity'}' の実装クラスです。
 *
{6}
 */
@Resource(schema = "{1}", table = "{2}")
@Generated(value = /*++'++*/{/*++'++*/"{9}"/*++'++*/}/*++'++*/)
/*++{7}++*/public class /*++{2}++*//*--*/EntityBase/*--*/
	extends /*++{3}++*//*--*/Object/*--*/
	implements BEntity /*++'++*/{/*++'++*/

	/**
	 * この定数クラスのスキーマ名
	 */
	public static final String SCHEMA = "{1}";

	/**
	 * この定数クラスのテーブル名
	 */
	public static final String TABLE = "{2}";

	/**
	 * この定数クラスのテーブルを指す '{'@link ResourceLocator'}'
	 */
	public static final ResourceLocator $TABLE = new ResourceLocator(SCHEMA, TABLE);

	private final DataObject $data;

	private final Relationship $relationship;

	/**
	 * 登録用コンストラクタです。
	 */
	public /*++{2}++*//*--*/EntityBase/*--*/() /*++'++*/{/*++'++*/
		$relationship = BlendeeContext.get(RelationshipFactory.class).getInstance($TABLE);
		$data = new DataObject($relationship);
	/*++'++*/}/*++'++*/

	/**
	 * 参照、更新用コンストラクタです。
	 *
	 * @param data 値を持つ '{'@link DataObject'}'
	 */
	public /*++{2}++*//*--*/EntityBase/*--*/(DataObject data) /*++'++*/{/*++'++*/
		$relationship = BlendeeContext.get(RelationshipFactory.class).getInstance($TABLE);
		this.$data = data;
	/*++'++*/}/*++'++*/

	@Override
	public DataObject getDataObject() /*++'++*/{/*++'++*/
		return $data;
	/*++'++*/}/*++'++*/

/*++{4}++*/
/*==EntityPropertyAccessorPart==*/
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
	@EntitySetter(column = "{1}", type = /*++{2}++*//*--*/Object/*--*/.class)
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
	@EntityGetter(column = "{1}", type = /*++{2}++*//*--*/Object/*--*/.class, optional = /*++{8}++*//*--*/false/*--*/)
	public /*++{5}++*/ /*--*/String/*--*/get/*++{0}++*/() /*++'++*/{/*++'++*/
		Binder binder = $data.getBinder("{1}");
		if (binder == null) return null;
		return /*++{6}++*/(/*++{2}++*//*--*/String/*--*/) binder.getValue()/*++{7}++*/;
	/*++'++*/}/*++'++*/

/*==EntityPropertyAccessorPart==*/
/*++{5}++*/
/*==EntityRelationshipPart==*/
	/**
	 * 参照先テーブル名 {0}<br>
	 * 外部キー名 {1}<br>
	 */
	public static final String /*++{0}++*/_BY_/*++{1}++*/ = "{1}";

	/**
	 * このレコードが参照しているレコードの Entity を返します。<br>
	 *
	 * 参照先テーブル名 {0}<br>
	 * 外部キー名 {1}<br>
	 * 項目名 {2}<br>
	 *
	 * @return 参照しているレコードの Entity
	 */
	@EntityRelationship(fk = "{1}", referenced = /*++{0}++*//*--*/EntityBase/*--*/.class)
	public /*++{0}++*//*--*/EntityBase/*--*/ /*++get{3}++*//*--*/getRelationship/*--*/() /*++'++*/{/*++'++*/
		return new /*++{0}++*//*--*/EntityBase/*--*/(
			$data.getDataObject(/*++{0}++*/_BY_/*++{1}++*/));
	/*++'++*/}/*++'++*/

/*==EntityRelationshipPart==*/
/*++'++*/}/*++'++*/
