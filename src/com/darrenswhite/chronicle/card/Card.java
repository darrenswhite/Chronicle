package com.darrenswhite.chronicle.card;

import com.darrenswhite.chronicle.config.ConfigTemplate;
import com.darrenswhite.chronicle.effect.*;
import com.darrenswhite.chronicle.equipment.Weapon;
import com.darrenswhite.chronicle.game.Game;
import com.darrenswhite.chronicle.player.Player;
import com.darrenswhite.chronicle.rewards.Reward;
import com.darrenswhite.chronicle.rewards.StatReward;
import com.darrenswhite.chronicle.rewards.WeaponReward;
import org.apache.commons.csv.CSVRecord;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author Darren White
 */
public class Card extends ConfigTemplate implements IEffectTarget {

	private final List<Reward> rewards = new LinkedList<>();
	private final int id;
	private final String name;
	private final Legend legend;
	private final Type type;
	private final Family family;
	private final Rarity rarity;
	private final Source source;
	private final Effect effect;
	private final int initialHealth;
	private boolean aggressive;
	private int attack;
	private int health;
	private int goldCost;

	public Card(Map<String, Integer> headers, CSVRecord record) {
		super(headers, record);
		id = parseInt(record.get(headers.get("id")));
		name = record.get(headers.get("name"));
		legend = parseEnum(Legend.class, record.get(headers.get("archetype")));
		type = parseEnum(Type.class, record.get(headers.get("type")));
		family = parseEnum(Family.class, record.get(headers.get("family")));
		attack = parseInt(record.get(headers.get("attack")));
		initialHealth = health = parseInt(record.get(headers.get("health")));
		goldCost = parseInt(record.get(headers.get("goldcost")));
		parseReward(record.get(headers.get("reward0type")),
				record.get(headers.get("reward0value0")),
				record.get(headers.get("reward0value1")));
		parseReward(record.get(headers.get("reward1type")),
				record.get(headers.get("reward1value0")),
				record.get(headers.get("reward1value1")));
		parseReward(record.get(headers.get("reward2type")),
				record.get(headers.get("reward2value0")),
				record.get(headers.get("reward2value1")));
		rarity = parseEnum(Rarity.class, record.get(headers.get("rarity")));
		source = parseEnum(Source.class, record.get(headers.get("source")));
		aggressive = parseBoolean(record.get(headers.get("aggressive")));
		effect = Effect.create(id);
	}

	@Override
	public void applyToProperty(Game g, EffectProperty property, EffectAction action, List<CardPredicate> predicates, int value, int value2) {
		switch (property) {
			case ATTACK:
				switch (action) {
					case SET:
						attack = value;
						break;
					case ADD:
						attack += value;
						break;
					case REMOVE:
						attack -= value;
						break;
				}
				break;
			case HEALTH:
				switch (action) {
					case SET:
						health = value;
						break;
					case ADD:
						health += value;
						break;
					case REMOVE:
						health -= value;
						break;
				}
				break;
			case EFFECT_EXHAUST:
				if (effect != null) {
					effect.removeProperty(EffectProperty.EXHAUST);
				}
				break;
			case AGGRESSIVE:
				switch (action) {
					case SET:
						aggressive = value > 0;
						break;
					case ADD:
						aggressive = true;
						break;
					case REMOVE:
						aggressive = false;
						break;
				}
				break;
			case COST:
				switch (action) {
					case SET:
						goldCost = value;
						break;
					case ADD:
						goldCost += value;
						break;
					case REMOVE:
						goldCost -= value;
						break;
				}
				break;
		}
	}

	@Override
	public void applyToProperty(Game g, EffectProperty property, EffectAction action, List<CardPredicate> predicates, Weapon weapon) {
	}

	public Card copy() {
		return new Card(getHeaders(), getRecord());
	}

	public void encounter(Game g) {
		switch (type) {
			case COMBAT:
				if (!encounterCreature(g)) {
					return;
				}
				break;
			case NONCOMBAT:
				if (!encounterSupport(g)) {
					return;
				}
				break;
			default:
				return;
		}

		if (effect != null) {
			effect.apply(g);
		}

		for (Reward r : rewards) {
			r.apply(g.getPlayer());
		}
	}

	private boolean encounterCreature(Game g) {
		Player p = g.getPlayer();
		int hits = 0;

		while (health > 0) {
			if (aggressive || hits > 0) {
				p.dealDamage(attack);

				if (p.getHealth() <= 0) {
					return false;
				}
			}

			health -= p.getTotalAttack();

			p.updateWeapon();

			hits++;
		}

		return true;
	}

	private boolean encounterSupport(Game g) {
		return g.getPlayer().spendGold(goldCost);
	}

