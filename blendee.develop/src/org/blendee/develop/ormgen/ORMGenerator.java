package org.blendee.develop.ormgen;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UncheckedIOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.blendee.internal.U;
import org.blendee.jdbc.BlendeeContext;
import org.blendee.jdbc.ColumnMetadata;
import org.blendee.jdbc.CrossReference;
import org.blendee.jdbc.Metadata;
import org.blendee.jdbc.PrimaryKeyMetadata;
import org.blendee.jdbc.TablePath;
import org.blendee.jdbc.TableMetadata;
import org.blendee.sql.Column;
import org.blendee.sql.Relationship;
import org.blendee.sql.RelationshipFactory;
import org.blendee.support.Many;
import org.blendee.support.annotation.EntityRelationship;
import org.blendee.support.annotation.FKs;
import org.blendee.support.annotation.PseudoFK;
import org.blendee.support.annotation.PseudoPK;

/**
 * データベースの構成を読み取り、各テーブルの EntityManager クラスと Entity クラスの Java ソースを生成するジェネレータクラスです。
 *
 * @author 千葉 哲嗣
 */
public class ORMGenerator {

	private static final CodeFormatter defaultCodeFormatter = new DefaultCodeFormatter();

	private static final String managerTemplate;

	private static final String entityTemplate;

	private static final String queryTemplate;

	private static final String entityPropertyAccessorPartTemplate;

	private static final String entityRelationshipPartTemplate;

	private static final String queryColumnPart1Template;

	private static final String queryRelationshipPart1Template;

	private static final String queryColumnPart2Template;

	private static final String queryRelationshipPart2Template;

	private static final String queryRelationshipPart3Template;

	private static final Map<Class<?>, Class<?>> primitiveToWrapperMap = new HashMap<>();

	private final Metadata metadata;

	private final String packageName;

	private final String schemaName;

	private final Class<?> managerSuperclass;

	private final Class<?> entitySuperclass;

	private final Class<?> querySuperclass;

	private final CodeFormatter codeFormatter;

	private final boolean useNumberClass;

	private final boolean useNullGuard;

	private final String generatorName;
	static {
		primitiveToWrapperMap.put(boolean.class, Boolean.class);
		primitiveToWrapperMap.put(byte.class, Byte.class);
		primitiveToWrapperMap.put(char.class, Character.class);
		primitiveToWrapperMap.put(short.class, Short.class);
		primitiveToWrapperMap.put(int.class, Integer.class);
		primitiveToWrapperMap.put(long.class, Long.class);
		primitiveToWrapperMap.put(float.class, Float.class);
		primitiveToWrapperMap.put(double.class, Double.class);
		primitiveToWrapperMap.put(void.class, Void.class);
	}

	static {
		String charset = "UTF-8";

		{
			managerTemplate = convertToTemplate(readTemplate(ManagerBase.class, charset));
		}

		{
			String source = readTemplate(RowBase.class, charset);
			{
				String[] result = pickupFromSource(source, "EntityPropertyAccessorPart");
				entityPropertyAccessorPartTemplate = convertToTemplate(result[0]);
				source = result[1];
			}
			{
				String[] result = pickupFromSource(source, "EntityRelationshipPart");
				entityRelationshipPartTemplate = convertToTemplate(result[0]);
				source = result[1];
			}

			entityTemplate = convertToTemplate(source);
		}

		{
			String source = readTemplate(QueryBase.class, charset);
			{
				String[] result = pickupFromSource(source, "ColumnPart1");
				queryColumnPart1Template = convertToTemplate(result[0]);
				source = result[1];
			}
			{
				String[] result = pickupFromSource(source, "RelationshipPart1");
				queryRelationshipPart1Template = convertToTemplate(result[0]);
				source = result[1];
			}

			{
				String[] result = pickupFromSource(source, "ColumnPart2");
				queryColumnPart2Template = convertToTemplate(result[0]);
				source = result[1];
			}
			{
				String[] result = pickupFromSource(source, "RelationshipPart2");
				queryRelationshipPart2Template = convertToTemplate(result[0]);
				source = result[1];
			}
			{
				String[] result = pickupFromSource(source, "RelationshipPart3");
				queryRelationshipPart3Template = convertToTemplate(result[0]);
				source = result[1];
			}

			queryTemplate = convertToTemplate(source);
		}

	}

