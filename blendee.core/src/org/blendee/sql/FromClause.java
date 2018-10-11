package org.blendee.sql;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.blendee.jdbc.BPreparedStatement;
import org.blendee.jdbc.ChainPreparedStatementComplementer;
import org.blendee.jdbc.CrossReference;
import org.blendee.jdbc.TablePath;

/**
 * SELECT 文の FROM 句を表すクラスです。
 * @author 千葉 哲嗣
 * @see SQLQueryBuilder#SQLQueryBuilder(FromClause)
 */
public class FromClause implements ChainPreparedStatementComplementer {

	/**
	 * テーブルの結合方式を表す列挙型です。
	 * @author 千葉 哲嗣
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

	private final RuntimeId id;

	private final Set<RelationshipContainer> localRelationships = new LinkedHashSet<>();

	private final List<JointContainer> joints = new LinkedList<>();

	private boolean forSubquery;

	/**
	 * パラメータのテーブルをルートとする FROM 句を生成します。
	 * @param path テーブルのルート
	 * @param id
	 */
	public FromClause(TablePath path, RuntimeId id) {
		this(RelationshipFactory.getInstance().getInstance(path), id);
	}

	/**
	 * パラメータのテーブルをルートとする FROM 句を生成します。
	 * @param root テーブルのルート
	 * @param id
	 */
	public FromClause(Relationship root, RuntimeId id) {
		this.root = root;
		this.id = id;
		localRelationships.add(new RelationshipContainer(root));
	}

	@SuppressWarnings("javadoc")
	public RuntimeId getQueryId() {
		return id;
	}

	/**
	 * この FROM 句のテーブルをルートにした {@link Relationship} と結合します。
	 * @param type 結合方式
	 * @param relationship 同一ルート {@link Relationship}
	 * @throws IllegalStateException 結合できないテーブルを使用している場合
	 */
	@SuppressWarnings("unlikely-arg-type")
	public void join(JoinType type, Relationship relationship) {
		if (localRelationships.contains(relationship)) return;
		if (!root.equals(relationship.getRoot())) {
			if (forSubquery) return;

			throw new IllegalStateException("同一ルートではないので、結合できません");
		}

		Set<Relationship> set = new HashSet<>();
		relationship.addParentTo(set);
		set.add(relationship);
		for (Relationship element : set) {
			localRelationships.add(new RelationshipContainer(type, element));
		}
	}

	/**
	 * この FROM 句に、他のテーブルを結合します。
	 * @param type 結合方式
	 * @param another 結合するテーブル
	 * @param onCriteria この FROM 句のテーブルと another の ON に使用する条件句
	 */
	public void join(JoinType type, FromClause another, Criteria onCriteria) {
		joints.add(
			new JointContainer(type, another, onCriteria));

		onCriteria.getColumnsInternal().forEach(c -> {
			Relationship targetRoot = c.getRootRelationship();
			c.setRelationship(target -> {
				if (targetRoot.equals(root))
					localRelationships.add(new RelationshipContainer(JoinType.LEFT_OUTER_JOIN, target));
			});
		});
	}

	@Override
	public int complement(int done, BPreparedStatement statement) {
		int[] result = { done };
		joints.forEach(j -> {
			result[0] = j.complement(result[0], statement);
		});

		return result[0];
	}

	/**
	 * 条件内にもつ {@link Binder} を返します。
	 * @return {@link Binder} の配列
	 */
	public Binder[] getBinders() {
		List<Binder> binders = new LinkedList<>();
		joints.forEach(j -> j.addBindersTo(binders));
		return binders.toArray(new Binder[binders.size()]);
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
		String string;
		if (!isJoined()) {
			string = " FROM " + root.getTablePath();
		} else {
			LinkedList<String> result = process();
			result.addFirst(id.toString(root));
			string = " FROM " + String.join(" ", result);
		}

		return string;
	}

	/**
	 * このインスタンスのコピーを返します。
	 * @return copy
	 */
	protected FromClause replicate() {
		FromClause clone = new FromClause(root.getTablePath(), id);

		//中身はImmutableなのでそのままコピー
		clone.localRelationships.addAll(localRelationships);
		clone.joints.addAll(joints);

		return clone;
	}

	void clearRelationships() {
		localRelationships.clear();
		localRelationships.add(new RelationshipContainer(root));
	}

	void forSubquery(boolean forSubquery) {
		clearRelationships();
		this.forSubquery = forSubquery;
	}

	boolean forSubquery() {
		return forSubquery;
	}

	boolean isJoined() {
		return localRelationships.size() > 1 || joints.size() > 0 || forSubquery;
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

	private String processPart(
		JoinType type,
		Relationship relationship,
		Column[] left,
		Column[] right) {
		Criteria criteria = new CriteriaFactory(id).create();
		CriteriaFactory factory = new CriteriaFactory(id);
		for (int i = 0; i < left.length; i++) {
			criteria.and(factory.createCriteria("{0} = {1}", new Column[] { left[i], right[i] }, Bindable.EMPTY_ARRAY));
		}

		return type
			+ " "
			+ id.toString(relationship)
			+ " ON ("
			+ criteria.toString(true).trim()
			+ ")";
	}

	private String processPart(
		RuntimeId anotherId,
		JoinType type,
		Relationship relationship,
		Criteria onCriteria) {
		return type
			+ " "
			+ anotherId.toString(relationship)
			+ " ON ("
			+ onCriteria.toString(true).trim()
			+ ")";
	}

	/**
	 * Immutable
	 */
	private class RelationshipContainer implements Comparable<RelationshipContainer> {

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
	 * Immutable
	 */
	private class JointContainer {

		private final JoinType type;

		private final FromClause another;

		private final Criteria onCriteria;

		private JointContainer(JoinType type, FromClause another, Criteria onCriteria) {
			this.type = type;
			this.another = another;
			this.onCriteria = onCriteria;
		}

		private void append(List<String> list) {
			list.add(processPart(another.getQueryId(), type, another.root, onCriteria));
			list.addAll(another.process());
		}

		private int complement(int done, BPreparedStatement statement) {
			done = another.complement(done, statement);
			if (onCriteria == null) return done;
			return onCriteria.complement(done, statement);
		}

		private void addBindersTo(List<Binder> binders) {
			if (onCriteria == null) return;
			Arrays.stream(onCriteria.getBinders()).forEach(b -> binders.add(b));
		}
	}
}
