package org.blendee.orm;

import org.blendee.jdbc.BlendeeException;
import org.blendee.sql.Column;
import org.blendee.sql.Relationship;

/**
 * 検索時に SELECT 句に使用されていなかったカラムの値を取得しようとしたときにスローされる例外です。
 * @author 千葉 哲嗣
 * @see SelectedValues
 */
public class IllegalValueException extends BlendeeException {

	private static final long serialVersionUID = -5391436397301623589L;

	IllegalValueException(String message) {
		super(message);
	}

	static final String buildMessage(Column column) {
		var relationship = column.getRelationship();

		//"SELECT 句にないカラム "
		//+ relationship.getTablePath().getTableName()
		//+ "."
		//+ column.getName()
		//+ " を使用することはできません ["
		//+ buildRelationshipPart(column.getRelationship())
		//+ "]";
		return "Can not use column "
			+ relationship.getTablePath().getTableName()
			+ "."
			+ column.getName()
			+ " that is not in the SELECT clause. ["
			+ buildRelationshipPart(column.getRelationship())
			+ "]";
	}

	private static String buildRelationshipPart(Relationship relationship) {
		if (relationship.isRoot()) {
			var path = relationship.getTablePath();
			return path.getTableName();
		}

		var fkNames = relationship.getCrossReference().getForeignKeyColumnNames();
		var fk = fkNames.length == 1 ? fkNames[0] : "(" + String.join(", ", fkNames) + ")";

		return buildRelationshipPart(relationship.getParent()) + "." + fk + " -> " + relationship.getTablePath().getTableName();
	}
}