	/**
	 * インスタンスを生成します。
	 *
	 * @param metadata テーブルを読み込む対象となるデータベースの {@link Metadata}
	 * @param packageName EntityManager クラス、 Entity クラスが属するパッケージ名
	 * @param schemaName テーブルを読み込む対象となるスキーマ
	 * @param entityManagerSuperclass EntityManager クラスの親クラス
	 * @param entitySuperclass Entity クラスの親クラス
	 * @param querySuperclass Query クラスの親クラス
	 * @param codeAdjuster {@link CodeFormatter}
	 * @param useNumberClass Entity クラスの数値型項目を {@link Number} で統一する
	 * @param useNullGuard Entity クラスの項目に null ガードを適用する
	 * @param generatorName ジェネレータ名
	 */
	public ORMGenerator(
		Metadata metadata,
		String packageName,
		String schemaName,
		Class<?> entityManagerSuperclass,
		Class<?> entitySuperclass,
		Class<?> querySuperclass,
		CodeFormatter codeAdjuster,
		boolean useNumberClass,
		boolean useNullGuard,
		String generatorName) {
		this.metadata = Objects.requireNonNull(metadata);
		this.packageName = Objects.requireNonNull(packageName);

		this.schemaName = schemaName;
		this.managerSuperclass = entityManagerSuperclass != null ? entityManagerSuperclass : Object.class;
		this.entitySuperclass = entitySuperclass != null ? entitySuperclass : Object.class;
		this.querySuperclass = querySuperclass != null ? querySuperclass : Object.class;

		this.codeFormatter = codeAdjuster == null ? defaultCodeFormatter : codeAdjuster;

		this.useNumberClass = useNumberClass;
		this.useNullGuard = useNullGuard;
		this.generatorName = generatorName;
	}

	/**
	 * すべてのクラスファイルを生成します。
	 *
	 * @param home 生成された Java ソースを保存するためのルートとなる場所
	 * @param srcCharset 生成する Java ソースの文字コード
	 * @throws IOException ファイル書き込みに失敗した場合
	 */
	public void build(File home, Charset srcCharset) throws IOException {
		File packageDir = new File(home, String.join("/", packageName.split("\\.")));
		packageDir.mkdirs();

		TablePath[] tables = metadata.getTables(schemaName);
		for (TablePath table : tables) {
			Relationship relation = BlendeeContext.get(RelationshipFactory.class).getInstance(table);

			String tableName = relation.getTablePath().getTableName();

			write(
				new File(packageDir, createEntityManagerCompilationUnitName(tableName)),
				buildEntityManager(relation),
				srcCharset);

			write(
				new File(packageDir, createEntityCompilationUnitName(tableName)),
				buildEntity(relation),
				srcCharset);

			write(
				new File(packageDir, createQueryCompilationUnitName(tableName)),
				buildQuery(relation),
				srcCharset);
		}
	}

	/**
	 * このクラスが生成する EntityManager のコンパイル単位名を返します。
	 *
	 * @param tableName 対象となるテーブル名
	 * @return コンパイル単位名
	 */
	public static String createEntityManagerCompilationUnitName(String tableName) {
		return tableName + "Manager.java";
	}

	/**
	 * このクラスが生成する Entity のコンパイル単位名を返します。
	 *
	 * @param tableName 対象となるテーブル名
	 * @return コンパイル単位名
	 */
	public static String createEntityCompilationUnitName(String tableName) {
		return tableName + ".java";
	}

	/**
	 * このクラスが生成する Query のコンパイル単位名を返します。
	 *
	 * @param tableName 対象となるテーブル名
	 * @return コンパイル単位名
	 */
	public static String createQueryCompilationUnitName(String tableName) {
		return tableName + "Query.java";
	}

	/**
	 * EntityManager クラスを作成します。
	 *
	 * @param relation 対象となるテーブルをあらわす {@link Relationship}
	 * @return 生成されたソース
	 */
	public String buildEntityManager(Relationship relation) {
		if (!relation.isRoot()) throw new IllegalArgumentException("relation はルートでなければなりません");

		TablePath target = relation.getTablePath();

		return codeFormatter.formatEntityManager(
			managerTemplate,
			packageName,
			target.getTableName(),
			managerSuperclass.getName(),
			buildTableComment(metadata, target),
			getGenerator());
	}

