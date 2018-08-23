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
import java.util.jar.JarFile;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.blendee.internal.U;
import org.blendee.jdbc.BlendeeManager;
import org.blendee.jdbc.ColumnMetadata;
import org.blendee.jdbc.Configure;
import org.blendee.jdbc.ContextManager;
import org.blendee.jdbc.Metadata;
import org.blendee.jdbc.MetadataFactory;
import org.blendee.jdbc.TablePath;
import org.blendee.support.Row;
import org.blendee.support.TableFacade;
import org.blendee.support.TableFacadePackageRule;
import org.blendee.support.annotation.ForeignKey;
import org.blendee.support.annotation.PrimaryKey;
import org.blendee.support.annotation.Table;

/**
 * {@link VirtualSpace} を {@link Row} に付与されたアノテーションからロードするファクトリクラスです。
 * @author 千葉 哲嗣
 */
public class AnnotationMetadataFactory implements MetadataFactory {

	private static final ColumnMetadata[] emptyColumns = new ColumnMetadata[] {};

	private final VirtualSpace virtualSpace = new VirtualSpace();

	/**
	 * このクラスのインスタンスを生成します。
	 */
	public AnnotationMetadataFactory() {
		prepareVirtualSpace();
	}

	@Override
	public Metadata[] createMetadatas(Metadata depends) {
		if (!virtualSpace.isStarted()) {
			virtualSpace.start(depends);
		}

		return new Metadata[] { virtualSpace };
	}

	/**
	 * 内部で保持するキャッシュを再読み込みします。
	 */
	public void refresh() {
		virtualSpace.stop();
		prepareVirtualSpace();
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

	private void prepareVirtualSpace() {
		Configure config = ContextManager.get(BlendeeManager.class).getConfigure();
		String[] rootPackages = config.getOption(BlendeeConstants.ANNOTATED_ROW_PACKAGES).orElseThrow(
			() -> new NullPointerException());

		Stream<String> packages = Arrays.stream(rootPackages)
			.flatMap(root -> Arrays.stream(config.getSchemaNames()).map(s -> root + "." + TableFacadePackageRule.care(s)));

		packages.forEach(
			name -> listClasses(getClassLoader(), name)
				.stream()
				.map(AnnotationMetadataFactory::convert)
				.forEach(table -> virtualSpace.addTable(table)));
	}

	private static TableSource convert(Class<?> clazz) {
		Table table = clazz.getAnnotation(Table.class);

		PrimaryKey pk = clazz.getAnnotation(PrimaryKey.class);

		List<ForeignKeySource> fkSources = new LinkedList<>();
		Arrays.stream(clazz.getDeclaredFields()).forEach(f -> {
			ForeignKey fk = f.getAnnotation(ForeignKey.class);
			if (fk != null && fk.pseudo()) fkSources.add(createSource(fk));
		});

		//手動でクラスに直接つけられた疑似FKを取り込み
		Arrays.stream(clazz.getAnnotationsByType(ForeignKey.class)).forEach(fk -> {
			if (fk != null && fk.pseudo()) fkSources.add(createSource(fk));
		});

		return new TableSource(
			new TablePath(table.schema(), table.name()),
			null,
			emptyColumns,
			pk == null || !pk.pseudo() ? null : createSource(pk),
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
