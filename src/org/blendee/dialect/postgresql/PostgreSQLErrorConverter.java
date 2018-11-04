package org.blendee.dialect.postgresql;

import java.sql.SQLException;

import org.blendee.jdbc.BlendeeException;
import org.blendee.jdbc.ErrorConverter;
import org.blendee.jdbc.exception.CheckConstraintViolationException;
import org.blendee.jdbc.exception.ForeignKeyConstraintViolationException;
import org.blendee.jdbc.exception.NotNullConstraintViolationException;
import org.blendee.jdbc.exception.UniqueConstraintViolationException;

/**
 * PostgreSQL 用 {@link ErrorConverter} 実装です。
 * @author 千葉 哲嗣
 */
public class PostgreSQLErrorConverter implements ErrorConverter {

	@Override
	public BlendeeException convert(SQLException e) {
		String state = e.getSQLState();

		switch (state) {
		case "23502":
			return new NotNullConstraintViolationException(e.getMessage());
		case "23503":
			return new ForeignKeyConstraintViolationException(e.getMessage());
		case "23505":
			return new UniqueConstraintViolationException(e.getMessage());
		case "23514":
			return new CheckConstraintViolationException(e.getMessage());
		}

		return new BlendeeException(e);
	}
}
