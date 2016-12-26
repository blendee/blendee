package jp.ats.blendee.util;

import java.io.File;

import jp.ats.blendee.internal.FileIOStream;
import jp.ats.blendee.internal.HomeStorage;
import jp.ats.blendee.internal.U;
import jp.ats.blendee.jdbc.BlendeeContext;
import jp.ats.blendee.jdbc.BlendeeManager;
import jp.ats.blendee.selector.ColumnRepository;
import jp.ats.blendee.selector.ColumnRepositoryFactory;
import jp.ats.blendee.selector.StreamColumnRepository;

/**
 * ファイルをリポジトリのソースとして使用するシンプルなリポジトリファクトリクラスです。
 *
 * @author 千葉 哲嗣
 */
public class FileColumnRepositoryFactory implements ColumnRepositoryFactory {

	/**
	 * デフォルトリポジトリファイル名
	 */
	public static final String DEFAULT_COLUMN_REPOSITORY_FILE = "blendee-column-repository";

	/**
	 * {@link HomeStorage} に格納するリポジトリファイル用エントリのキー
	 */
	public static final String COLUMN_REPOSITORY_FILE = "column-repository-file";

	private final File repositoryFile;

	/**
	 * このクラスのコンストラクタです。
	 */
	public FileColumnRepositoryFactory() {
		BlendeeManager manager = BlendeeContext.get(BlendeeManager.class);

		repositoryFile = manager
			.getConfigure()
			.getOption(BlendeeConstants.COLUMN_REPOSITORY_FILE)
			.map(file -> new File(file))
			.orElseGet(() -> manager
				.getConfigure()
				.getOption(BlendeeConstants.CAN_ADD_NEW_ENTRIES)
				.filter(flag -> flag)
				.map(flag -> {
					String repositoryFileString = HomeStorage.loadProperties().getProperty(COLUMN_REPOSITORY_FILE);

					File repositoryFile = new File(repositoryFileString);
					if (!repositoryFile.exists()) throw new IllegalStateException(
						HomeStorage.getPropertiesFile().getAbsolutePath()
							+ " 内に記述されている "
							+ repositoryFileString
							+ " は存在しません。");

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
