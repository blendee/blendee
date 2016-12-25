package jp.ats.blendee.develop;

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

import jp.ats.blendee.internal.U;
import jp.ats.blendee.jdbc.ColumnMetadata;
import jp.ats.blendee.jdbc.CrossReference;
import jp.ats.blendee.jdbc.BContext;
import jp.ats.blendee.jdbc.Metadata;
import jp.ats.blendee.jdbc.PrimaryKeyMetadata;
import jp.ats.blendee.jdbc.ResourceLocator;
import jp.ats.blendee.jdbc.TableMetadata;
import jp.ats.blendee.sql.Column;
import jp.ats.blendee.sql.Relationship;
import jp.ats.blendee.sql.RelationshipFactory;
import jp.ats.blendee.support.Many;
import jp.ats.blendee.support.annotation.DTORelationship;
import jp.ats.blendee.support.annotation.FKs;
import jp.ats.blendee.support.annotation.PseudoFK;
import jp.ats.blendee.support.annotation.PseudoPK;

/**
 * データベースの構成を読み取り、各テーブルの DAO クラスと DTO クラスの Java ソースを生成するジェネレータクラスです。
 *
 * @author 千葉 哲嗣
 */
public class ORMGenerator {

	private static final CodeFormatter defaultCodeFormatter = new DefaultCodeFormatter();

	private static final String constantsTemplate;

	private static final String daoTemplate;

	private static final String dtoTemplate;

	private static final String queryTemplate;

	private static final String columnPartTemplate;

	private static final String relationshipPartTemplate;

	private static final String dtoPropertyAccessorPartTemplate;

	private static final String dtoRelationshipPartTemplate;

	private static final String queryColumnPart1Template;

	private static final String queryRelationshipPart1Template;

	private static final String queryColumnPart2Template;

	private static final String queryRelationshipPart2Template;

	private static final String queryRelationshipPart3Template;

	private static final Map<Class<?>, Class<?>> primitiveToWrapperMap = new HashMap<>();

	private final Metadata metadata;

	private final String packageName;

	private final String schemaName;

	private final Class<?> daoSuperclass;

	private final Class<?> dtoSuperclass;

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
			String source = readTemplate(Constants.class, charset);
			{
				String[] result = pickupFromSource(source, "ColumnPart");
				columnPartTemplate = convertToTemplate(result[0]);
				source = result[1];
			}
			{
				String[] result = pickupFromSource(source, "RelationshipPart");
				relationshipPartTemplate = convertToTemplate(result[0]);
				source = result[1];
			}

