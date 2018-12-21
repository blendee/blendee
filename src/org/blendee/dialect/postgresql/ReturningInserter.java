package org.blendee.dialect.postgresql;

import org.blendee.assist.Row;
import org.blendee.jdbc.BlendeeManager;
import org.blendee.jdbc.ColumnMetadata;
import org.blendee.jdbc.ContextManager;
import org.blendee.jdbc.DataTypeConverter;
import org.blendee.jdbc.Metadata;
import org.blendee.jdbc.PrimaryKeyMetadata;
import org.blendee.jdbc.TablePath;
import org.blendee.orm.DataObject;
import org.blendee.sql.Updatable;
import org.blendee.sql.ValueExtractor;
import org.blendee.sql.ValueExtractors;
import org.blendee.sql.ValueExtractorsConfigure;

/**
 * PK が単一項目で、シーケンス (serial) を使用しているテーブル用のユーティリティクラスです。
 * @param <T> PK の型
 * @author 千葉 哲嗣
 */
public class ReturningInserter<T> {

	private final TablePath path;

	private final String columnName;

	private final ValueExtractor extractor;

	/**
	 * @param path 対象テーブル
	 */
	public ReturningInserter(TablePath path) {
		this.path = path;

		Metadata metadata = BlendeeManager.get().getMetadata();

		PrimaryKeyMetadata pkMetadata = metadata.getPrimaryKeyMetadata(path);

		String[] columnNames = pkMetadata.getColumnNames();
		if (columnNames.length != 1) throw new IllegalStateException("PK の項目が複数あります table=[" + path + "]");

		columnName = columnNames[0];

		DataTypeConverter converter = ContextManager.get(BlendeeManager.class).getConfigure().getDataTypeConverter();
		ValueExtractors extractors = ContextManager.get(ValueExtractorsConfigure.class).getValueExtractors();

		for (ColumnMetadata columnMetadata : metadata.getColumnMetadatas(path)) {
			if (columnName.equals(columnMetadata.getName())) {
				extractor = extractors.selectValueExtractor(
					converter.convert(columnMetadata.getType(), columnMetadata.getTypeName()));
				break;
			}
		}

		throw new IllegalStateException();
	}

	/**
	 * @param data INSERT 対象
	 * @return PK
	 */
	public T insertAndGetSequencePK(DataObject data) {
		TablePath myPath = data.getPrimaryKey().getTablePath();
		if (!myPath.equals(path))
			throw new IllegalStateException("data のテーブルが不正です table=[" + myPath + "]");

		return insertAndGetSequencePKInternal(data);
	}

	/**
	 * @param row INSERT 対象
	 * @return PK
	 */
	public T insertAndGetSequencePK(Row row) {
		TablePath myPath = row.tablePath();
		if (!myPath.equals(path))
			throw new IllegalStateException("row のテーブルが不正です table=[" + myPath + "]");

		return insertAndGetSequencePKInternal(row);
	}

	@SuppressWarnings("unchecked")
	private T insertAndGetSequencePKInternal(Updatable data) {
		Object[] pk = new Object[] { null };
		ReturningUtilities.insert(path, data, (result) -> {
			if (pk[0] != null) throw new IllegalStateException("PK を指定した検索で、結果が複数あります PK=[" + pk[0] + "]");
			pk[0] = extractor.extract(result, 1);
		}, columnName);

		return (T) pk[0];
	}
}
