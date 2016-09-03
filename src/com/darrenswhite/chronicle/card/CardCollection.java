package com.darrenswhite.chronicle.card;

import com.darrenswhite.chronicle.player.Player;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author Darren White
 */
public class CardCollection {

	private static final Set<Card> cards = new HashSet<>();

	static {
		add(new Support.Builder()
				.name("Ali Morrisane")
				.cost(0)
				.reward(new Reward.Builder()
						.gold(3)
						.create()));
		add(new Creature.Builder()
				.name("Alpha Werewolf")
				.attack(8)
				.health(5)
				.effect(g -> g.getRival().dealDamage(g.getPlayer().isMortal() ? 8 : 4))
				.reward(new Reward.Builder()
						.gold(1)
						.create()));
		add(new Creature.Builder()
				.name("Commander Zilyana")
				.attack(7)
				.health(13)
				.reward(new Reward.Builder()
						.gold(7)
						.health(12)
						.create()));
		add(new Creature.Builder()
				.name("Count Draynor")
				.type(Card.Type.VAMPYRE)
				.attack(3)
				.health(5)
				.effect(g -> {
					Optional<Creature> next = g.getNextCard(c -> c instanceof Creature);
					next.ifPresent(c -> {
						Player p = g.getPlayer();
						int startHealth = p.getHealth();
						int endHealth = Math.min(startHealth + 4, p.getMaxHealth());
						int steal = endHealth - startHealth;

						c.setHealth(c.getHealth() - 4);
						g.getPlayer().setHealth(endHealth);
					});
				}));
		add(new Creature.Builder()
				.name("Dagannoth Sentinel")
				.type(Card.Type.BEAST)
				.attack(8)
				.health(9)
				.reward(new Reward.Builder()
						.base(1)
						.health(5)
						.create()));
		add(new Support.Builder()
				.name("Dragon Longsword")
				.type(Card.Type.EQUIPMENT)
				.cost(5)
				.reward(new Reward.Builder()
						.weapon(5, 3)
						.create()));
		add(new Support.Builder()
				.name("Dragon Scimitar")
				.type(Card.Type.EQUIPMENT)
				.cost(6)
				.effect(g -> g.getRival().removeHealth(4))
				.reward(new Reward.Builder()
						.weapon(5, 2)
						.create()));
		add(new Creature.Builder()
				.name("Gluttonous Behemoth")
				.type(Card.Type.BEAST)
				.attack(6)
				.health(19)
				.effect(g -> g.getPlayer().setHealth(g.getPlayer().getMaxHealth()))
				.reward(new Reward.Builder()
						.gold(3)
						.create()));
		add(new Support.Builder()
				.name("Granite Maul")
				.type(Card.Type.EQUIPMENT)
				.cost(5)
				.effect(g -> g.getRival().dealDamage(4))
				.reward(new Reward.Builder()
						.weapon(4, 2)
						.create()));
		add(new Creature.Builder()
				.name("Grotworm")
				.attack(5)
				.health(10)
				.reward(new Reward.Builder()
						.gold(7)
						.create()));
		add(new Creature.Builder()
				.name("Harold")
				.attack(4)
				.health(7)
				.effect(g -> {
					Optional<Creature> vamp = g.getNextCard(c ->
							c instanceof Creature &&
									c.getType().equals(Card.Type.VAMPYRE));
					vamp.ifPresent(c -> c.setAttack(c.getAttack() - 4));
				})
				.reward(new Reward.Builder()
						.gold(1)
						.health(2)
						.create()));
		add(new Creature.Builder()
				.name("Kalphite Soldier")
				.attack(2)
				.health(6)
				.type(Card.Type.KALPHITE)
				.reward(new Reward.Builder()
						.gold(3)
						.armour(2)
						.create()));
		add(new Support.Builder()
				.name("Mimic")
				.cost(0)
				.effect(g -> g.getPlayer().removeHealth(5))
				.reward(new Reward.Builder()
						.gold(4)
						.create()));
		add(new Creature.Builder()
				.name("Sergeant Grimspike")
				.type(Card.Type.GOBLIN)
				.attack(4)
				.health(10)
				.effect(g -> g.getRival().dealDamage(6))
				.reward(new Reward.Builder()
						.gold(2)
						.armour(2)
						.create()));
		add(new Creature.Builder()
				.name("Sergeant Slimetoes")
				.type(Card.Type.GOBLIN)
				.attack(2)
				.health(6)
				.reward(new Reward.Builder()
						.weapon(3, 2)
						.armour(2)
						.create()));
		add(new Support.Builder()
				.name("Shug")
				.type(Card.Type.ALLY)
				.cost(0)
				.effect(g -> {
					Optional<Creature> next = g.getNextCard(c -> c instanceof Creature);
					next.ifPresent(c -> {
						int health = c.getHealth();

						c.setHealth(c.getAttack());
						c.setAttack(health);
					});
				}));
		add(new Support.Builder()
				.name("Strength Potion")
				.type(Card.Type.POTION)
				.cost(4)
				.effect(g -> g.getPlayer().setTemporaryAttack(5)));
		add(new Creature.Builder()
				.name("Tenebra")
				.type(Card.Type.VAMPYRE)
				.attack(7)
				.health(13)
				.effect(g -> {
					Player p = g.getPlayer();
					int startHealth = p.getHealth();
					int endHealth = Math.min(startHealth + 8, p.getMaxHealth());
					int steal = endHealth - startHealth;

					g.getRival().removeHealth(steal);
					g.getPlayer().setHealth(endHealth);
				}));
		add(new Creature.Builder()
				.name("Tormented Demon")
				.type(Card.Type.DEMON)
				.attack(4)
				.health(10)
				.effect(g -> {
					g.getPlayer().dealDamage(4);
					g.getRival().dealDamage(4);
				})
				.reward(new Reward.Builder()
						.weapon(4, 2)
						.base(1)
						.create()));
		add(new Creature.Builder()
				.name("TzTok-Jad")
				.type(Card.Type.TZHAAR)
				.attack(15)
				.health(7)
				.reward(new Reward.Builder()
						.base(1)
						.armour(4)
						.create()));
		add(new Support.Builder()
				.name("Vampyre Power")
				.type(Card.Type.SPELL)
				.cost(13)
				.effect(g -> {
					g.getPlayer().setWeapon(null);
					g.getPlayer().setArmour(0);
					g.getPlayer().setMaxHealth(g.getPlayer().getMaxHealth() + 10);
					g.getPlayer().setHealth(g.getPlayer().getHealth() + 10);
				})
				.reward(new Reward.Builder()
						.base(3)
						.create()));
		add(new Support.Builder()
				.name("Worthy Opponent")
				.type(Card.Type.SPELL)
				.cost(0)
				.effect(g -> {
					Optional<Creature> next = g.getNextCard(c -> c instanceof Creature);

					next.ifPresent(c -> {
						if (c.getHealth() >= 10) {
							g.getPlayer().setBase(g.getPlayer().getBase() + 1);
						}
					});
				}));
		add(new Creature.Builder()
				.name("Yelps")
				.type(Card.Type.GOBLIN)
				.attack(4)
				.health(3)
				.effect(g -> {
					int gold = g.getPlayer().getGold();

					if (gold <= 8) {
						g.getPlayer().setGold(gold * 2);
					}
				}));
	}

	private static void add(CardBuilder builder) {
		cards.add(builder.create());
	}

	public static List<Card> findAll(Predicate<Card> predicate) {
		return cards.stream().filter(predicate).collect(Collectors.toList());
	}

	public static Optional<Card> findAny(Predicate<Card> predicate) {
		return cards.stream().filter(predicate).findAny();
	}
}