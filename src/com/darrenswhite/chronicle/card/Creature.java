package com.darrenswhite.chronicle.card;

import com.darrenswhite.chronicle.Game;
import com.darrenswhite.chronicle.player.Player;

/**
 * @author Darren White
 */
public class Creature implements Card {

	private final Builder builder;
	private final String name;
	private final Type type;
	private final Effect effect;
	private final Reward reward;
	private int attack;
	private int health;
	private boolean aggressive;

	private Creature(Builder builder, String name, Type type, Effect effect, Reward reward, int attack, int health, boolean aggressive) {
		this.builder = builder;
		this.name = name;
		this.type = type;
		this.effect = effect;
		this.reward = reward;
		this.attack = attack;
		this.health = health;
		this.aggressive = aggressive;
	}

	@Override
	public void encounter(Game g) {
		int wAtk;
		int wDur;
		int totalAtk;
		int armour, hit;
		int hits = 0;
		Player p = g.getPlayer();

		while (health > 0) {
			Weapon w = p.getWeapon();
			wAtk = w != null ? w.getAttack() : 0;
			wDur = w != null ? w.getDurability() : 0;
			totalAtk = p.getBase() + wAtk;

			if (aggressive || hits > 0) {
				p.dealDamage(attack);

				if (p.getHealth() == 0) {
					return;
				}
			}

			health -= totalAtk;

			if (wDur > 0) {
				w.setDurability(wDur - 1);
			}

			if (w != null && w.getDurability() == 0) {
				p.setWeapon(null);
			}

			hits++;
		}

		getEffect().apply(g);
		getReward().apply(g);
	}

	public int getAttack() {
		return attack;
	}

	@Override
	public Builder getBuilder() {
		return builder;
	}

	@Override
	public Effect getEffect() {
		return effect;
	}

	public int getHealth() {
		return health;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Reward getReward() {
		return reward;
	}

	@Override
	public Type getType() {
		return type;
	}

	public boolean isAggressive() {
		return aggressive;
	}

	public void setAggressive(boolean aggressive) {
		this.aggressive = aggressive;
	}

	public void setAttack(int attack) {
		this.attack = attack;
	}

	public void setHealth(int health) {
		this.health = health;
	}

	@Override
	public String toString() {
		return "Creature{" +
				"name='" + name + '\'' +
				", type=" + type +
				", effect=" + effect +
				", reward=" + reward +
				", attack=" + attack +
				", health=" + health +
				", aggressive=" + aggressive +
				'}';
	}

	public static class Builder implements CardBuilder {

		public static final Builder instance = new Builder();
		private String name;
		private Type type = Type.NONE;
		private Effect effect = Effect.none();
		private Reward reward = Reward.NONE;
		private int attack = -1;
		private int health = -1;
		private boolean aggressive = false;

		public Builder aggressive(boolean aggressive) {
			this.aggressive = aggressive;
			return this;
		}

		public Builder attack(int attack) {
			this.attack = attack;
			return this;
		}

		@Override
		public Creature create() {
			if (name == null) {
				throw new IllegalArgumentException("Creature must have a name!");
			}

			if (attack == -1 || health == -1) {
				throw new IllegalArgumentException("Creature must have attack and health values!");
			}

			return new Creature(this, name, type, effect, reward, attack, health, aggressive);
		}

		@Override
		public Builder effect(Effect effect) {
			this.effect = effect;
			return this;
		}

		public Builder health(int health) {
			this.health = health;
			return this;
		}

		@Override
		public Builder name(String name) {
			this.name = name;
			return this;
		}

		@Override
		public Builder reward(Reward reward) {
			this.reward = reward;
			return this;
		}

		@Override
		public Builder type(Type type) {
			this.type = type;
			return this;
		}
	}
}