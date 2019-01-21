package org.blendee.util;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

import org.blendee.assist.Row;
import org.blendee.assist.TableFacade;
import org.blendee.assist.TableFacadePackageRule;
import org.blendee.assist.annotation.Column;
import org.blendee.assist.annotation.ForeignKey;
import org.blendee.assist.annotation.PrimaryKey;
import org.blendee.assist.annotation.Table;
import org.blendee.internal.U;
import org.blendee.jdbc.BlendeeManager;
import org.blendee.jdbc.ColumnMetadata;
import org.blendee.jdbc.Configure;
import org.blendee.jdbc.ContextManager;
import org.blendee.jdbc.CrossReference;
import org.blendee.jdbc.Metadata;
import org.blendee.jdbc.MetadataFactory;
import org.blendee.jdbc.PrimaryKeyMetadata;
import org.blendee.jdbc.StoredIdentifier;
import org.blendee.jdbc.TableMetadata;
import org.blendee.jdbc.TablePath;

/**
 * {@link VirtualSpace} を {@link Row} に付与されたアノテーションからロードするファクトリクラスです。
 * @author 千葉 哲嗣
 */
public class AnnotationMetadataFactory implements MetadataFactory {

	private static final String DEFAULT_ROOT_PACKAGE = "org.blendee.db";

	private final VirtualSpace virtualSpace = new VirtualSpace();

	/**
	 * このクラスのインスタンスを生成します。
	 */
	public AnnotationMetadataFactory() {
		prepareVirtualSpace();
	}

	@Override
	public Metadata createMetadata() {
		if (!virtualSpace.isStarted()) {
			virtualSpace.start(getDepends());
		}

		return virtualSpace;
	}

	/**
	 * 内部で保持するキャッシュを再読み込みします。
	 */
	public void refresh() {
		synchronized (virtualSpace) {
			virtualSpace.stop();
			prepareVirtualSpace();
		}
	}

	@Override
	public String toString() {
		return U.toString(this);
	}

	/**
	 * アノテーションの調査をするクラスをロードするためのクラスローダーを返します。
	 * @return {@link ClassLoader}
	 */
	protected ClassLoader getClassLoader() {
		return AnnotationMetadataFactory.class.getClassLoader();
	}

	/**
	 * アノテーションの調査をするクラスの対象かどうかを返します。<br>
	 * オーバーライドすることで条件を変えることができます。
	 * @param clazz 調査対象のクラス
	 * @return アノテーションの調査をするクラスの対象かどうか
	 */
	protected boolean matches(Class<?> clazz) {
		return TableFacade.class.isAssignableFrom(clazz) && !clazz.isInterface();
	}

	/**
	 * @return {@link Metadata}
	 */
	protected Metadata getDepends() {
		return new Metadata() {

			@Override
			public TablePath[] getTables(String schemaName) {
				throw new UnsupportedOperationException();
			}

			@Override
			public TableMetadata getTableMetadata(TablePath path) {
				throw new UnsupportedOperationException();
			}

			@Override
			public ColumnMetadata[] getColumnMetadatas(TablePath path) {
				throw new UnsupportedOperationException();
			}

			@Override
			public PrimaryKeyMetadata getPrimaryKeyMetadata(TablePath path) {
				throw new UnsupportedOperationException();
			}

			@Override
			public TablePath[] getResourcesOfImportedKey(TablePath path) {
				throw new UnsupportedOperationException();
			}

			@Override
			public TablePath[] getResourcesOfExportedKey(TablePath path) {
				throw new UnsupportedOperationException();
			}

			@Override
			public CrossReference[] getCrossReferences(TablePath exported, TablePath imported) {
				throw new UnsupportedOperationException();
			}

			@Override
			public StoredIdentifier getStoredIdentifier() {
				throw new UnsupportedOperationException();
			}
		};
	}

	/**
	 * アノテーションからカラムを作成するかを決定します。<br>
	 * TableFacade 作成時に DB から最新の情報でカラムを作る場合には、 false にします。
	 * @return falseの場合、アノテーションからカラムを作成しない
	 */
	protected boolean usesAllVirtualColumns() {
		return true;
	}

