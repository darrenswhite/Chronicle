package com.darrenswhite.chronicle.card;

import com.darrenswhite.chronicle.Game;
import com.darrenswhite.chronicle.config.ConfigTemplate;
import com.darrenswhite.chronicle.effect.Effect;
import com.darrenswhite.chronicle.equipment.Weapon;
import com.darrenswhite.chronicle.player.Player;
import com.darrenswhite.chronicle.rewards.Reward;
import com.darrenswhite.chronicle.rewards.StatReward;
import com.darrenswhite.chronicle.rewards.WeaponReward;
import com.darrenswhite.chronicle.stats.Healable;
import org.apache.commons.csv.CSVRecord;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author Darren White
 */
public class Card extends ConfigTemplate implements Cloneable, Healable {

	private final List<Reward> rewards = new LinkedList<>();
	private final int id;
	private final String name;
	private final Legend legend;
	private final Type type;
	private final Family family;
	private final Rarity rarity;
	private final Source source;
	private final boolean aggressive;
	private final Effect effect;
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
		health = parseInt(record.get(headers.get("health")));
		goldCost = parseInt(record.get(headers.get("goldcost")));
		parseReward("reward0type", record.get(headers.get("reward0type")),
				"reward0value0", record.get(headers.get("reward0value0")),
				"reward0value1", record.get(headers.get("reward0value1")));
		parseReward("reward1type", record.get(headers.get("reward1type")),
				"reward1value0", record.get(headers.get("reward1value0")),
				"reward1value1", record.get(headers.get("reward1value1")));
		parseReward("reward2type", record.get(headers.get("reward2type")),
				"reward2value0", record.get(headers.get("reward2value0")),
				"reward2value1", record.get(headers.get("reward2value1")));
		rarity = parseEnum(Rarity.class, record.get(headers.get("rarity")));
		source = parseEnum(Source.class, record.get(headers.get("source")));
		aggressive = parseBoolean(record.get(headers.get("aggressive")));
		effect = Effect.create(id);
	}

	public Card copy() {
		try {
			return (Card) super.clone();
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}

	public void encounter(Game g) {
		Player p = g.getPlayer();

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
		int armour, hit;
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
		Player p = g.getPlayer();

		if (p.gold < goldCost) {
			return false;
		}

		p.gold -= goldCost;

		return true;
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

	public Legend getLegend() {
		return legend;
	}

	@Override
	public int getMaxHealth() {
		return Integer.MAX_VALUE;
	}

	public String getName() {
		return name;
	}

	public Rarity getRarity() {
		return rarity;
	}

	public Source getSource() {
		return source;
	}

	public Type getType() {
		return type;
	}

	public boolean isAggressive() {
		return aggressive;
	}

	private void parseReward(String rewardTypeHeader, String rewardTypeString, String reward0Header, String reward0String, String reward1Header, String reward1String) {
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
	public void setHealth(int health) {
		this.health = health;
	}

	@Override
	public String toString() {
		return name;
	}

	public enum Family {

		NONE,
		BEAST,
		DEMON,
		DRAGON,
		LOCATION,
		EQUIPMENT,
		FAMILIAR,
		GIANT,
		GOBLIN,
		KALPHITE,
		OGRE,
		ORK,
		PIRATE,
		POTION,
		SHIP,
		SLAYER_TASK,
		SPELL,
		TROLL,
		TZHAAR,
		UNDEAD,
		ALLY,
		MAHJARRAT,
		VAMPYRE,
		ACTION,
	}

	public enum Legend {

		ALL,
		LINZA,
		RAPTOR,
		ARIANE,
		OZAN,
		VANESCULA,
		MORVRAN,
	}

	public enum Rarity {

		BASIC,
		SAPPHIRE,
		EMERALD,
		RUBY,
		DIAMOND
	}

	public enum Source {

		NONE,
		STARTER,
		UNLOCK,
		PURCHASE,
		FROM_EFFECT,
		PAGE_CARD,
	}

	public enum Type {

		OPEN_ROAD,
		NONCOMBAT,
		COMBAT,
		ANY,
	}
}