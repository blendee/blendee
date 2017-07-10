package org.blendee.sql;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.blendee.jdbc.BlendeeContext;
import org.blendee.jdbc.CrossReference;
import org.blendee.jdbc.TablePath;

/**
 * SELECT 文の FROM 句を表すクラスです。
 * @author 千葉 哲嗣
 * @see QueryBuilder#QueryBuilder(FromClause)
 */
public class FromClause {

	/**
	 * テーブルの結合方式を表す列挙型です。
	 * @author 千葉 哲嗣
	 * @version $Name: v0_4_20090119a $
	 */
	public enum JoinType {

		/**
		 * INNER JOIN
		 */
		INNER_JOIN,

		/**
		 * FULL OUTER JOIN
		 */
		FULL_OUTER_JOIN,

		/**
		 * LEFT OUTER JOIN
		 */
		LEFT_OUTER_JOIN,

		/**
		 * RIGHT OUTER JOIN
		 */
		RIGHT_OUTER_JOIN;

		@Override
		public String toString() {
			switch (this) {
			case INNER_JOIN:
				return "INNER JOIN";

			case FULL_OUTER_JOIN:
				return "FULL OUTER JOIN";

			case LEFT_OUTER_JOIN:
				return "LEFT OUTER JOIN";

			case RIGHT_OUTER_JOIN:
				return "RIGHT OUTER JOIN";
			default:
				throw new Error();
			}
		}
	}

	private final Relationship root;

	private final Set<RelationshipContainer> localRelationships = new LinkedHashSet<>();

	private final List<JointContainer> joints = new LinkedList<>();

	private String cache;

	/**
	 * パラメータのテーブルをルートとする FROM 句を生成します。
	 * @param path テーブルのルート
	 */
	public FromClause(TablePath path) {
		root = BlendeeContext.get(RelationshipFactory.class).getInstance(path);
		localRelationships.add(new RelationshipContainer(root));
	}

	/**
	 * この FROM 句のテーブルをルートにした {@link Relationship} と結合します。
	 * @param type 結合方式
	 * @param relationship 同一ルート {@link Relationship}
	 * @throws IllegalStateException 結合できないテーブルを使用している場合
	 */
	public void join(JoinType type, Relationship relationship) {
		if (localRelationships.contains(relationship)) return;
		if (!root.equals(relationship.getRoot())) throw new IllegalStateException("同一ルートではないので、結合できません");
		cache = null;
		Set<Relationship> set = new HashSet<>();
		relationship.addParentTo(set);
		set.add(relationship);
		for (Relationship element : set) {
			localRelationships.add(new RelationshipContainer(type, element));
		}
	}

	/**
	 * この FROM 句のテーブルをルートにした {@link Relationship} と結合します。
	 * @param type 結合方式
	 * @param path このインスタンスのルートのツリーに含まれる {@link TablePath}
	 * @throws IllegalStateException 結合できないテーブルを使用している場合
	 * @throws IllegalStateException ツリー内に同一テーブルが複数あるため、あいまいな指定がされている場合
	 */
	public void join(JoinType type, TablePath path) {
		Relationship relationship = RelationshipFactory.convert(root, path);
		if (localRelationships.contains(relationship)) return;
		cache = null;
		Set<Relationship> set = new HashSet<>();
		relationship.addParentTo(set);
		set.add(relationship);
		for (Relationship element : set) {
			localRelationships.add(new RelationshipContainer(type, element));
		}
	}

	/**
	 * 他の FROM 句と結合するためのジョイントを生成します。
	 * @param base このインスタンスとルートが同じで、結合する {@link Relationship}
	 * @param columnNames base に属する結合するカラム
	 * @return ジョイント
	 * @throws IllegalArgumentException columnNames が空の場合
	 * @throws IllegalStateException 結合できないテーブルを使用している場合
	 */
	public Joint getJoint(Relationship base, String[] columnNames) {
		if (columnNames.length < 1) throw new IllegalArgumentException("columnNames が空です");
		if (!root.equals(base.getRoot())) throw new IllegalStateException("同一ルートではないので、結合できません");
		Column[] columns = new Column[columnNames.length];
		for (int i = 0; i < columnNames.length; i++) {
			columns[i] = base.getColumn(columnNames[i]);
		}
		return new Joint(base, columns);
	}

	/**
	 * 他の FROM 句と結合するためのジョイントを生成します。
	 * @param path このインスタンスのルートのツリーに含まれていて、結合する {@link Relationship}
	 * @param columnNames base に属する結合するカラム
	 * @return ジョイント
	 * @throws IllegalArgumentException columnNames が空の場合
	 * @throws IllegalStateException 結合できないテーブルを使用している場合
	 * @throws IllegalStateException ツリー内に同一テーブルが複数あるため、あいまいな指定がされている場合
	 */
	public Joint getJoint(TablePath path, String[] columnNames) {
		if (columnNames.length < 1) throw new IllegalArgumentException("columnNames が空です");
		Relationship base = RelationshipFactory.convert(root, path);
		Column[] columns = new Column[columnNames.length];
		for (int i = 0; i < columnNames.length; i++) {
			columns[i] = base.getColumn(columnNames[i]);
		}
		return new Joint(base, columns);
	}

	/**
	 * この FROM 句を使用する SQL 文のルートとなる {@link Relationship} を返します。
	 * @return ルート
	 */
	public Relationship getRoot() {
		return root;
	}

