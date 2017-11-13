package org.blendee.jdbc;

class LazyTransaction extends BTransaction {

	private final TransactionFactory factory;

	private BTransaction transaction;

	private Configure config;

	LazyTransaction(TransactionFactory factory) {
		this.factory = factory;
	}

	@Override
	public void commit() {
		if (transaction == null) return;
		prepareTransaction().commit();
	}

	@Override
	public void rollback() {
		if (transaction == null) return;
		prepareTransaction().rollback();
	}

	@Override
	public void close() {
		if (transaction == null) return;
		prepareTransaction().close();
	}

	@Override
	public BConnection getConnection() {
		return prepareTransaction().getConnection();
	}

	@Override
	protected BConnection getConnectionInternal() {
		throw new UnsupportedOperationException();
	}

	@Override
	protected void commitInternal() {
		throw new UnsupportedOperationException();
	}

	@Override
	protected void rollbackInternal() {
		throw new UnsupportedOperationException();
	}

	@Override
	protected void closeInternal() {
		throw new UnsupportedOperationException();
	}

	@Override
	void prepareConnection(Configure config) {
		this.config = config;
	}

	private BTransaction prepareTransaction() {
		if (transaction == null) {
			transaction = factory.createTransaction();
			transaction.prepareConnection(config);
		}

		return transaction;
	}
}