	/**
	 * Entity クラスを一件作成します。
	 *
	 * @param relation 対象となるテーブルをあらわす {@link Relationship}
	 * @return 生成されたソース
	 */
	public String buildEntity(Relationship relation) {
		if (!relation.isRoot()) throw new IllegalArgumentException("relation はルートでなければなりません");

		TablePath target = relation.getTablePath();
		String tableName = target.getTableName();

		Set<String> importPart = new LinkedHashSet<>();

		String propertyAccessorPart;
		{
			List<String> list = new LinkedList<>();
			for (Column column : relation.getColumns()) {
				Class<?> type = column.getType();

				String classNameString;

				if (type.isArray()) {
					Class<?> componentType = convertForNumber(type.getComponentType());
					classNameString = componentType.getName() + "[]";
				} else {
					classNameString = convertForNumber(convertPrimitiveClassToWrapperClass(column.getType())).getName();
				}

				String nullCheck = "", returnPrefix = "", returnSuffix = "", returnType = classNameString;
				boolean returnOptional = false;
				if (useNullGuard) {
					if (column.getColumnMetadata().isNotNull() || column.isPrimaryKey()) {
						importPart.add(buildImportPart(Objects.class));
						nullCheck = Objects.class.getSimpleName() + ".requireNonNull(value);" + U.LINE_SEPARATOR;
					} else {
						importPart.add(buildImportPart(Optional.class));
						String optional = Optional.class.getSimpleName();
						returnPrefix = optional + ".ofNullable(";
						returnSuffix = ")";
						returnType = optional + "<" + classNameString + ">";
						returnOptional = true;
					}
				}

				list.add(codeFormatter.formatEntityPropertyAccessorPart(
					entityPropertyAccessorPartTemplate,
					toUpperCaseFirstLetter(column.getName()),
					column.getName(),
					classNameString,
					buildColumnComment(column),
					nullCheck,
					returnType,
					returnPrefix,
					returnSuffix,
					Boolean.toString(returnOptional)));
			}

			propertyAccessorPart = String.join("", list);
		}

		String relationshipPart;
		{
			Map<String, Boolean> checker = createDuprecateChecker(relation);

			List<String> list = new LinkedList<>();
			for (Relationship child : relation.getRelationships()) {
				CrossReference crossReference = child.getCrossReference();
				String foreignKey = crossReference.getForeignKeyName();

				String childTableName = child.getTablePath().getTableName();
				String methodName = checker.get(childTableName) ? childTableName + "By" + foreignKey : childTableName;

				list.add(codeFormatter.formatEntityRelationshipPart(
					entityRelationshipPartTemplate,
					child.getTablePath().getTableName(),
					foreignKey,
					String.join(", ", crossReference.getForeignKeyColumnNames()),
					toUpperCaseFirstLetter(methodName),
					tableName));
			}

			if (list.size() > 0) importPart.add(buildImportPart(EntityRelationship.class));

			relationshipPart = String.join("", list);
		}

		return codeFormatter.formatEntity(
			entityTemplate,
			packageName,
			schemaName,
			tableName,
			entitySuperclass.getName(),
			propertyAccessorPart,
			relationshipPart,
			buildTableComment(metadata, target),
			buildAnnotationPart(metadata, relation, importPart),
			String.join(U.LINE_SEPARATOR, importPart),
			getGenerator());
	}