	@Override
	public String toString() {
		if (cache != null) return cache;
		if (!isJoined()) {
			cache = " FROM " + root.getTablePath();
		} else {
			LinkedList<String> result = process();
			result.addFirst(root.getTablePath() + " " + root.getID());
			cache = " FROM " + String.join(" ", result);
		}
		return cache;
	}

	void clearRelationships() {
		cache = null;
		localRelationships.clear();
		localRelationships.add(new RelationshipContainer(root));
	}

	boolean isJoined() {
		return localRelationships.size() > 1 || joints.size() > 1;
	}

	void addUsingRelationshipsTo(Collection<Relationship> collection) {
		for (RelationshipContainer element : localRelationships) {
			collection.add(element.relationship);
		}
		for (JointContainer element : joints) {
			element.addUsingRelationships(collection);
		}
	}

	FromClause replicate() {
		FromClause clone = new FromClause(root.getTablePath());

		//中身はImmutableなのでそのままコピー
		clone.localRelationships.addAll(localRelationships);
		clone.joints.addAll(joints);

		return clone;
	}

	private LinkedList<String> process() {
		LinkedList<RelationshipContainer> localList = new LinkedList<>();
		localList.addAll(localRelationships);
		Collections.sort(localList);
		localList.removeFirst();
		LinkedList<String> clause = new LinkedList<>();
		for (RelationshipContainer element : localList) {
			element.append(clause);
		}
		for (JointContainer element : joints) {
			element.append(clause);
		}
		return clause;
	}

	private static String processPart(
		JoinType type,
		Relationship relationship,
		Column[] left,
		Column[] right) {
		Condition condition = ConditionFactory.createCondition();
		Bindable[] bindables = new Bindable[0];
		for (int i = 0; i < left.length; i++) {
			condition.and(ConditionFactory.createCondition("{0} = {1}", new Column[] { left[i], right[i] }, bindables));
		}

		return type
			+ " "
			+ relationship.getTablePath()
			+ " "
			+ relationship.getID()
			+ " ON ("
			+ condition.toString(true).trim()
			+ ")";
	}

	/**
	 * FROM 句同士の結合を表すクラスです。
	 * @author 千葉 哲嗣
	 * @version $Name: v0_4_20090119a $
	 */
	public class Joint {

		private final Relationship base;

		private final Column[] columns;

		private Joint(Relationship base, Column[] columns) {
			this.base = base;
			this.columns = columns;
		}

		/**
		 * 他の FROM 句で生成したジョイントと、このジョイントを結合します。
		 * @param type 結合方式
		 * @param another 他の FROM 句で生成したジョイント
		 * @throws IllegalArgumentException another を生成した FROM 句とこの FROM 句のルートとなるテーブルが同じ場合
		 * @throws IllegalArgumentException another のカラム数とこのインスタンスの持つカラム数が違う場合
		 */
		public void join(JoinType type, Joint another) {
			if (base.getRoot().equals(another.base.getRoot()))
				throw new IllegalArgumentException("同一ルート同士は結合できません");

			if (columns.length != another.columns.length)
				throw new IllegalArgumentException("カラム数が一致しません");

			cache = null;
			joints.add(new JointContainer(type, columns, another));
		}

		private FromClause getFromClause() {
			return FromClause.this;
		}
	}

	/**
	 * Immutable
	 */
	private static class RelationshipContainer implements Comparable<RelationshipContainer> {

		private final JoinType type;

		private final Relationship relationship;

		private RelationshipContainer(JoinType type, Relationship relationship) {
			this.type = type;
			this.relationship = relationship;
		}

		private RelationshipContainer(Relationship relationship) {
			this(null, relationship);
		}

		@Override
		public boolean equals(Object o) {
			//Relationship が含まれているか検査できるように Relationship とも比較できる
			if (o instanceof RelationshipContainer) {
				return relationship.equals(((RelationshipContainer) o).relationship);
			} else if (o instanceof Relationship) {
				return relationship.equals(o);
			}
			return false;
		}

		@Override
		public int hashCode() {
			return relationship.hashCode();
		}

		@Override
		public int compareTo(RelationshipContainer target) {
			return relationship.compareTo(target.relationship);
		}

		private void append(List<String> list) {
			CrossReference reference = relationship.getCrossReference();
			String[] foreignKeyColumnNames = reference.getForeignKeyColumnNames();
			String[] primaryKeyColumnNames = reference.getPrimaryKeyColumnNames();

			int length = foreignKeyColumnNames.length;

			Relationship parentRelationship = relationship.getParent();
			Column[] left = new Column[length];
			Column[] right = new Column[length];
			for (int i = 0; i < length; i++) {
				left[i] = parentRelationship.getColumn(foreignKeyColumnNames[i]);
				right[i] = relationship.getColumn(primaryKeyColumnNames[i]);
			}
			list.add(processPart(type, relationship, left, right));
		}
	}

	/**
	 * Immutable<br>
	 * selfColumns が外部から変更される可能性がある場合、 Immutable が守れなくなるので注意すること。
	 */
	private static class JointContainer {

		private final JoinType type;

		private final Column[] selfColumns;

		private final Joint another;

		private JointContainer(JoinType type, Column[] selfColumns, Joint another) {
			this.type = type;
			this.selfColumns = selfColumns;
			this.another = another;
		}

		private void addUsingRelationships(Collection<Relationship> collection) {
			another.getFromClause().addUsingRelationshipsTo(collection);
		}

		private void append(List<String> list) {
			list.add(processPart(type, another.base, selfColumns, another.columns));
			list.addAll(another.getFromClause().process());
		}
	}
}
