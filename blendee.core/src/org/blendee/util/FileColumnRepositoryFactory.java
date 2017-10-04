package org.blendee.util;

import java.io.File;

import org.blendee.internal.FileIOStream;
import org.blendee.internal.HomeStorage;
import org.blendee.internal.U;
import org.blendee.jdbc.ContextManager;
import org.blendee.jdbc.BlendeeManager;
import org.blendee.jdbc.Configure;
import org.blendee.selector.ColumnRepository;
import org.blendee.selector.ColumnRepositoryFactory;
import org.blendee.selector.StreamColumnRepository;

/**
 * ファイルをリポジトリのソースとして使用するシンプルなリポジトリファクトリクラスです。
 * @author 千葉 哲嗣
 */
public class FileColumnRepositoryFactory implements ColumnRepositoryFactory {

	/**
	 * デフォルトリポジトリファイル名
	 */
	public static final String DEFAULT_COLUMN_REPOSITORY_FILE = "org.blendee.repository";

	/**
	 * {@link HomeStorage} に格納するリポジトリファイル用エントリのキー
	 */
	public static final String COLUMN_REPOSITORY_FILE_KEY = "column-repository-file";

	private final File repositoryFile;

	/**
	 * このクラスのコンストラクタです。
	 */
	public FileColumnRepositoryFactory() {
		BlendeeManager manager = ContextManager.get(BlendeeManager.class);

		Configure config = manager.getConfigure();

		repositoryFile = config
			.getOption(BlendeeConstants.COLUMN_REPOSITORY_FILE)
			.map(file -> new File(file))
			.orElseGet(
				() -> config
					.getOption(BlendeeConstants.CAN_ADD_NEW_ENTRIES)
					.filter(flag -> flag)
					.map(flag -> {
						HomeStorage storage = config.getOption(BlendeeConstants.HOME_STORAGE_IDENTIFIER)
							.map(i -> new HomeStorage(i))
							.orElse(new HomeStorage());

						String repositoryFileString = storage.loadProperties().getProperty(COLUMN_REPOSITORY_FILE_KEY);

						if (!U.presents(repositoryFileString)) throw new IllegalStateException(
							storage.getPropertiesFile().toAbsolutePath()
								+ " に、キー "
								+ COLUMN_REPOSITORY_FILE_KEY
								+ " の値が存在しません");

						File repositoryFile = new File(repositoryFileString);
						if (!repositoryFile.exists()) throw new IllegalStateException(
							storage.getPropertiesFile().toAbsolutePath()
								+ " 内に記述されている "
								+ repositoryFileString
								+ " は存在しません");

						return repositoryFile;
					})
					.orElseGet(() -> U.getResourceAsFile("/" + DEFAULT_COLUMN_REPOSITORY_FILE)));

		if (repositoryFile == null) throw new IllegalStateException("リポジトリファイルが存在しません");
	}

	@Override
	public ColumnRepository createColumnRepository() {
		return new StreamColumnRepository(new FileIOStream(repositoryFile));
	}

	@Override
	public String toString() {
		return U.toString(this);
	}
}
