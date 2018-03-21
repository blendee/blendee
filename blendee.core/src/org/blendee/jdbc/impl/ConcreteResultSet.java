package org.blendee.jdbc.impl;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.function.Consumer;

import org.blendee.internal.U;
import org.blendee.jdbc.AutoCloseableFinalizer;
import org.blendee.jdbc.BlenResultSet;
import org.blendee.jdbc.Configure;

/**
 * Blendee が使用する {@link BlenResultSet} の標準実装クラスです。
 * @author 千葉 哲嗣
 */
public class ConcreteResultSet implements BlenResultSet {

	private final Configure config;

	private final ResultSet base;

	private final ResultSetMetaData metadata;

	//statementをここで参照しておかないと、statementが先に解放され自動クローズがかかった場合
	//ResultSetをnext()中でもResultSetがクローズされてしまう
	private ConcretePreparedStatement statement;

	@SuppressWarnings("javadoc")
	public ConcreteResultSet(
		Configure config,
		ResultSet base,
		ConcretePreparedStatement statement,
		AutoCloseableFinalizer finalizer) {
		this.config = config;
		this.base = base;
		this.statement = statement;

		try {
			this.metadata = base.getMetaData();
		} catch (SQLException e) {
			throw config.getErrorConverter().convert(e);
		}

		if (finalizer != null)
			finalizer.regist(this, base);
	}

	@Override
	public boolean next() {
		try {
			boolean result = base.next();
			if (!result) close();
			return result;
		} catch (SQLException e) {
			throw config.getErrorConverter().convert(e);
		}
	}

	@Override
	public boolean getBoolean(String columnName) {
		try {
			return base.getBoolean(columnName);
		} catch (SQLException e) {
			throw config.getErrorConverter().convert(e);
		}
	}

	@Override
	public double getDouble(String columnName) {
		try {
			return base.getDouble(columnName);
		} catch (SQLException e) {
			throw config.getErrorConverter().convert(e);
		}
	}

	@Override
	public float getFloat(String columnName) {
		try {
			return base.getFloat(columnName);
		} catch (SQLException e) {
			throw config.getErrorConverter().convert(e);
		}
	}

	@Override
	public int getInt(String columnName) {
		try {
			return base.getInt(columnName);
		} catch (SQLException e) {
			throw config.getErrorConverter().convert(e);
		}
	}

	@Override
	public long getLong(String columnName) {
		try {
			return base.getLong(columnName);
		} catch (SQLException e) {
			throw config.getErrorConverter().convert(e);
		}
	}

	@Override
	public String getString(String columnName) {
		try {
			return base.getString(columnName);
		} catch (SQLException e) {
			throw config.getErrorConverter().convert(e);
		}
	}

	@Override
	public Timestamp getTimestamp(String columnName) {
		try {
			return base.getTimestamp(columnName);
		} catch (SQLException e) {
			throw config.getErrorConverter().convert(e);
		}
	}

	@Override
	public BigDecimal getBigDecimal(String columnName) {
		try {
			return base.getBigDecimal(columnName);
		} catch (SQLException e) {
			throw config.getErrorConverter().convert(e);
		}
	}

	@Override
	public InputStream getBinaryStream(String columnName) {
		try {
			return base.getBinaryStream(columnName);
		} catch (SQLException e) {
			throw config.getErrorConverter().convert(e);
		}
	}

	@Override
	public Reader getCharacterStream(String columnName) {
		try {
			return base.getCharacterStream(columnName);
		} catch (SQLException e) {
			throw config.getErrorConverter().convert(e);
		}
	}

	@Override
	public Object getObject(String columnName) {
		try {
			return base.getObject(columnName);
		} catch (SQLException e) {
			throw config.getErrorConverter().convert(e);
		}
	}

	@Override
	public byte[] getBytes(String columnName) {
		try {
			return base.getBytes(columnName);
		} catch (SQLException e) {
			throw config.getErrorConverter().convert(e);
		}
	}

	@Override
	public Blob getBlob(String columnName) {
		try {
			return base.getBlob(columnName);
		} catch (SQLException e) {
			throw config.getErrorConverter().convert(e);
		}
	}

	@Override
	public Clob getClob(String columnName) {
		try {
			return base.getClob(columnName);
		} catch (SQLException e) {
			throw config.getErrorConverter().convert(e);
		}
	}

	@Override
	public boolean getBoolean(int columnIndex) {
		try {
			return base.getBoolean(columnIndex);
		} catch (SQLException e) {
			throw config.getErrorConverter().convert(e);
		}
	}

