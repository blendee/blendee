package org.blendee.jdbc;

class LazyTransaction extends Transaction {

	private final TransactionFactory factory;

	private Transaction transaction;

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
	public BlenConnection getConnection() {
		return prepareTransaction().getConnection();
	}

	@Override
	protected BlenConnection getConnectionInternal() {
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
		if (transaction == null) return;
		transaction.closeInternal();
	}

	@Override
	void prepareConnection(Configure config) {
		this.config = config;
	}

	private Transaction prepareTransaction() {
		if (transaction == null) {
			transaction = factory.createTransaction();
			transaction.prepareConnection(config);
		}

		return transaction;
	}
}
