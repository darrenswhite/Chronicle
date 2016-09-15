package com.darrenswhite.chronicle.config;

import com.darrenswhite.chronicle.card.Card;
import com.darrenswhite.chronicle.effect.EffectCondition;
import com.darrenswhite.chronicle.effect.EffectConditionLink;
import com.darrenswhite.chronicle.effect.EffectConsequence;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.input.BOMInputStream;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author Darren White
 */
public class ConfigProvider {

	private static final CSVFormat format = CSVFormat.DEFAULT.withDelimiter('\t').withFirstRecordAsHeader();
	private static final String CARDS_PATH = "csv/cards.csv";
	private static final String CONDITIONS_PATH = "csv/conditionlink.csv";
	private static final String LINKS_PATH = "csv/conditiontoconsequencelink.csv";
	private static final String CONSEQUENCES_PATH = "csv/consequencelink.csv";
	private static ConfigProvider instance;
	private List<Card> cards;
	private List<EffectCondition> conditions;
	private List<EffectConditionLink> links;
	private List<EffectConsequence> consequences;

	private ConfigProvider() {
	}

	public Optional<Card> get(Predicate<Card> filter) {
		return cards.stream().filter(filter).findAny();
	}

	public List<Card> getAll(Predicate<Card> filter) {
		return cards.stream().filter(filter).collect(Collectors.toList());
	}

	public List<EffectCondition> getConditions(int cardId) {
		return conditions.stream().filter(c -> c.getCardId() == cardId).collect(Collectors.toCollection(LinkedList::new));
	}

	public List<EffectConsequence> getConsequences(int cardId) {
		return consequences.stream().filter(c -> c.getCardId() == cardId).collect(Collectors.toCollection(LinkedList::new));
	}

	public static ConfigProvider getInstance() {
		if (instance == null) {
			instance = new ConfigProvider();
			instance.init();
		}

		return instance;
	}

	public List<EffectConditionLink> getLinks(int cardId) {
		return links.stream().filter(c -> c.getCardId() == cardId).collect(Collectors.toCollection(LinkedList::new));
	}

	private void init() {
		conditions = parse(Paths.get(CONDITIONS_PATH), EffectCondition::new);
		links = parse(Paths.get(LINKS_PATH), EffectConditionLink::new);
		consequences = parse(Paths.get(CONSEQUENCES_PATH), EffectConsequence::new);
		cards = parse(Paths.get(CARDS_PATH), Card::new);
	}

	private <T extends ConfigTemplate> List<T> parse(Path path, BiFunction<Map<String, Integer>, CSVRecord, T> f) {
		List<T> data = new LinkedList<>();

		try (CSVParser parser = new CSVParser(new InputStreamReader(new BOMInputStream(Files.newInputStream(path))), format)) {

			for (CSVRecord r : parser) {
				try {
					T t = f.apply(parser.getHeaderMap(), r);
					data.add(t);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return data;
	}
}