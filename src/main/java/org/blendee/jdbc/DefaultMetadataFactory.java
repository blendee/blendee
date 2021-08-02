package org.blendee.jdbc;

import org.blendee.jdbc.impl.JDBCMetadata;

/**
 * @author 千葉 哲嗣
 */
public class DefaultMetadataFactory implements MetadataFactory {

	@Override
	public Metadata createMetadata() {
		return new JDBCMetadata();
	}
}