	@Override
	public double getDouble(int columnIndex) {
		try {
			return base.getDouble(columnIndex);
		} catch (SQLException e) {
			throw config.getErrorConverter().convert(e);
		}
	}

	@Override
	public float getFloat(int columnIndex) {
		try {
			return base.getFloat(columnIndex);
		} catch (SQLException e) {
			throw config.getErrorConverter().convert(e);
		}
	}

	@Override
	public int getInt(int columnIndex) {
		try {
			return base.getInt(columnIndex);
		} catch (SQLException e) {
			throw config.getErrorConverter().convert(e);
		}
	}

	@Override
	public long getLong(int columnIndex) {
		try {
			return base.getLong(columnIndex);
		} catch (SQLException e) {
			throw config.getErrorConverter().convert(e);
		}
	}

	@Override
	public String getString(int columnIndex) {
		try {
			return base.getString(columnIndex);
		} catch (SQLException e) {
			throw config.getErrorConverter().convert(e);
		}
	}

	@Override
	public Timestamp getTimestamp(int columnIndex) {
		try {
			return base.getTimestamp(columnIndex);
		} catch (SQLException e) {
			throw config.getErrorConverter().convert(e);
		}
	}

	@Override
	public BigDecimal getBigDecimal(int columnIndex) {
		try {
			return base.getBigDecimal(columnIndex);
		} catch (SQLException e) {
			throw config.getErrorConverter().convert(e);
		}
	}

	@Override
	public Object getObject(int columnIndex) {
		try {
			return base.getObject(columnIndex);
		} catch (SQLException e) {
			throw config.getErrorConverter().convert(e);
		}
	}

	@Override
	public InputStream getBinaryStream(int columnIndex) {
		try {
			return base.getBinaryStream(columnIndex);
		} catch (SQLException e) {
			throw config.getErrorConverter().convert(e);
		}
	}

	@Override
	public Reader getCharacterStream(int columnIndex) {
		try {
			return base.getCharacterStream(columnIndex);
		} catch (SQLException e) {
			throw config.getErrorConverter().convert(e);
		}
	}

	@Override
	public byte[] getBytes(int columnIndex) {
		try {
			return base.getBytes(columnIndex);
		} catch (SQLException e) {
			throw config.getErrorConverter().convert(e);
		}
	}

	@Override
	public Blob getBlob(int columnIndex) {
		try {
			return base.getBlob(columnIndex);
		} catch (SQLException e) {
			throw config.getErrorConverter().convert(e);
		}
	}

	@Override
	public Clob getClob(int columnIndex) {
		try {
			return base.getClob(columnIndex);
		} catch (SQLException e) {
			throw config.getErrorConverter().convert(e);
		}
	}

	@Override
	public boolean wasNull() {
		try {
			return base.wasNull();
		} catch (SQLException e) {
			throw config.getErrorConverter().convert(e);
		}
	}

	@Override
	public int getColumnCount() {
		try {
			return metadata.getColumnCount();
		} catch (SQLException e) {
			throw config.getErrorConverter().convert(e);
		}
	}

	@Override
	public String getColumnName(int columnIndex) {
		try {
			return metadata.getColumnName(columnIndex);
		} catch (SQLException e) {
			throw config.getErrorConverter().convert(e);
		}
	}

	@Override
	public int getColumnType(int columnIndex) {
		try {
			return metadata.getColumnType(columnIndex);
		} catch (SQLException e) {
			throw config.getErrorConverter().convert(e);
		}
	}

	@Override
	public String getColumnTypeName(int columnIndex) {
		try {
			return metadata.getColumnTypeName(columnIndex);
		} catch (SQLException e) {
			throw config.getErrorConverter().convert(e);
		}
	}

	@Override
	public void close() {
		U.close(base);
		//ここで statement の参照をなくし、 statement が自動クローズの対象になるようにする
		statement = null;
	}

	/**
	 * このインスタンスを生成した statement を返します。
	 * @return このインスタンスを生成した statement
	 */
	public ConcretePreparedStatement getStatement() {
		//statement は GC をコントロールするために参照している
		//万が一コンパイラの最適化等で参照が消されないようにpublicメソッドで使用しているようにする
		return statement;
	}

	/**
	 * 内部で持っている {@link ResultSet} を直接操作できるようにします。
	 * @param consumer {@link Consumer}
	 */
	public void borrow(Consumer<ResultSet> consumer) {
		consumer.accept(base);
	}

	@Override
	public String toString() {
		return U.toString(this);
	}
}
