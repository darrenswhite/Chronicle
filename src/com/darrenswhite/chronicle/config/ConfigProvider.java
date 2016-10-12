package com.darrenswhite.chronicle.config;

import com.darrenswhite.chronicle.card.Card;
import com.darrenswhite.chronicle.effect.*;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.input.BOMInputStream;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
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
	private final Map<Integer, List<ConditionConsequenceLink>> conditionConsequenceLinkCache = new HashMap<>();
	private final Map<Integer, List<ConditionLink>> conditionLinkCache = new HashMap<>();
	private final Map<Integer, List<ConsequenceLink>> consequenceLinkCache = new HashMap<>();
	private final Map<Integer, List<EffectCondition>> effectConditionCache = new HashMap<>();
	private final Map<Integer, List<EffectConsequence>> effectConsequenceCache = new HashMap<>();
	private Map<Integer, Card> cards;
	private List<ConditionConsequenceLink> conditionConsequenceLinks;
	private List<ConditionLink> conditionLinks;
	private List<ConsequenceLink> consequenceLinks;
	private List<EffectCondition> effectConditions;
	private List<EffectConsequence> effectConsequences;

	private ConfigProvider() {
	}

	public Card getCard(int cardId) {
		return cards.get(cardId);
	}

	public Optional<Card> getCard(Predicate<Card> filter) {
		return cards.values().stream().filter(filter).findAny();
	}

	public List<Card> getCards(Predicate<Card> filter) {
		return cards.values().stream().filter(filter).collect(Collectors.toList());
	}

	public List<ConditionConsequenceLink> getConditionConsequenceLinks(int cardId) {
		List<ConditionConsequenceLink> links = conditionConsequenceLinkCache.get(cardId);

		if (links == null) {
			links = conditionConsequenceLinks.stream().filter(c ->
					c.getCardId() == cardId).collect(Collectors.toCollection(LinkedList::new));
			conditionConsequenceLinkCache.put(cardId, links);
		}

		return links;
	}

	private List<ConditionLink> getConditionLinks(int cardId) {
		List<ConditionLink> links = conditionLinkCache.get(cardId);

		if (links == null) {
			links = conditionLinks.stream().filter(c ->
					c.getCardId() == cardId).collect(Collectors.toCollection(LinkedList::new));
			conditionLinkCache.put(cardId, links);
		}

		return links;
	}

	public List<EffectCondition> getConditions(int cardId) {
		List<EffectCondition> conditions = effectConditionCache.get(cardId);

		if (conditions == null) {
			List<EffectCondition> ec = new LinkedList<>();

			for (ConditionLink conditionLink : getConditionLinks(cardId)) {
				effectConditions.stream().filter(c -> c.getId() == conditionLink.getConditionId()).forEach(c -> {
					EffectCondition condition = c.copy();
					condition.setValue(conditionLink.getConditionValue());
					ec.add(condition);
				});
			}

			conditions = ec;
			effectConditionCache.put(cardId, conditions);
		}

		return conditions;
	}

	private List<ConsequenceLink> getConsequenceLinks(int cardId) {
		List<ConsequenceLink> links = consequenceLinkCache.get(cardId);

		if (links == null) {
			links = consequenceLinks.stream().filter(c ->
					c.getCardId() == cardId).collect(Collectors.toCollection(LinkedList::new));
			consequenceLinkCache.put(cardId, links);
		}

		return links;
	}

	public List<EffectConsequence> getConsequences(int cardId) {
		List<EffectConsequence> consequences = effectConsequenceCache.get(cardId);

		if (consequences == null) {
			List<EffectConsequence> ec = new LinkedList<>();

			for (ConsequenceLink consequenceLink : getConsequenceLinks(cardId)) {
				effectConsequences.stream().filter(c -> c.getId() == consequenceLink.getConsequenceId()).forEach(c -> {
					EffectConsequence consequence = c.copy();
					consequence.setValue(consequenceLink.getConsequenceValue0(), consequenceLink.getConsequenceValue1());
					ec.add(consequence);
				});
			}

			consequences = ec;
			effectConsequenceCache.put(cardId, consequences);
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

	private InputStream getResource(String name) {
		InputStream in = getClass().getResourceAsStream('/' + name);

		if (in == null) {
			try {
				in = Files.newInputStream(Paths.get(name));
			} catch (IOException e) {
				return null;
			}
		}

		return in;
	}

	private void init() {
		conditionLinks = parse(getResource(CONDITION_LINK_PATH), ConditionLink::new);
		conditionConsequenceLinks = parse(getResource(CONDITION_CONSEQUENCE_LINK_PATH), ConditionConsequenceLink::new);
		consequenceLinks = parse(getResource(CONSEQUENCE_LINK_PATH), ConsequenceLink::new);
		effectConditions = parse(getResource(EFFECT_CONDITIONS_PATH), EffectCondition::create);
		effectConsequences = parse(getResource(EFFECT_CONSEQUENCES_PATH), EffectConsequence::create);
		cards = parse(getResource(CARDS_PATH), Card::new).stream().collect(Collectors.toMap(Card::getId, c -> c));
	}

	private <T extends ConfigTemplate> List<T> parse(InputStream in, BiFunction<Map<String, Integer>, CSVRecord, T> f) {
		List<T> data = new LinkedList<>();

		try (CSVParser parser = new CSVParser(new InputStreamReader(new BOMInputStream(in)), format)) {

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