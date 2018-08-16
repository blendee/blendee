package org.blendee.develop.ormgen;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.UncheckedIOException;
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

import javax.lang.model.SourceVersion;

import org.blendee.internal.U;
import org.blendee.jdbc.ColumnMetadata;
import org.blendee.jdbc.ContextManager;
import org.blendee.jdbc.CrossReference;
import org.blendee.jdbc.Metadata;
import org.blendee.jdbc.PrimaryKeyMetadata;
import org.blendee.jdbc.TableMetadata;
import org.blendee.jdbc.TablePath;
import org.blendee.sql.Column;
import org.blendee.sql.Relationship;
import org.blendee.sql.RelationshipFactory;
import org.blendee.support.Many;
import org.blendee.support.TableFacadePackageRule;
import org.blendee.support.annotation.FKs;
import org.blendee.support.annotation.PseudoFK;
import org.blendee.support.annotation.PseudoPK;
import org.blendee.support.annotation.RowRelationship;

/**
 * データベースの構成を読み取り、各テーブルクラスの Java ソースを生成するジェネレータクラスです。
 * @author 千葉 哲嗣
 */
public class TableFacadeGenerator {

	private static final CodeFormatter defaultCodeFormatter = new CodeFormatter() {};

	private static final String template;

	private static final String columnNamesPartTemplate;

	private static final String relationshipsPartTemplate;

	private static final String rowPropertyAccessorPartTemplate;

	private static final String rowRelationshipPartTemplate;

	private static final String relationshipColumnPart1Template;

	private static final String relationshipColumnPart2Template;

	private static final String tableRelationshipPartTemplate;

	private static final Map<Class<?>, Class<?>> primitiveToWrapperMap = new HashMap<>();

	private final Metadata metadata;

	private final String rootPackageName;

	private final Class<?> managerSuperclass;

	private final Class<?> rowSuperclass;

	private final CodeFormatter codeFormatter;

	private final boolean useNumberClass;

	private final boolean useNullGuard;

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
		String source = readTemplate(TableFacadeTemplate.class, "UTF-8");
		{
			String[] result = pickupFromSource(source, "ColumnNamesPart");
			columnNamesPartTemplate = convertToTemplate(result[0]);
			source = result[1];
		}

		{
			String[] result = pickupFromSource(source, "RelationshipsPart");
			relationshipsPartTemplate = convertToTemplate(result[0]);
			source = result[1];
		}

		{
			String[] result = pickupFromSource(source, "RowPropertyAccessorPart");
			rowPropertyAccessorPartTemplate = convertToTemplate(result[0]);
			source = result[1];
		}

		{
			String[] result = pickupFromSource(source, "RowRelationshipPart");
			rowRelationshipPartTemplate = convertToTemplate(result[0]);
			source = result[1];
		}

		{
			String[] result = pickupFromSource(source, "ColumnPart1");
			relationshipColumnPart1Template = convertToTemplate(result[0]);
			source = result[1];
		}

		{
			String[] result = pickupFromSource(source, "ColumnPart2");
			relationshipColumnPart2Template = convertToTemplate(result[0]);
			source = result[1];
		}

		{
			String[] result = pickupFromSource(source, "TableRelationshipPart");
			tableRelationshipPartTemplate = convertToTemplate(result[0]);
			source = result[1];
		}

