package com.darrenswhite.chronicle.config;

import org.apache.commons.csv.CSVRecord;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.Map;

/**
 * @author Darren White
 */
public abstract class ConfigTemplate {

	private final Map<String, Integer> headers;
	private final CSVRecord record;

	public ConfigTemplate(Map<String, Integer> headers, CSVRecord record) {
		this.headers = headers;
		this.record = record;
	}

	public Map<String, Integer> getHeaders() {
		return headers;
	}

	public CSVRecord getRecord() {
		return record;
	}

	protected final boolean parseBoolean(String s) {
		return Boolean.parseBoolean(s);
	}

	protected final double parseDouble(String s) {
		try {
			return Double.parseDouble(s);
		} catch (NumberFormatException e) {
			return 0;
		}
	}

	protected final <T extends Enum<?>> T parseEnum(Class<? extends T> clazz, String s) {
		try {
			return clazz.getEnumConstants()[Integer.parseInt(s)];
		} catch (NumberFormatException e) {
			return null;
		}
	}

	protected final double parseFloat(String s) {
		try {
			return Float.parseFloat(s);
		} catch (NumberFormatException e) {
			return 0;
		}
	}

	protected final int parseInt(String s) {
		try {
			return Integer.parseInt(s);
		} catch (NumberFormatException e) {
			return 0;
		}
	}

	protected final Object parseJSON(String s) {
		try {
			return new JSONParser().parse(s);
		} catch (ParseException e) {
			return null;
		}
	}
}