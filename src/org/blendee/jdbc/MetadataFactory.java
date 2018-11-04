package org.blendee.jdbc;

/**
 * メタデータを作成するファクトリを定義したインターフェイスです。
 * @author 千葉 哲嗣
 * @see Initializer#setMetadataFactoryClass(Class)
 */
@FunctionalInterface
public interface MetadataFactory {

	/**
	 * メタデータのインスタンスを作成します。
	 * @return 生成されたメタデータ
	 */
	Metadata createMetadata();
}
