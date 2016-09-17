package com.darrenswhite.chronicle.config;

import com.darrenswhite.chronicle.card.Card;
import com.darrenswhite.chronicle.effect.*;
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
	private static final String CONDITION_LINK_PATH = "csv/conditionlink.csv";
	private static final String CONDITION_CONSEQUENCE_LINK_PATH = "csv/conditiontoconsequencelink.csv";
	private static final String CONSEQUENCE_LINK_PATH = "csv/consequencelink.csv";
	private static final String EFFECT_CONDITIONS_PATH = "csv/effectconditions.csv";
	private static final String EFFECT_CONSEQUENCES_PATH = "csv/effectconsequences.csv";
	private static ConfigProvider instance;
	private List<Card> cards;
	private List<ConditionLink> conditionLinks;
	private List<ConditionConsequenceLink> conditionConsequenceLinks;
	private List<ConsequenceLink> consequenceLinks;
	private List<EffectCondition> effectConditions;
	private List<EffectConsequence> effectConsequences;

	private ConfigProvider() {
	}

	public Optional<Card> get(Predicate<Card> filter) {
		return cards.stream().filter(filter).findAny();
	}

	public Optional<Card> get(int cardId) {
		return cards.stream().filter(c -> c.getId() == cardId).findFirst();
	}

	public List<Card> getAll(Predicate<Card> filter) {
		return cards.stream().filter(filter).collect(Collectors.toList());
	}

	public List<ConditionConsequenceLink> getConditionConsequenceLinks(int cardId) {
		return conditionConsequenceLinks.stream().filter(c -> c.getCardId() == cardId).collect(Collectors.toCollection(LinkedList::new));
	}

	private List<ConditionLink> getConditionLinks(int cardId) {
		return conditionLinks.stream().filter(c -> c.getCardId() == cardId).collect(Collectors.toCollection(LinkedList::new));
	}

	public List<EffectCondition> getConditions(int cardId) {
		List<EffectCondition> conditions = new LinkedList<>();

		for (ConditionLink conditionLink : getConditionLinks(cardId)) {
			effectConditions.stream().filter(c -> c.getId() == conditionLink.getConditionId()).forEach(c -> {
				c.setValue(conditionLink.getConditionValue());
				conditions.add(c.copy());
			});
		}

		return conditions;
	}

	private List<ConsequenceLink> getConsequenceLinks(int cardId) {
		return consequenceLinks.stream().filter(c -> c.getCardId() == cardId).collect(Collectors.toCollection(LinkedList::new));
	}

	public List<EffectConsequence> getConsequences(int cardId) {
		List<EffectConsequence> consequences = new LinkedList<>();

		for (ConsequenceLink consequenceLink : getConsequenceLinks(cardId)) {
			effectConsequences.stream().filter(c -> c.getId() == consequenceLink.getConsequenceId()).forEach(c -> {
				c.setValue0(consequenceLink.getConsequenceValue0());
				c.setValue1(consequenceLink.getConsequenceValue1());
				consequences.add(c.copy());
			});
		}

		return consequences;
	}

	public static ConfigProvider getInstance() {
		if (instance == null) {
			instance = new ConfigProvider();
			instance.init();
		}

		return instance;
	}

	private void init() {
		conditionLinks = parse(Paths.get(CONDITION_LINK_PATH), ConditionLink::new);
		conditionConsequenceLinks = parse(Paths.get(CONDITION_CONSEQUENCE_LINK_PATH), ConditionConsequenceLink::new);
		consequenceLinks = parse(Paths.get(CONSEQUENCE_LINK_PATH), ConsequenceLink::new);
		effectConditions = parse(Paths.get(EFFECT_CONDITIONS_PATH), EffectCondition::new);
		effectConsequences = parse(Paths.get(EFFECT_CONSEQUENCES_PATH), EffectConsequence::new);
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