		template = convertToTemplate(source);
	}

	/**
	 * インスタンスを生成します。
	 * @param metadata テーブルを読み込む対象となるデータベースの {@link Metadata}
	 * @param rootPackageName 各自動生成クラスが属するパッケージの親パッケージ
	 * @param managerSuperclass RowManager クラスの親クラス
	 * @param rowSuperclass Row クラスの親クラス
	 * @param codeFormatter {@link CodeFormatter}
	 * @param useNumberClass Row クラスの数値型項目を {@link Number} で統一する
	 * @param useNullGuard Row クラスの項目に null ガードを適用する
	 */
	public TableFacadeGenerator(
		Metadata metadata,
		String rootPackageName,
		Class<?> managerSuperclass,
		Class<?> rowSuperclass,
		CodeFormatter codeFormatter,
		boolean useNumberClass,
		boolean useNullGuard) {
		this.metadata = Objects.requireNonNull(metadata);
		this.rootPackageName = Objects.requireNonNull(rootPackageName);
		this.managerSuperclass = managerSuperclass != null ? managerSuperclass : Object.class;
		this.rowSuperclass = rowSuperclass != null ? rowSuperclass : Object.class;

		this.codeFormatter = codeFormatter == null ? defaultCodeFormatter : codeFormatter;

		this.useNumberClass = useNumberClass;
		this.useNullGuard = useNullGuard;
	}

	/**
	 * 自動生成可能なテーブル名かどうか判定します。
	 * @param name テーブル名
	 * @return 生成可能な場合 true
	 */
	public static boolean isGeneratableTableName(String name) {
		return SourceVersion.isName(name);
	}

	/**
	 * すべてのクラスファイルを生成します。
	 * @param schemaName 対象となるスキーマ
	 * @param home 生成された Java ソースを保存するためのルートとなる場所
	 * @param srcCharset 生成する Java ソースの文字コード
	 * @throws IOException ファイル書き込みに失敗した場合
	 */
	public void build(String schemaName, File home, Charset srcCharset) throws IOException {
		File rootPackageDir = new File(home, String.join("/", rootPackageName.split("\\.")));
		rootPackageDir.mkdirs();

		File packageDir = new File(rootPackageDir, TableFacadePackageRule.care(schemaName));
		packageDir.mkdir();

		TablePath[] tables = metadata.getTables(schemaName);
		for (TablePath table : tables) {

			Relationship relation = ContextManager.get(RelationshipFactory.class).getInstance(table);

			String tableName = relation.getTablePath().getTableName();

			//使用できない名前の場合
			if (!isGeneratableTableName(tableName)) return;
			//TODO 警告出す方法を検討

			write(
				new File(packageDir, createCompilationUnitName(tableName)),
				build(relation),
				srcCharset);
		}
	}

	/**
	 * このクラスが生成する Table のコンパイル単位名を返します。
	 * @param tableName 対象となるテーブル名
	 * @return コンパイル単位名
	 */
	public static String createCompilationUnitName(String tableName) {
		checkName(tableName);
		return tableName + ".java";
	}

	/**
	 * Table クラスを一件作成します。
	 * @param relation 対象となるテーブルをあらわす {@link Relationship}
	 * @return 生成されたソース
	 */
	public String build(Relationship relation) {
		if (!relation.isRoot()) throw new IllegalArgumentException("relation はルートでなければなりません");

		TablePath target = relation.getTablePath();

		String schemaName = target.getSchemaName();

		String packageName = rootPackageName + "." + TableFacadePackageRule.care(schemaName);

		String tableName = target.getTableName();

		checkName(tableName);

		Set<String> importPart = new LinkedHashSet<>();

		String columnNamesPart, propertyAccessorPart, columnPart1, columnPart2;
		{
			List<String> columnNames = new LinkedList<>();
			List<String> properties = new LinkedList<>();
			List<String> list1 = new LinkedList<>();
			List<String> list2 = new LinkedList<>();

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
						nullCheck = Objects.class.getSimpleName() + ".requireNonNull(value);" + U.LINE_SEPARATOR;
					} else {
						String optional = Optional.class.getSimpleName();
						returnPrefix = optional + ".ofNullable(";
						returnSuffix = ")";
						returnType = optional + "<" + classNameString + ">";
						returnOptional = true;
					}
				}

				String columnName = safe(column.getName());

				Map<String, String> args = new HashMap<>();
				args.put("PACKAGE", packageName);
				args.put("TABLE", tableName);
				args.put("METHOD", toUpperCaseFirstLetter(columnName));
				args.put("COLUMN", columnName);
				args.put("TYPE", classNameString);
				args.put("COMMENT", buildColumnComment(column));
				args.put("NULL_CHECK", nullCheck);
				args.put("RETURN_TYPE", returnType);
				args.put("PREFIX", returnPrefix);
				args.put("SUFFIX", returnSuffix);
				args.put("OPTIONAL", Boolean.toString(returnOptional));

				columnNames.add(
					codeFormatter.formatColumnNamesPart(columnNamesPartTemplate, args));

				properties.add(
					codeFormatter.formatRowPropertyAccessorPart(rowPropertyAccessorPartTemplate, args));

				list1.add(
					codeFormatter.formatRelationshipColumnPart1(relationshipColumnPart1Template, args));

				list2.add(
					codeFormatter.formatRelationshipColumnPart2(relationshipColumnPart2Template, args));
			}

			columnNamesPart = String.join("", columnNames);
			propertyAccessorPart = String.join("", properties);
			columnPart1 = String.join("", list1);
			columnPart2 = String.join("", list2);
		}

		String relationshipsPart, rowRelationshipPart, myTemplate, tableRelationshipPart;
		{
			Map<String, Boolean> checker = createDuprecateChecker(relation);

			List<String> relationships = new LinkedList<>();
			List<String> rowRelationships = new LinkedList<>();
			List<String> tableRelationships = new LinkedList<>();

			for (Relationship child : relation.getRelationships()) {
				CrossReference crossReference = child.getCrossReference();
				String foreignKey = crossReference.getForeignKeyName();

				TablePath childPath = child.getTablePath();
				String childTableName = childPath.getTableName();

				String methodName = "$" + (checker.get(childTableName) ? childTableName + "$" + foreignKey : childTableName);

				String relationship = "$" + (checker.get(childTableName) ? childTableName + "$" + foreignKey : childTableName);

				String typeParam = Many.class.getSimpleName() + "<" + packageName + "." + tableName + ".Row, M>";

				Map<String, String> args = new HashMap<>();
				args.put("PACKAGE", packageName);
				args.put("TABLE", tableName);
				args.put("REFERENCE_PACKAGE", rootPackageName + "." + TableFacadePackageRule.care(childPath.getSchemaName()));
				args.put("REFERENCE", childTableName);
				args.put("FK", foreignKey);
				args.put("FK_COLUMNS", String.join(", ", crossReference.getForeignKeyColumnNames()));
				args.put("METHOD", methodName);
				args.put("RELATIONSHIP", relationship);
				args.put("MANY", typeParam);

				relationships.add(
					codeFormatter.formatRelationshipsPart(
						relationshipsPartTemplate,
						args));

				rowRelationships.add(
					codeFormatter.formatRowRelationshipPart(
						rowRelationshipPartTemplate,
						args));

				tableRelationships.add(
					codeFormatter.formatTableRelationshipPart(
						tableRelationshipPartTemplate,
						args));
			}

			if (relationships.size() > 0) {
				importPart.add(buildImportPart(RowRelationship.class));
				importPart.add(buildImportPart(Many.class));
			}

			myTemplate = erase(template, relationships.isEmpty());

			relationshipsPart = String.join("", relationships);
			rowRelationshipPart = String.join("", rowRelationships);
			tableRelationshipPart = String.join("", tableRelationships);
		}

		Map<String, String> args = new HashMap<>();
		args.put("PACKAGE", packageName);
		args.put("SCHEMA", schemaName);
		args.put("TABLE", tableName);
		args.put("ANNOTATION", buildAnnotationPart(metadata, relation, importPart));
		args.put("IMPORTS", String.join(U.LINE_SEPARATOR, importPart));
		args.put("PARENT", managerSuperclass.getName());
		args.put("COLUMN_NAMES_PART", columnNamesPart);
		args.put("RELATIONSHIPS_PART", relationshipsPart);
		args.put("ROW_PARENT", rowSuperclass.getName());
		args.put("ROW_PROPERTY_ACCESSOR_PART", propertyAccessorPart);
		args.put("ROW_RELATIONSHIP_PART", rowRelationshipPart);
		args.put("COLUMN_PART1", columnPart1);
		args.put("COLUMN_PART2", columnPart2);
		args.put("TABLE_RELATIONSHIP_PART", tableRelationshipPart);
		args.put("TABLE_COMMENT", buildTableComment(metadata, target));

		return codeFormatter.format(myTemplate, args);
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
			Arrays.stream(relation.getRelationships())
				.map(r -> r.getCrossReference())
				.map(TableFacadeGenerator::buildFKAnnotation)
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
			Arrays.stream(columnNames)
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

	private static String toUpperCaseFirstLetter(String target) {
		return Character.toUpperCase(target.charAt(0)) + target.substring(1);
	}

	private static void write(
		File java,
		String body,
		Charset charset)
		throws IOException {
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
		builder.append("schema: " + target.getSchemaName());
		builder.append(U.LINE_SEPARATOR);
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

	private static String erase(String source, boolean erase) {
		if (erase)
			return source.replaceAll("/\\*--\\?--\\*/.+?/\\*--\\?--\\*/", "");

		return source.replaceAll("/\\*--\\?--\\*/", "");
	}

	private static String readTemplate(Class<?> target, String charset) {
		try (InputStream input = target.getResourceAsStream(target.getSimpleName() + ".java")) {
			return new String(U.readBytes(input), charset);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	/**
	 * 使用できない名前の検査
	 */
	private static void checkName(String name) {
		if (!isGeneratableTableName(name)) throw new IllegalStateException(name);
	}

	private static String safe(String name) {
		if (!SourceVersion.isName(name)) return "_" + name;
		return name;
	}
}
