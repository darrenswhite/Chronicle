package com.darrenswhite.chronicle.card;

import com.darrenswhite.chronicle.Game;

/**
 * @author Darren White
 */
public class Support implements Card {

	private final Builder builder;
	private final String name;
	private final Type type;
	private final Rarity rarity;
	private final Legend legend;
	private final Effect effect;
	private final Reward reward;
	private int cost;

	public Support(Builder builder, String name, Type type, Rarity rarity,
	               Legend legend, int cost, Effect effect, Reward reward) {
		this.builder = builder;
		this.name = name;
		this.type = type;
		this.rarity = rarity;
		this.legend = legend;
		this.cost = cost;
		this.effect = effect;
		this.reward = reward;
	}

	@Override
	public void encounter(Game g) {
		if (g.getPlayer().getGold() >= cost) {
			g.getPlayer().setGold(g.getPlayer().getGold() - cost);
			getEffect().apply(g);
			getReward().apply(g);
		}
	}

	@Override
	public Builder getBuilder() {
		return builder;
	}

	public int getCost() {
		return cost;
	}

	@Override
	public Effect getEffect() {
		return effect;
	}

	@Override
	public Legend getLegend() {
		return legend;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Rarity getRarity() {
		return rarity;
	}

	@Override
	public Reward getReward() {
		return reward;
	}

	@Override
	public Type getType() {
		return type;
	}

	public void setCost(int cost) {
		this.cost = cost;
	}

	@Override
	public String toString() {
		return "Support{" +
				"name='" + name + '\'' +
				", type=" + type +
				", effect=" + effect +
				", reward=" + reward +
				", cost=" + cost +
				'}';
	}

	public static class Builder implements CardBuilder {

		private String name;
		private Type type = Type.NONE;
		private Rarity rarity = null;
		private Legend legend = Legend.ALL;
		private Effect effect = Effect.none();
		private Reward reward = Reward.NONE;
		private int cost = -1;

		public Builder cost(int cost) {
			this.cost = cost;
			return this;
		}

		@Override
		public Support create() {
			if (name == null) {
				throw new IllegalArgumentException("Support must have a name!");
			}

			if (cost == -1) {
				throw new IllegalArgumentException("Support must have a cost!");
			}

			if (rarity == null) {
				throw new IllegalArgumentException("Support must have a rarity!");
			}

			return new Support(this, name, type, rarity, legend, cost, effect, reward);
		}

		@Override
		public Builder effect(Effect effect) {
			this.effect = effect;
			return this;
		}

		@Override
		public Builder legend(Legend legend) {
			this.legend = legend;
			return this;
		}

		@Override
		public Builder name(String name) {
			this.name = name;
			return this;
		}

		@Override
		public Builder rarity(Rarity rarity) {
			this.rarity = rarity;
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