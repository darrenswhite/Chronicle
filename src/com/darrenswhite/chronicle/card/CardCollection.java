package com.darrenswhite.chronicle.card;

import com.darrenswhite.chronicle.io.CSVReader;

import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author Darren White
 */
public class CardCollection {

	private static CardCollection instance;
	private final Set<Card> cards;
	private final Set<Effect> effects;
	private final Set<EffectCondition> conditions;
	private final Set<EffectConsequence> consequences;
	private final Set<ConditionLink> conditionLinks;
	private final Set<ConditionConsequenceLink> conditionConsequenceLinks;
	private final Set<ConsequenceLink> consequenceLinks;

	private CardCollection() {
		cards = new CSVReader<>(Paths.get("csv/cards.csv"), Card.class).parse();
		effects = new CSVReader<>(Paths.get("csv/effects.csv"), Effect.class).parse();
		conditions = new CSVReader<>(Paths.get("csv/effectconditions.csv"), EffectCondition.class).parse();
		consequences = new CSVReader<>(Paths.get("csv/effectconsequences.csv"), EffectConsequence.class).parse();
		conditionLinks = new CSVReader<>(Paths.get("csv/conditionlink.csv"), ConditionLink.class).parse();
		conditionConsequenceLinks = new CSVReader<>(Paths.get("csv/conditiontoconsequencelink.csv"), ConditionConsequenceLink.class).parse();
		consequenceLinks = new CSVReader<>(Paths.get("csv/consequencelink.csv"), ConsequenceLink.class).parse();

		init();
	}

	private void add(CardBuilder builder) {
		cards.add(builder.create());
	}

	public List<Card> findAll(Predicate<Card> filter) {
		return cards.stream().filter(filter).collect(Collectors.toList());
	}

	public Optional<Card> findAny(Predicate<Card> filter) {
		return cards.stream().filter(filter).findAny();
	}

	private Set<ConditionLink> getConditions(Card card) {
		return conditionLinks.stream().filter(c -> c.card_id == card.id).collect(Collectors.toSet());
	}

	private Set<ConsequenceLink> getConsequences(Card card) {
		return consequenceLinks.stream().filter(c -> c.card_id == card.id).collect(Collectors.toSet());
	}

	public static CardCollection getInstance() {
		return instance != null ? instance : (instance = new CardCollection());
	}

	private Set<ConditionConsequenceLink> getLinks(Card card) {
		return conditionConsequenceLinks.stream().filter(c -> c.card_id == card.id).collect(Collectors.toSet());
	}

	public void init() {
		for (Card card : cards) {
			card.conditions = getConditions(card);
			card.consequences = getConsequences(card);
			card.links = getLinks(card);
		}
	}

	private void printCardData(Card card) {
		System.out.println(card.name);

		Optional<ConditionConsequenceLink> conditionConsequenceLink = conditionConsequenceLinks.stream().filter(c -> c.card_id == card.id).findFirst();
		List<ConditionLink> conditionLink = conditionLinks.stream().filter(c -> c.card_id == card.id).collect(Collectors.toList());
		List<ConsequenceLink> consequenceLink = consequenceLinks.stream().filter(c -> c.card_id == card.id).collect(Collectors.toList());

		conditionConsequenceLink.ifPresent(System.out::println);

		System.out.println();
		System.out.println("[ CONDITION LINKS ]");

		for (ConditionLink cl : conditionLink) {
			Optional<EffectCondition> effectCondition = conditions.stream().filter(c -> c.id == cl.condition_id).findFirst();
			// Optional<Effect> effect0 = effects.stream().filter(e -> e.condition0 == cl.condition_id).findFirst();
			// Optional<Effect> effect1 = effects.stream().filter(e -> e.condition1 == cl.condition_id).findFirst();
			System.out.println(cl);
			effectCondition.ifPresent(System.out::println);
			// effect0.ifPresent(System.out::println);
			// effect1.ifPresent(System.out::println);
			System.out.println();
		}

		System.out.println("[ CONSEQUENCE LINKS ]");

		for (ConsequenceLink cl : consequenceLink) {
			Optional<EffectConsequence> effectConsequence = consequences.stream().filter(c -> c.id == cl.consequence_id).findFirst();
			// Optional<Effect> effect0 = effects.stream().filter(e -> e.consequence0 == cl.consequence_id).findFirst();
			// Optional<Effect> effect1 = effects.stream().filter(e -> e.consequence1 == cl.consequence_id).findFirst();
			System.out.println(cl);
			effectConsequence.ifPresent(System.out::println);
			// effect0.ifPresent(System.out::println);
			// effect1.ifPresent(System.out::println);
			System.out.println();
		}
	}
}