	private void prepareVirtualSpace() {
		Configure config = ContextManager.get(BlendeeManager.class).getConfigure();
		String rootPackage = config.getOption(BlendeeConstants.TABLE_FACADE_PACKAGE).orElse(DEFAULT_ROOT_PACKAGE);

		DatabaseInfo info = new DatabaseInfo(rootPackage, getClassLoader());
		try {
			Properties properties = info.read();

			if (info.hasStoredIdentifier(properties))
				virtualSpace.setStoredIdentifier(info.getStoredIdentifier(properties));
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}

		Arrays.stream(config.getSchemaNames())
			.map(s -> rootPackage + "." + TableFacadePackageRule.care(s))
			.forEach(
				name -> listClasses(getClassLoader(), name)
					.stream()
					.map(this::convert)
					.forEach(virtualSpace::addTable));
	}

	private TableSource convert(Class<?> clazz) {
		Table table = clazz.getAnnotation(Table.class);

		PrimaryKey pk = clazz.getAnnotation(PrimaryKey.class);

		List<ForeignKeySource> fkSources = new LinkedList<>();
		List<ColumnMetadata> columnMetadatas = new LinkedList<>();

		Arrays.stream(clazz.getDeclaredFields()).forEach(f -> {
			ForeignKey fk = f.getAnnotation(ForeignKey.class);
			if (fk != null) fkSources.add(createSource(fk));

			if (usesAllVirtualColumns()) {
				Column column = f.getAnnotation(Column.class);

				if (column != null) {
					ColumnMetadata columnMetadata = new VirtualColumnMetadata(
						table.schema(),
						table.name(),
						column.name(),
						column.type(),
						column.typeName(),
						column.size(),
						column.hasDecimalDigits(),
						column.decimalDigits(),
						column.remarks(),
						column.defaultValue(),
						column.ordinalPosition(),
						column.notNull());

					columnMetadatas.add(columnMetadata);
				}
			}
		});

		//手動でクラスに直接つけられた疑似FKを取り込み
		Arrays.stream(clazz.getAnnotationsByType(ForeignKey.class)).forEach(fk -> {
			if (fk != null) fkSources.add(createSource(fk));
		});

		VirtualTableMetadata tableMetadata = new VirtualTableMetadata(
			table.schema(),
			table.name(),
			table.type(),
			table.remarks());

		return new TableSource(
			new TablePath(table.schema(), table.name()),
			tableMetadata,
			columnMetadatas.toArray(new ColumnMetadata[columnMetadatas.size()]),
			pk == null ? new PrimaryKeySource(null, new String[] {}, false) : createSource(pk),
			fkSources.toArray(new ForeignKeySource[fkSources.size()]));
	}

	private static PrimaryKeySource createSource(PrimaryKey annotation) {
		return new PrimaryKeySource(annotation.name(), annotation.columns(), true);
	}

	private static ForeignKeySource createSource(ForeignKey annotation) {
		return new ForeignKeySource(
			annotation.name(),
			annotation.columns(),
			annotation.refColumns(),
			TablePath.parse(annotation.references()));
	}

	@SuppressWarnings("unchecked")
	private List<Class<?>> listClasses(ClassLoader loader, String packageName) {
		String path = packageName.replace('.', '/');

		URL url = loader.getResource(path);

		if (url == null) return Collections.EMPTY_LIST;

		if ("file".equals(url.getProtocol())) return forFile(packageName, loader, url);

		if ("jar".equals(url.getProtocol())) return forJar(path, loader, url);

		throw new Error();
	}

	private List<Class<?>> forFile(String packageName, ClassLoader loader, URL url) {
		File[] files = new File(url.getFile()).listFiles((dir, name) -> filterFile(name));

		return Arrays.stream(files)
			.map(file -> packageName + "." + file.getName().replaceAll(".class$", ""))
			.map(name -> U.call(() -> loader.loadClass(name)))
			.filter(this::matches)
			.collect(Collectors.toList());
	}

	private List<Class<?>> forJar(String path, ClassLoader loader, URL url) {
		try (JarFile jarFile = ((JarURLConnection) url.openConnection()).getJarFile()) {
			return Collections.list(jarFile.entries())
				.stream()
				.map(entry -> entry.getName())
				.filter(name -> name.startsWith(path) && filterFile(name.substring(name.lastIndexOf('/'))))
				.map(name -> name.replace('/', '.').replaceAll(".class$", ""))
				.map(name -> U.call(() -> loader.loadClass(name.replace('/', '.').replaceAll(".class$", ""))))
				.filter(this::matches)
				.collect(Collectors.toList());
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	private static boolean filterFile(String name) {
		return name.indexOf('$') == -1 && name.endsWith(".class");
	}
}
