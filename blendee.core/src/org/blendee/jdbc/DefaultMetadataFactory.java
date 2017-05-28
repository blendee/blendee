package org.blendee.jdbc;

import org.blendee.internal.U;

/**
 * 独自の {@link MetadataFactory} を設定しなかった場合に使用されるデフォルトの {@link MetadataFactory} です。
 *
 * @author 千葉 哲嗣
 */
class DefaultMetadataFactory implements MetadataFactory {

	/**
	 * @return 空の {@link Metadata} 配列
	 */
	@Override
	public Metadata[] createMetadatas(Metadata depends) {
		return Metadata.EMPTY_ARRAY;
	}

	@Override
	public String toString() {
		return U.toString(this);
	}
}