	/**
	 * Query クラスを一件作成します。
	 *
	 * @param relation 対象となるテーブルをあらわす {@link Relationship}
	 * @return 生成されたソース
	 */
	public String buildQuery(Relationship relation) {
		if (!relation.isRoot()) throw new IllegalArgumentException("relation はルートでなければなりません");

		String rootTableName = relation.getTablePath().getTableName();
		String columnPart1, columnPart2;
		{
			List<String> list1 = new LinkedList<>();
			List<String> list2 = new LinkedList<>();
			for (Column column : relation.getColumns()) {
				list1.add(codeFormatter.formatQueryColumnPart1(
					queryColumnPart1Template,
					column.getName()));

				list2.add(codeFormatter.formatQueryColumnPart2(
					queryColumnPart2Template,
					column.getName(),
					rootTableName,
					packageName));
			}

			columnPart1 = String.join("", list1);
			columnPart2 = String.join("", list2);
		}

		String relationshipPart1, relationshipPart2, relationshipPart3;
		{
			Map<String, Boolean> checker = createDuprecateChecker(relation);

			List<String> list1 = new LinkedList<>();
			List<String> list2 = new LinkedList<>();
			List<String> list3 = new LinkedList<>();
			for (Relationship child : relation.getRelationships()) {
				TablePath childTablePath = child.getTablePath();

				String foreignKey = child.getCrossReference().getForeignKeyName();

				String tableName = childTablePath.getTableName();

				String relationship = checker.get(tableName) ? tableName + "_BY_" + foreignKey : tableName;

				String typeParam = Many.class.getSimpleName() + "<" + rootTableName + ", M>";

				list1.add(codeFormatter.formatQueryRelationshipPart1(
					queryRelationshipPart1Template,
					tableName,
					foreignKey,
					relationship,
					typeParam));

				list2.add(codeFormatter.formatQueryRelationshipPart1(
					queryRelationshipPart2Template,
					tableName,
					foreignKey,
					relationship,
					rootTableName,
					typeParam,
					packageName));

				list3.add(codeFormatter.formatQueryRelationshipPart1(
					queryRelationshipPart3Template,
					tableName,
					foreignKey,
					relationship,
					rootTableName,
					typeParam,
					packageName));
			}

			relationshipPart1 = String.join("", list1);
			relationshipPart2 = String.join("", list2);
			relationshipPart3 = String.join("", list3);
		}

		String tableName = relation.getTablePath().getTableName();

		return codeFormatter.formatQuery(
			queryTemplate,
			packageName,
			tableName,
			querySuperclass.getName(),
			columnPart1,
			relationshipPart1,
			columnPart2,
			relationshipPart2,
			relationshipPart3,
			relation.getRelationships().length > 0 ? ("import " + Many.class.getName() + ";") : "",
			getGenerator());
	}

	@Override
	public String toString() {
		return U.toString(this);
	}

	private static String buildAnnotationPart(
		Metadata metadata,
		Relationship relation,
		Set<String> importPart) {
		List<String> result = new LinkedList<>();

		String pkPart = buildPKAnnotation(
			metadata.getPrimaryKeyMetadata(relation.getTablePath()));

		if (pkPart.length() > 0) {
			importPart.add(buildImportPart(PseudoPK.class));
			result.add(pkPart);
		}

		String fkPart = String.join(
			", ",
			Arrays.asList(relation.getRelationships())
				.stream()
				.map(r -> r.getCrossReference())
				.map(ORMGenerator::buildFKAnnotation)
				.filter(part -> part.length() > 0)
				.collect(Collectors.toList()));

		if (fkPart.length() > 0) {
			fkPart = "@" + FKs.class.getSimpleName() + "({" + fkPart + "})";
			result.add(fkPart);
			importPart.add(buildImportPart(FKs.class));
			importPart.add(buildImportPart(PseudoFK.class));
		}

		String resultString = String.join(U.LINE_SEPARATOR, result);

		return resultString.length() > 0 ? resultString + U.LINE_SEPARATOR : resultString;
	}

	private static String buildPKAnnotation(PrimaryKeyMetadata pk) {
		String[] columnNames = pk.getColumnNames();
		if (pk == null || columnNames.length == 0 || !pk.isPseudo()) return "";

		return buildPKAnnotation(PseudoPK.class, pk.getName(), columnNames);
	}

	private static String buildFKAnnotation(CrossReference fk) {
		String[] columnNames = fk.getForeignKeyColumnNames();
		if (columnNames.length == 0 || !fk.isPseudo()) return "";

		return buildFKAnnotation(
			PseudoFK.class,
			fk.getForeignKeyName(),
			fk.getPrimaryKeyTable().toString(),
			columnNames);
	}

	private static String buildPKAnnotation(
		Class<?> annotation,
		String keyName,
		String[] columnNames) {
		return "@"
			+ annotation.getSimpleName()
			+ "(name = \""
			+ keyName
			+ "\", columns = {"
			+ buildColumnsPart(columnNames)
			+ "})";
	}

	private static String buildFKAnnotation(
		Class<?> annotation,
		String keyName,
		String references,
		String[] columnNames) {
		return "@"
			+ annotation.getSimpleName()
			+ "(name = \""
			+ keyName
			+ "\", references = \""
			+ references
			+ "\", columns = {"
			+ buildColumnsPart(columnNames)
			+ "})";
	}

