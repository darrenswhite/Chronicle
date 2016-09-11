package com.darrenswhite.chronicle.card;

import com.darrenswhite.chronicle.Game;
import com.darrenswhite.chronicle.player.Player;

import java.util.Set;

/**
 * @author Darren White
 */
public class Card implements Cloneable {

	public static final int TYPE_SUPPORT = 1;
	public static final int TYPE_CREATURE = 2;

	public static final int ARCHETYPE_ALL = 0;
	public static final int ARCHETYPE_LINZA = 1;
	public static final int ARCHETYPE_RAPTOR = 2;
	public static final int ARCHETYPE_ARIANE = 3;
	public static final int ARCHETYPE_OZAN = 4;
	public static final int ARCHETYPE_VANESCULA = 5;
	public static final int ARCHETYPE_MORVRAN = 6;

	public static final int FAMILY_NONE = 0;
	public static final int FAMILY_BEAST = 1;
	public static final int FAMILY_DEMON = 2;
	public static final int FAMILY_DRAGON = 3;
	public static final int FAMILY_LOCATION = 4;
	public static final int FAMILY_EQUIPMENT = 5;
	public static final int FAMILY_FAMILIAR = 6;
	public static final int FAMILY_GIANT = 7;
	public static final int FAMILY_GOBLIN = 8;
	public static final int FAMILY_KALPHITE = 9;
	public static final int FAMILY_OGRE = 10;
	public static final int FAMILY_ORK = 11;
	public static final int FAMILY_PIRATE = 12;
	public static final int FAMILY_POTION = 13;
	public static final int FAMILY_SHIP = 14;
	public static final int FAMILY_SLAYER_TASK = 15;
	public static final int FAMILY_SPELL = 16;
	public static final int FAMILY_TROLL = 17;
	public static final int FAMILY_TZHAAR = 18;
	public static final int FAMILY_UNDEAD = 19;
	public static final int FAMILY_ALLY = 20;
	public static final int FAMILY_ENEMY = 21;
	public static final int FAMILY_VAMPYRE = 22;
	public static final int FAMILY_ACTION = 23;

	public static final int REWARD_NONE = 0;
	public static final int REWARD_GOLD = 2;
	public static final int REWARD_HEALTH = 3;
	public static final int REWARD_BASE = 4;
	public static final int REWARD_ARMOUR = 5;
	public static final int REWARD_WEAPON = 6;

	public static final int RARITY_BASIC = 0;
	public static final int RARITY_SAPPHIRE = 0;
	public static final int RARITY_EMERALD = 1;
	public static final int RARITY_RUBY = 2;
	public static final int RARITY_DIAMOND = 3;

	public int id;
	public int nameid;
	public String name;
	public int archetype;
	public int type;
	public int family;
	public String image;
	public int attack;
	public int health;
	public int goldcost;
	public int reward0value0;
	public int reward0value1;
	public int reward0type;
	public int reward1value0;
	public int reward1value1;
	public int reward1type;
	public int reward2value0;
	public int reward2value1;
	public int reward2type;
	public int rarity;
	public int descid;
	public int effectdesc;
	public int source;
	public String artist;
	public int hitsplat;
	public boolean refund;
	public boolean aggressive;
	public Set<ConditionLink> conditions;
	public Set<ConsequenceLink> consequences;
	public Set<ConditionConsequenceLink> links;

	private void applyEffect(Game g) {
		if (consequences == null) {
			return;
		}

		if (conditions != null) {
			if (links != null) {
				for (ConditionConsequenceLink link : links) {
					//link.consequence_num
				}
			}

			for (ConditionLink c : conditions) {
				if (!c.apply(g)) {
					return;
				}
			}
		}

		for (ConsequenceLink c : consequences) {
			c.accept(g);
		}
	}

	private void applyReward(Player p, int type, int value0, int value1) {
		switch (type) {
			case REWARD_ARMOUR:
				p.armour += value0;
				break;
			case REWARD_BASE:
				p.base += value0;
				break;
			case REWARD_GOLD:
				p.gold += value0;
				break;
			case REWARD_HEALTH:
				p.health += value0;
				break;
			case REWARD_WEAPON:
				p.weaponAttack += value0;
				p.weaponDurability += value1;
				break;
		}
	}

	public Card createClone() {
		try {
			return (Card) super.clone();
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}

	public void encounter(Game g) {
		Player p = g.getPlayer();

		switch (type) {
			case TYPE_CREATURE:
				encounterCreature(p);
				break;
			case TYPE_SUPPORT:
				encounterSupport(p);
				break;
		}

		if (p.health <= 0) {
			return;
		}

		applyEffect(g);
		applyReward(p, reward0type, reward0value0 + 1, reward0value1 + 1);
		applyReward(p, reward1type, reward1value0 + 1, reward1value1 + 1);
		applyReward(p, reward2type, reward2value0 + 1, reward2value1 + 1);
	}

	private void encounterCreature(Player p) {
		int armour, hit;
		int hits = 0;

		while (health > 0) {
			if (aggressive || hits > 0) {
				p.dealDamage(attack);

				if (p.health <= 0) {
					return;
				}
			}

			health -= p.getTotalAttack();

			p.updateWeapon();

			hits++;
		}
	}

	private void encounterSupport(Player p) {
		if (p.gold >= goldcost) {
			p.gold -= goldcost;
		}
	}

	@Override
	public String toString() {
		return "Card{" +
				"id=" + id +
				", nameid=" + nameid +
				", name='" + name + '\'' +
				", archetype=" + archetype +
				", type=" + type +
				", family=" + family +
				", image=" + image +
				", attack=" + attack +
				", health=" + health +
				", goldcost=" + goldcost +
				", reward0value0=" + reward0value0 +
				", reward0value1=" + reward0value1 +
				", reward0type=" + reward0type +
				", reward1value0=" + reward1value0 +
				", reward1value1=" + reward1value1 +
				", reward1type=" + reward1type +
				", reward2value0=" + reward2value0 +
				", reward2value1=" + reward2value1 +
				", reward2type=" + reward2type +
				", rarity=" + rarity +
				", descid=" + descid +
				", effectdesc=" + effectdesc +
				", source=" + source +
				", artist=" + artist +
				", hitsplat=" + hitsplat +
				", refund=" + refund +
				", aggressive=" + aggressive +
				'}';
	}
}