			constantsTemplate = convertToTemplate(source);
			daoTemplate = convertToTemplate(readTemplate(DAOBase.class, charset));
		}

		{
			String source = readTemplate(DTOBase.class, charset);
			{
				String[] result = pickupFromSource(source, "DTOPropertyAccessorPart");
				dtoPropertyAccessorPartTemplate = convertToTemplate(result[0]);
				source = result[1];
			}
			{
				String[] result = pickupFromSource(source, "DTORelationshipPart");
				dtoRelationshipPartTemplate = convertToTemplate(result[0]);
				source = result[1];
			}

			dtoTemplate = convertToTemplate(source);
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
	 * @param packageName DAO クラス、 DTO クラスが属するパッケージ名
	 * @param schemaName テーブルを読み込む対象となるスキーマ
	 * @param daoSuperclass DAO クラスの親クラス
	 * @param dtoSuperclass DTO クラスの親クラス
	 * @param querySuperclass Query クラスの親クラス
	 * @param codeAdjuster {@link CodeFormatter}
	 * @param useNumberClass DTO クラスの数値型項目を {@link Number} で統一する
	 * @param useNullGuard DTO クラスの項目に null ガードを適用する
	 * @param generatorName ジェネレータ名
	 */
	public ORMGenerator(
		Metadata metadata,
		String packageName,
		String schemaName,
		Class<?> daoSuperclass,
		Class<?> dtoSuperclass,
		Class<?> querySuperclass,
		CodeFormatter codeAdjuster,
		boolean useNumberClass,
		boolean useNullGuard,
		String generatorName) {
		this.metadata = Objects.requireNonNull(metadata);
		this.packageName = Objects.requireNonNull(packageName);

		this.schemaName = schemaName;
		this.daoSuperclass = daoSuperclass != null ? daoSuperclass : Object.class;
		this.dtoSuperclass = dtoSuperclass != null ? dtoSuperclass : Object.class;
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

		ResourceLocator[] tables = metadata.getTables(schemaName);
		for (ResourceLocator table : tables) {
			Relationship relation = BContext.get(RelationshipFactory.class).getInstance(table);

			String tableName = relation.getResourceLocator().getTableName();

			write(
				new File(packageDir, createConstantsCompilationUnitName(tableName)),
				buildConstants(relation),
				srcCharset);

			write(
				new File(packageDir, createDAOCompilationUnitName(tableName)),
				buildDAO(relation),
				srcCharset);

			write(
				new File(packageDir, createDTOCompilationUnitName(tableName)),
				buildDTO(relation),
				srcCharset);

			write(
				new File(packageDir, createQueryCompilationUnitName(tableName)),
				buildQuery(relation),
				srcCharset);
		}
	}

	/**
	 * このクラスが生成する定数クラスのコンパイル単位名を返します。
	 *
	 * @param tableName 対象となるテーブル名
	 * @return コンパイル単位名
	 */
	public static String createConstantsCompilationUnitName(String tableName) {
		return tableName + "Constants.java";
	}

	/**
	 * このクラスが生成する DAO のコンパイル単位名を返します。
	 *
	 * @param tableName 対象となるテーブル名
	 * @return コンパイル単位名
	 */
	public static String createDAOCompilationUnitName(String tableName) {
		return tableName + "DAO.java";
	}

	/**
	 * このクラスが生成する DTO のコンパイル単位名を返します。
	 *
	 * @param tableName 対象となるテーブル名
	 * @return コンパイル単位名
	 */
	public static String createDTOCompilationUnitName(String tableName) {
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
	 * 定数クラスを一件作成します。
	 *
	 * @param relation 対象となるテーブルをあらわす {@link Relationship}
	 * @return 生成されたソース
	 */
	public String buildConstants(Relationship relation) {
		if (!relation.isRoot()) throw new IllegalArgumentException("relation はルートでなければなりません");

		String columnPart;
		{
			List<String> list = new LinkedList<>();
			for (Column column : relation.getColumns()) {
				list.add(codeFormatter.formatConstantsColumnPart(
					columnPartTemplate,
					column.getName(),
					buildColumnComment(column)));
			}
			columnPart = String.join("", list);
		}

		String relationshipPart;
		{
			List<String> list = new LinkedList<>();
			for (Relationship child : relation.getRelationships()) {
				String foreignKey = child.getCrossReference().getForeignKeyName();
				list.add(codeFormatter.formatConstantsRelationshipPart(
					relationshipPartTemplate,
					child.getResourceLocator().getTableName(),
					foreignKey));
			}
			relationshipPart = String.join("", list);
		}

		ResourceLocator target = relation.getResourceLocator();

		return codeFormatter.formatConstants(
			constantsTemplate,
			packageName,
			schemaName,
			target.getTableName(),
			columnPart,
			relationshipPart,
			buildTableComment(metadata, target),
			getGenerator());
	}

	/**
	 * DAO クラスを作成します。
	 *
	 * @param relation 対象となるテーブルをあらわす {@link Relationship}
	 * @return 生成されたソース
	 */
	public String buildDAO(Relationship relation) {
		if (!relation.isRoot()) throw new IllegalArgumentException("relation はルートでなければなりません");

		ResourceLocator target = relation.getResourceLocator();

		return codeFormatter.formatDAO(
			daoTemplate,
			packageName,
			target.getTableName(),
			daoSuperclass.getName(),
			buildTableComment(metadata, target),
			getGenerator());
	}

	/**
	 * DTO クラスを一件作成します。
	 *
	 * @param relation 対象となるテーブルをあらわす {@link Relationship}
	 * @return 生成されたソース
	 */
	public String buildDTO(Relationship relation) {
		if (!relation.isRoot()) throw new IllegalArgumentException("relation はルートでなければなりません");

		ResourceLocator target = relation.getResourceLocator();
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

				list.add(codeFormatter.formatDTOPropertyAccessorPart(
					dtoPropertyAccessorPartTemplate,
					tableName,
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

				String childTableName = child.getResourceLocator().getTableName();
				String methodName = checker.get(childTableName) ? childTableName + "By" + foreignKey : childTableName;

				list.add(codeFormatter.formatDTORelationshipPart(
					dtoRelationshipPartTemplate,
					child.getResourceLocator().getTableName(),
					foreignKey,
					String.join(", ", crossReference.getForeignKeyColumnNames()),
					methodName,
					tableName));
			}

			if (list.size() > 0) importPart.add(buildImportPart(DTORelationship.class));

			relationshipPart = String.join("", list);
		}

		return codeFormatter.formatDTO(
			dtoTemplate,
			packageName,
			schemaName,
			tableName,
			dtoSuperclass.getName(),
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

		String rootTableName = relation.getResourceLocator().getTableName();
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
					rootTableName));
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
				ResourceLocator childResourceLocator = child.getResourceLocator();

				String foreignKey = child.getCrossReference().getForeignKeyName();

				String tableName = childResourceLocator.getTableName();

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
					typeParam));

				list3.add(codeFormatter.formatQueryRelationshipPart1(
					queryRelationshipPart3Template,
					tableName,
					foreignKey,
					relationship,
					rootTableName,
					typeParam));
			}

			relationshipPart1 = String.join("", list1);
			relationshipPart2 = String.join("", list2);
			relationshipPart3 = String.join("", list3);
		}

		String tableName = relation.getResourceLocator().getTableName();

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
			metadata.getPrimaryKeyMetadata(relation.getResourceLocator()));

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
			fk.getPrimaryKeyResource().toString(),
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
			String tableName = child.getResourceLocator().getTableName();
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
		ResourceLocator target) {
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