	private static String buildColumnsPart(String[] columnNames) {
		return String.join(
			", ",
			Arrays.asList(columnNames)
				.stream()
				.map(name -> "\"" + name + "\"")
				.collect(Collectors.toList()));
	}

	private static String buildImportPart(Class<?> target) {
		return "import " + target.getName() + ";";
	}

	private static Map<String, Boolean> createDuprecateChecker(Relationship relation) {
		Map<String, Boolean> checker = new HashMap<>();
		for (Relationship child : relation.getRelationships()) {
			String tableName = child.getTablePath().getTableName();
			if (checker.containsKey(tableName)) {
				checker.put(tableName, true);
			} else {
				checker.put(tableName, false);
			}
		}

		return checker;
	}

	private static Class<?> convertPrimitiveClassToWrapperClass(Class<?> target) {
		if (!target.isPrimitive()) return target;
		return primitiveToWrapperMap.get(target);
	}

	private Class<?> convertForNumber(Class<?> target) {
		if (!useNumberClass) return target;

		if (Number.class.isAssignableFrom(target)) return Number.class;

		return target;
	}

	private String getGenerator() {
		return generatorName != null ? generatorName : ORMGenerator.class.getName();
	}

	private static String toUpperCaseFirstLetter(String target) {
		return Character.toUpperCase(target.charAt(0)) + target.substring(1);
	}

	private static void write(
		File java,
		String body,
		Charset charset) throws IOException {
		try (BufferedWriter writer = new BufferedWriter(
			new OutputStreamWriter(
				new FileOutputStream(java),
				charset))) {
			writer.write(body);
			writer.flush();
		}
	}

	private static String buildTableComment(
		Metadata metadata,
		TablePath target) {
		TableMetadata tableMetadata = metadata.getTableMetadata(target);

		StringBuilder builder = new StringBuilder();
		builder.append("name: " + tableMetadata.getName());
		builder.append(U.LINE_SEPARATOR);
		builder.append("type: " + tableMetadata.getType());
		builder.append(U.LINE_SEPARATOR);
		builder.append("remarks: ");
		builder.append(U.trim(tableMetadata.getRemarks()));

		return decorate(builder.toString(), "");
	}

	private static String buildColumnComment(Column column) {
		ColumnMetadata metadata = column.getColumnMetadata();

		StringBuilder builder = new StringBuilder();
		builder.append("name: " + metadata.getName());
		builder.append(U.LINE_SEPARATOR);
		builder.append("remarks: ");
		builder.append(U.trim(metadata.getRemarks()));
		builder.append(U.LINE_SEPARATOR);

		boolean hasDecimalDigits = metadata.hasDecimalDigits() && metadata.getDecimalDigits() != 0 ? true : false;

		String sizeString = "("
			+ metadata.getSize()
			+ (hasDecimalDigits ? ", " + metadata.getDecimalDigits() : "")
			+ ")";

		builder.append("type: " + metadata.getTypeName() + sizeString);

		return decorate(builder.toString(), "\t");
	}

	private static String decorate(String base, String top) {
		String prefix = top + " * ";
		String suffix = "<br>";
		String[] lines = base.split("[\\r\\n]+");
		return prefix + String.join(suffix + U.LINE_SEPARATOR + prefix, lines) + suffix;
	}

	private static String[] pickupFromSource(String source, String key) {
		String patternBase = "/\\*==" + key + "==\\*/";
		Matcher matcher = Pattern.compile(
			patternBase + "(.+?)" + patternBase,
			Pattern.MULTILINE + Pattern.DOTALL).matcher(source);
		matcher.find();
		return new String[] { matcher.group(1), matcher.replaceAll("") };
	}

	private static String convertToTemplate(String source) {
		source = source.replaceAll("/\\*--\\*/.+?/\\*--\\*/", "");
		return source.replaceAll("/\\*\\+\\+(.+?)\\+\\+\\*/", "$1");
	}

	private static String readTemplate(Class<?> target, String charset) {
		URL templateURL = U.getResourcePathByName(target, target.getSimpleName() + ".java");
		try {
			return new String(U.readBytes(templateURL.openStream()), charset);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}
}
