package org.blendee.jdbc;

/**
 * メタデータを作成するファクトリを定義したインターフェイスです。
 * @author 千葉 哲嗣
 * @see Initializer#setMetadataFactoryClass(Class)
 */
@FunctionalInterface
public interface MetadataFactory {

	/**
	 * メタデータのインスタンスを作成します。<br>
	 * メタデータを複数返す場合、各メタデータは重ね合わせて使用されます。
	 * @param depends 生成するメタデータがベースとして参照する
	 * @return 生成されたメタデータ
	 */
	Metadata[] createMetadatas(Metadata depends);
}
