package org.blendee.sql;

import java.util.Objects;

/**
 * 'SELECT COUNT(*)' となる SELECT 句です。
 * @author 千葉 哲嗣
 */
public class SelectCountClause extends SelectClause {

	private static final String clauseBase = "SELECT COUNT(*) ";

	private final String clause;

	/**
	 * 別名を指定可能なコンストラクタです。
	 * @param alias 別名
	 */
	public SelectCountClause(String alias) {
		super(RuntimeIdFactory.getInstance());
		Objects.requireNonNull(alias);
		clause = clauseBase + "AS " + alias.trim() + " ";
	}

	/**
	 * 別名無しのコンストラクタです。
	 */
	public SelectCountClause() {
		super(RuntimeIdFactory.getInstance());
		clause = clauseBase;
	}

	@Override
	protected SelectClause createNewInstance(RuntimeId id) {
		return this;
	}

	@Override
	public String toString(boolean joining) {
		return clause;
	}

	@Override
	public SelectClause replicate() {
		return this;
	}

	@Override
	protected void addBlock(ListQueryBlock block) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean equals(Object another) {
		return this == another;
	}

	@Override
	public int hashCode() {
		return System.identityHashCode(this);
	}

	@Override
	boolean isValid() {
		return true;
	}
}
