package org.blendee.sql;

import java.util.function.Consumer;

import org.blendee.jdbc.ColumnMetadata;
import org.blendee.jdbc.DataTypeConverter;

/**
 * {@link Relationship} に含まれるカラムを表すクラスです。<br>
 * データベース上では同じカラムでも、カラムが属する {@link Relationship} が違う場合、それらは別物として扱われます。
 * @author 千葉 哲嗣
 * @see Relationship#getColumn(String)
 * @see Relationship#getColumns()
 */
public class RelationshipColumn implements Column {

	private final Relationship relationship;

	private final ColumnMetadata metadata;

	private final String name;

	private final Class<?> type;

	private final String id;

	private final String complementedName;

	private final int hashCode;

	RelationshipColumn(
		Relationship relationship,
		ColumnMetadata metadata,
		DataTypeConverter converter,
		String index) {
		this.relationship = relationship;
		this.metadata = metadata;
		this.name = metadata.getName();
		this.type = converter.convert(metadata.getType(), metadata.getTypeName());
		id = relationship.getId() + "_c" + index;
		complementedName = relationship.getId() + "." + metadata.getName();
		hashCode = id.hashCode();
	}

	RelationshipColumn() {
		this.relationship = null;
		this.metadata = null;
		this.name = null;
		this.type = null;
		id = null;
		complementedName = null;
		hashCode = 0;
	}

	/**
	 * コピーコンストラクタ
	 * @param copyFrom コピー元
	 */
	RelationshipColumn(Column copyFrom) {
		this.relationship = copyFrom.getRelationship();
		this.metadata = copyFrom.getColumnMetadata();
		this.name = copyFrom.getName();
		this.type = copyFrom.getType();
		id = copyFrom.getId();
		complementedName = copyFrom.getComplementedName();
		hashCode = copyFrom.hashCode();
	}

	@Override
	public int hashCode() {
		return hashCode;
	}

	@Override
	public boolean equals(Object o) {
		return o instanceof RelationshipColumn && id.equals(((RelationshipColumn) o).id);
	}

	@Override
	public int compareTo(Column target) {
		return id.compareTo(target.getId());
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public String toString() {
		return complementedName;
	}

	@Override
	public Criteria getCriteria(Bindable bindable) {
		return CriteriaFactory.create(this, bindable);
	}

	@Override
	public Relationship getRelationship() {
		return relationship;
	}

	@Override
	public Relationship getRootRelationship() {
		return relationship.getRoot();
	}

	@Override
	public void consumeRelationship(Consumer<Relationship> consumer) {
		consumer.accept(relationship);
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Class<?> getType() {
		return type;
	}

	@Override
	public ColumnMetadata getColumnMetadata() {
		return metadata;
	}

	@Override
	public String getComplementedName() {
		return complementedName;
	}

	@Override
	public RelationshipColumn replicate() {
		return this;
	}
}
