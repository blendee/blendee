package org.blendee.util;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;
import java.util.Optional;

import org.blendee.internal.U;
import org.blendee.jdbc.BlendeeManager;
import org.blendee.jdbc.ContextManager;
import org.blendee.jdbc.Metadata;
import org.blendee.jdbc.MetadataFactory;

/**
 * {@link VirtualSpace} を XML ファイルからロードするファクトリクラスです。
 * @author 千葉 哲嗣
 */
public class FileMetadataFactory implements MetadataFactory {

	/**
	 * デフォルトの XML ファイルの場所
	 */
	public static final String XML_LOCATION = "/blendee-metadata.xml";

	private URL xml;

	private final VirtualSpace virtualSpace;

	/**
	 * このクラスのインスタンスを生成します。
	 * @throws IOException XML の読み込みに失敗した場合
	 * @throws ClassNotFoundException XML 内で定義されている項目の型のクラスが存在しない場合
	 */
	public FileMetadataFactory() throws IOException, ClassNotFoundException {
		BlendeeManager manager = ContextManager.get(BlendeeManager.class);

		Optional<String> fileOfOption = manager.getConfigure().getOption(BlendeeConstants.METADATA_XML_FILE);

		xml = fileOfOption.map(name -> {
			try {
				return new File(name).toURI().toURL();
			} catch (IOException e) {
				throw new UncheckedIOException(e);
			}
		}).orElseGet(() -> FileMetadataFactory.class.getResource(XML_LOCATION));

		virtualSpace = FileVirtualSpaceFactory.getInstance(xml);
	}

	@Override
	public Metadata[] createMetadatas(Metadata depends) {
		if (!virtualSpace.isStarted()) {
			virtualSpace.start(depends);
		}
		return new Metadata[] { virtualSpace };
	}

	@Override
	public String toString() {
		return U.toString(this);
	}
}