	public static CardPredicate generatePredicate(CardPredicateType type, EffectEvalInt operand, int value) {
		Predicate<Card> predicate;

		switch (type) {
			case SPECIFIC:
				predicate = c -> c.getType() != Type.OPEN_ROAD && (value <= 0 || c.getId() == value);
				break;
			case TYPE:
				Type cardType = Type.values()[value];
				predicate = c -> cardType == Type.ANY || c.getType() == cardType;
				break;
			case FAMILY:
				Family family = Family.values()[value];
				predicate = c -> c.getType() != Type.OPEN_ROAD && (family == Family.NONE || c.getFamily() == family);
				break;
			case ATTACK:
				predicate = c -> c.getType() == Type.COMBAT && Effect.intAssessment(c.getAttack(), operand, value);
				break;
			case HEALTH:
				predicate = c -> c.getType() == Type.COMBAT && Effect.intAssessment(c.getInitialHealth(), operand, value);
				break;
			case COST:
				predicate = c -> c.getType() == Type.NONCOMBAT && Effect.intAssessment(c.getGoldCost(), operand, value);
				break;
			case REWARD_ATTACK:
			case REWARD_HEALTH:
			case REWARD_GOLD:
			case REWARD_ARMOUR:
				predicate = c -> {
					if (c.rewards != null && c.rewards.size() > 0) {
						for (Reward reward : c.rewards) {
							if (reward.getType() == getRewardTypeFromPredicate(type) && Effect.intAssessment(reward.getStat(), operand, value)) {
								return true;
							}
						}
					}
					return false;
				};
				break;
			case REWARD_WEAPON_DURABILITY:
				predicate = c -> {
					if (c.rewards != null && c.rewards.size() > 0) {
						for (Reward reward : c.rewards) {
							if (reward.getType() == Reward.Type.WEAPON && Effect.intAssessment(reward.getWeapon().durability, operand, value)) {
								return true;
							}
						}
					}
					return false;
				};
				break;
			case REWARD_WEAPON_ATTACK:
				predicate = c -> {
					if (c.rewards != null && c.rewards.size() > 0) {
						for (Reward reward : c.rewards) {
							if (reward.getType() == Reward.Type.WEAPON && Effect.intAssessment(reward.getWeapon().attack, operand, value)) {
								return true;
							}
						}
					}
					return false;
				};
				break;
			case AGGRESSIVE:
				predicate = c -> {
					if (!c.aggressive) {
						return value == 0;
					}
					return value == 1;
				};
				break;
			default:
				predicate = c -> c.getType() != Type.OPEN_ROAD;
				break;
		}
		return new CardPredicate(type, predicate);
	}

	public int getAttack() {
		return attack;
	}

	public Family getFamily() {
		return family;
	}

	public int getGoldCost() {
		return goldCost;
	}

	public int getHealth() {
		return health;
	}

	public int getId() {
		return id;
	}

	public int getInitialHealth() {
		return initialHealth;
	}

	public Legend getLegend() {
		return legend;
	}

	public String getName() {
		return name;
	}

