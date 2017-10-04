package org.blendee.selector;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UncheckedIOException;
import java.util.Arrays;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.blendee.internal.IOStream;
import org.blendee.jdbc.TablePath;

/**
 * {@link IOStream} から定義情報を読み込むことができる {@link ColumnRepository} です。
 * @author 千葉 哲嗣
 */
public class StreamColumnRepository extends AbstractColumnRepository {

	private static final Pattern tableLinePattern = Pattern
		.compile("^id=([^&]+)&path=([^&]*)&using=\\{([^\\}]*)\\}&timestamp=(\\d+)$");

	private static final Pattern columnLinePattern = Pattern.compile("^id=([^&]+)&column=([^&]+)&mark=(true|false)$");

	private final IOStream repository;

	/**
	 * このクラスのインスタンスを生成します。
	 * @param repository リポジトリのソース
	 */
	public StreamColumnRepository(IOStream repository) {
		this.repository = repository;
		initialize();
	}

	@Override
	void read(Map<String, TablePathSource> tablePathMap) {
		String[] lines;
		try (InputStream input = new BufferedInputStream(repository.getInputStream())) {
			lines = SimpleResourceReader.readLines(input);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}

		for (int i = 0; i < lines.length; i++) {
			String line = lines[i];
			Matcher tableMatcher = tableLinePattern.matcher(line);
			Matcher columnMatcher = columnLinePattern.matcher(line);
			if (tableMatcher.matches()) {
				TablePathSource source = new TablePathSource(
					tableMatcher.group(1),
					TablePath.parse(tableMatcher.group(2)),
					Arrays.asList(tableMatcher.group(3).split(","))
						.stream()
						.filter(e -> e.length() > 0)
						.collect(Collectors.toList()),
					Long.parseLong(tableMatcher.group(4)));
				tablePathMap.put(source.getId(), source);
			} else if (columnMatcher.matches()) {
				TablePathSource source = tablePathMap.get(columnMatcher.group(1));
				if (source == null) throw new IllegalStateException("IDが存在しません " + "[" + line + "]");

				source.add(
					new ColumnSource(
						new ColumnPath(columnMatcher.group(2)),
						Boolean.parseBoolean(columnMatcher.group(3))));
			} else {
				throw new IllegalStateException("不正なフォーマットです " + "[" + line + "]");
			}
		}
	}

	@Override
	void write(Map<String, TablePathSource> tablePathMap) {
		try (PrintWriter writer = new PrintWriter(
			new BufferedWriter(new OutputStreamWriter(repository.getOutputStream())))) {
			String[] ids = getIDs();
			for (String id : ids) {
				TablePathSource source = tablePathMap.get(id);
				writer.println(source);
				for (ColumnSource columnSource : source.getColumnSources())
					writer.println("id=" + id + "&column=" + columnSource);
				writer.println();
				writer.flush();
			}

			writer.flush();
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}
}
