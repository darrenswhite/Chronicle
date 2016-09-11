package com.darrenswhite.chronicle.io;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.input.BOMInputStream;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Darren White
 */
public class CSVReader<T> {

	private static final CSVFormat format = CSVFormat.DEFAULT.withDelimiter('\t').withFirstRecordAsHeader();

	private final CSVParser parser;
	private final Class<T> clazz;

	private Set<T> data;

	public CSVReader(Path path, Class<T> clazz) {
		try {
			parser = new CSVParser(new InputStreamReader(new BOMInputStream(Files.newInputStream(path))), format);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		this.clazz = clazz;
	}

	private T createInstance(CSVRecord r) {
		T t;

		try {
			t = clazz.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			return null;
		}

		for (Field f : clazz.getDeclaredFields()) {
			Object value = getValue(r, f);

			try {
				if (value != null) {
					f.set(t, value);
				}
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}

		return t;
	}

	private Object getValue(CSVRecord r, Field f) {
		String name = f.getName();

		if (!parser.getHeaderMap().containsKey(name)) {
			return null;
		}

		int header = parser.getHeaderMap().get(name);
		String stringValue = r.get(header);

		return parseValue(f.getType(), stringValue);
	}

	public Set<T> parse() {
		if (data == null) {
			data = new HashSet<>();

			for (CSVRecord r : parser) {
				T t = createInstance(r);

				if (t != null) {
					data.add(t);
				}
			}
		}

		return data;
	}

	private Object parseValue(Class<?> type, String stringValue) {
		Object value = null;

		if (stringValue.isEmpty()) {
			return null;
		}

		if (type.equals(int.class) || type.equals(Integer.class)) {
			value = Integer.parseInt(stringValue);
		} else if (type.equals(double.class) || type.equals(Double.class)) {
			value = Double.parseDouble(stringValue);
		} else if (type.equals(float.class) || type.equals(Float.class)) {
			value = Float.parseFloat(stringValue);
		} else if (type.equals(JSONObject.class) || type.equals(JSONArray.class)) {
			try {
				value = new JSONParser().parse(stringValue);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		} else if (type.equals(boolean.class) || type.equals(Boolean.class)) {
			value = Boolean.parseBoolean(stringValue);
		} else if (type.equals(String.class)) {
			value = stringValue;
		}

		return value;
	}
}