	@Override
	public int getPropertyValue(EffectProperty property, List<CardPredicate> cardPredicates, int maxValue) {
		if (cardPredicates != null && cardPredicates.size() > 0) {
			for (CardPredicate cardPredicate : cardPredicates) {
				if (cardPredicate.getType() != CardPredicateType.NONE && cardPredicate.getPredicate() != null && !cardPredicate.test(this)) {
					return 0;
				}
			}
		}

		int val2 = 0;

		switch (property) {
			case HEALTH:
				if (getType() == Type.COMBAT) {
					val2 = health;
				} else {
					val2 = 0;
				}
				break;
			case MAX_HEALTH:
				val2 = -1;
				break;
			case ATTACK:
				if (getType() == Type.COMBAT) {
					val2 = attack;
				} else {
					val2 = 0;
				}
				break;
			case COST:
				if (getType() == Type.NONCOMBAT) {
					val2 = goldCost;
				} else {
					val2 = 0;
				}
				break;
			case REWARD_ATTACK:
				if (rewards != null) {
					Optional<Reward> reward = rewards.stream().filter(c -> c.getType() == Reward.Type.ATTACK).findFirst();
					if (reward.isPresent()) {
						val2 = reward.get().getStat();
					}
				}
				break;
			case REWARD_GOLD:
				if (rewards != null) {
					Optional<Reward> reward = rewards.stream().filter(c -> c.getType() == Reward.Type.GOLD).findFirst();
					if (reward.isPresent()) {
						val2 = reward.get().getStat();
					}
				}
				break;
			case REWARD_HEALTH:
				if (rewards != null) {
					Optional<Reward> reward = rewards.stream().filter(c -> c.getType() == Reward.Type.HEALTH).findFirst();
					if (reward.isPresent()) {
						val2 = reward.get().getStat();
					}
				}
				break;
			case REWARD_EQUIPMENT_COUNT:
				if (rewards != null) {
					List<Reward> all = rewards.stream().filter(x ->
							x.getType() == Reward.Type.WEAPON || x.getType() == Reward.Type.ARMOUR).collect(Collectors.toList());
					if (all != null && all.size() > 0) {
						val2 = all.size();
					}
				}
				break;
			case REWARD_ARMOUR_COUNT:
				if (rewards != null) {
					List<Reward> all = rewards.stream().filter(x -> x.getType() == Reward.Type.ARMOUR).collect(Collectors.toList());
					if (all != null && all.size() > 0) {
						val2 = all.size();
					}
				}
				break;
			case REWARD_ARMOUR:
				if (rewards != null) {
					Optional<Reward> reward = rewards.stream().filter(c -> c.getType() == Reward.Type.ARMOUR).findFirst();
					if (reward != null) {
						Weapon weapon = reward.get().getWeapon();
						if (weapon != null) {
							val2 = weapon.durability;
						}
					}
				}
				break;
			case REWARD_WEAPON_COUNT:
				if (rewards != null) {
					List<Reward> all = rewards.stream().filter(x -> x.getType() == Reward.Type.WEAPON).collect(Collectors.toList());
					if (all != null && all.size() > 0) {
						val2 = all.size();
					}
				}
				break;
			case REWARD_WEAPON_ATTACK:
				if (rewards != null) {
					Optional<Reward> reward = rewards.stream().filter(c -> c.getType() == Reward.Type.WEAPON).findFirst();
					if (reward != null) {
						Weapon weapon = reward.get().getWeapon();
						if (weapon != null) {
							val2 = weapon.attack;
						}
					}
				}
				break;
			case REWARD_WEAPON_DURABILITY:
				if (rewards != null) {
					Optional<Reward> reward = rewards.stream().filter(c -> c.getType() == Reward.Type.WEAPON).findFirst();
					if (reward != null) {
						Weapon weapon = reward.get().getWeapon();
						if (weapon != null) {
							val2 = weapon.durability;
						}
					}
				}
				break;
			case CARD_ID:
				val2 = id;
				break;
			case AGGRESSIVE:
				val2 = aggressive ? 1 : 0;
				break;
			case COUNT:
				val2 = 1;
				break;
			case EFFECT_EXHAUST:
				val2 = 0;
				if (effect != null) {
					for (EffectConsequence consequence : effect.getConsequences()) {
						if (consequence.getTargetProperty() == EffectProperty.EXHAUST) {
							val2 = 1;
						}
					}
				}
				break;
			default:
				val2 = 0;
				break;
		}

		if (maxValue <= 0) {
			return val2;
		}

		return Math.min(maxValue, val2);
	}

	public Rarity getRarity() {
		return rarity;
	}

	public static Reward.Type getRewardTypeFromPredicate(CardPredicateType type) {
		switch (type) {
			case REWARD_ATTACK:
				return Reward.Type.ATTACK;
			case REWARD_HEALTH:
				return Reward.Type.HEALTH;
			case REWARD_GOLD:
				return Reward.Type.GOLD;
			case REWARD_ARMOUR:
				return Reward.Type.ARMOUR;
			case REWARD_WEAPON_DURABILITY:
				return Reward.Type.WEAPON;
			case REWARD_WEAPON_ATTACK:
				return Reward.Type.WEAPON;
			default:
				return Reward.Type.NONE;
		}
	}

	public Source getSource() {
		return source;
	}

	public Type getType() {
		return type;
	}

	@Override
	public Weapon getWeapon() {
		return null;
	}

	public boolean isAggressive() {
		return aggressive;
	}

	private void parseReward(String rewardTypeString, String reward0String, String reward1String) {
		if (rewardTypeString.isEmpty() || reward0String.isEmpty()) {
			return;
		}

		int type = parseInt(rewardTypeString);
		int amount1 = parseInt(reward0String);
		int amount2 = parseInt(reward1String);
		Reward reward;

		switch (type) {
			case 2:
				reward = new StatReward(StatReward.Type.GOLD, amount1 + 1);
				break;
			case 3:
				reward = new StatReward(StatReward.Type.HEALTH, amount1 + 1);
				break;
			case 4:
				reward = new StatReward(StatReward.Type.ATTACK, amount1 + 1);
				break;
			case 5:
				reward = new StatReward(StatReward.Type.ARMOUR, amount1 + 1);
				break;
			case 6:
				reward = new WeaponReward(new Weapon(amount1 + 1, amount2 + 1));
				break;
			default:
				return;
		}

		rewards.add(reward);
	}

	public void removeHealth(int amount) {
		if (type == Type.COMBAT) {
			health -= amount;
		}
	}

	@Override
	public String toString() {
		return name;
	}
}