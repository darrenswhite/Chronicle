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
				.rarity(Card.Rarity.BASIC)
				.cost(0)
				.reward(new Reward.Builder()
						.gold(3)
						.create()));
		add(new Creature.Builder()
				.name("Alpha Werewolf")
				.rarity(Card.Rarity.BASIC)
				.legend(Card.Legend.VANESCULA)
				.attack(8)
				.health(5)
				.effect(g -> g.getRival().dealDamage(g.getPlayer().isMortal() ? 8 : 4))
				.reward(new Reward.Builder()
						.gold(1)
						.create()));
		add(new Support.Builder()
				.name("Barker Toad")
				.rarity(Card.Rarity.RUBY)
				.type(Card.Type.FAMILIAR)
				.cost(1)
				.effect(g -> {
					Optional<Card> cannonball = g.getPlayer().getCard(c -> c.getName().equals("Cannonball"));

					cannonball.ifPresent(c -> {
						g.getPlayer().removeCard(c);
						g.getRival().dealDamage(7);
					});
				}));
		add(new Support.Builder()
				.name("Cannonball")
				.rarity(Card.Rarity.SAPPHIRE)
				.type(Card.Type.EQUIPMENT)
				.cost(0)
				.effect(g -> g.getRival().dealDamage(1)));
		add(new Creature.Builder()
				.name("Commander Zilyana")
				.rarity(Card.Rarity.RUBY)
				.attack(7)
				.health(13)
				.reward(new Reward.Builder()
						.gold(7)
						.health(12)
						.create()));
		add(new Creature.Builder()
				.name("Corporeal Beast")
				.rarity(Card.Rarity.DIAMOND)
				.legend(Card.Legend.RAPTOR)
				.type(Card.Type.BEAST)
				.attack(7)
				.health(14)
				.effect(g -> g.getPlayer().setArmour(g.getPlayer().getArmour() + g.getPlayer().getHealth())));
		add(new Creature.Builder()
				.name("Count Draynor")
				.rarity(Card.Rarity.RUBY)
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
				.rarity(Card.Rarity.BASIC)
				.type(Card.Type.BEAST)
				.attack(8)
				.health(9)
				.reward(new Reward.Builder()
						.base(1)
						.health(5)
						.create()));
		add(new Support.Builder()
				.name("Destroy")
				.rarity(Card.Rarity.EMERALD)
				.legend(Card.Legend.RAPTOR)
				.type(Card.Type.SPELL)
				.cost(0)
				.effect(g -> {
					if (g.getPlayer().getArmour() < 6) {
						return;
					}

					g.getPlayer().setArmour(g.getPlayer().getArmour() - 6);
					Optional<Creature> next = g.getNextCard(c -> c instanceof Creature);

					next.ifPresent(c -> c.setHealth(0));
				}));
		add(new Support.Builder()
				.name("Dondakan's Cannon")
				.rarity(Card.Rarity.BASIC)
				.legend(Card.Legend.RAPTOR)
				.type(Card.Type.EQUIPMENT)
				.cost(2)
				.effect(g -> g.getRival().dealDamage(g.getPlayer().getArmour() / 2)));
		add(new Support.Builder()
				.name("Dragon Longsword")
				.rarity(Card.Rarity.BASIC)
				.type(Card.Type.EQUIPMENT)
				.cost(5)
				.reward(new Reward.Builder()
						.weapon(5, 3)
						.create()));
		add(new Support.Builder()
				.name("Dragon Scimitar")
				.rarity(Card.Rarity.SAPPHIRE)
				.type(Card.Type.EQUIPMENT)
				.cost(6)
				.effect(g -> g.getRival().removeHealth(4))
				.reward(new Reward.Builder()
						.weapon(5, 2)
						.create()));
		add(new Support.Builder()
				.name("Fight Cauldron")
				.rarity(Card.Rarity.BASIC)
				.legend(Card.Legend.RAPTOR)
				.type(Card.Type.LOCATION)
				.cost(0)
				.effect(g -> g.getPlayer().removeHealth(8))
				.reward(new Reward.Builder()
						.armour(7)
						.gold(2)
						.create()));
		add(new Support.Builder()
				.name("Frenzy")
				.rarity(Card.Rarity.EMERALD)
				.legend(Card.Legend.RAPTOR)
				.type(Card.Type.SPELL)
				.cost(0)
				.effect(g -> {
					g.getPlayer().removeHealth(2);
					Optional<Creature> next = g.getNextCard(c -> c instanceof Creature);

					next.ifPresent(c -> c.setHealth(c.getHealth() - g.getPlayer().getArmour() / 2));
				}));
		add(new Creature.Builder()
				.name("Gluttonous Behemoth")
				.rarity(Card.Rarity.RUBY)
				.type(Card.Type.BEAST)
				.attack(6)
				.health(19)
				.effect(g -> g.getPlayer().setHealth(g.getPlayer().getMaxHealth()))
				.reward(new Reward.Builder()
						.gold(3)
						.create()));
		add(new Support.Builder()
				.name("Granite Maul")
				.rarity(Card.Rarity.RUBY)
				.type(Card.Type.EQUIPMENT)
				.cost(5)
				.effect(g -> g.getRival().dealDamage(4))
				.reward(new Reward.Builder()
						.weapon(4, 2)
						.create()));
		add(new Creature.Builder()
				.name("Grotworm")
				.rarity(Card.Rarity.BASIC)
				.attack(5)
				.health(10)
				.reward(new Reward.Builder()
						.gold(7)
						.create()));
		add(new Creature.Builder()
				.name("Harold")
				.rarity(Card.Rarity.EMERALD)
				.legend(Card.Legend.VANESCULA)
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
				.rarity(Card.Rarity.SAPPHIRE)
				.attack(2)
				.health(6)
				.type(Card.Type.KALPHITE)
				.reward(new Reward.Builder()
						.gold(3)
						.armour(2)
						.create()));
		add(new Creature.Builder()
				.name("KGP Agent")
				.rarity(Card.Rarity.SAPPHIRE)
				.type(Card.Type.BEAST)
				.health(2)
				.attack(1)
				.reward(new Reward.Builder()
						.base(1)
						.create()));
		add(new Support.Builder()
				.name("Mimic")
				.rarity(Card.Rarity.BASIC)
				.cost(0)
				.effect(g -> g.getPlayer().removeHealth(5))
				.reward(new Reward.Builder()
						.gold(4)
						.create()));
		add(new Creature.Builder()
				.name("Mithril Dragon")
				.rarity(Card.Rarity.EMERALD)
				.attack(8)
				.health(10)
				.reward(new Reward.Builder()
						.armour(12)
						.create()));
		add(new Support.Builder()
				.name("Preparation")
				.rarity(Card.Rarity.RUBY)
				.legend(Card.Legend.RAPTOR)
				.type(Card.Type.SPELL)
				.cost(0)
				.effect(g -> {
					Optional<Creature> next = g.getNextCard(c -> c instanceof Creature);

					next.ifPresent(c -> g.getPlayer().setGold(g.getPlayer().getGold() + (c.getAttack() / 2)));
				}));
		add(new Support.Builder()
				.name("Rock Cake")
				.rarity(Card.Rarity.SAPPHIRE)
				.cost(0)
				.effect(g -> g.getPlayer().removeHealth(3))
				.reward(new Reward.Builder()
						.armour(6)
						.create()));
		add(new Creature.Builder()
				.name("Rowdy Cannoneer")
				.rarity(Card.Rarity.EMERALD)
				.type(Card.Type.PIRATE)
				.attack(2)
				.health(5)
				.effect(g -> {
					g.getRival().dealDamage(4);
					g.getPlayer().addCard(findAny(c -> c.getName().equals("Cannonball")).get());
				}));
		add(new Support.Builder()
				.name("Saradomin Brew")
				.rarity(Card.Rarity.BASIC)
				.type(Card.Type.POTION)
				.legend(Card.Legend.RAPTOR)
				.cost(4)
				.effect(g -> g.setHealth(g.getHealth() + g.getArmour()))
				.reward(new Reward.Builder()
						.health(2)
						.create()));
		add(new Creature.Builder()
				.name("Sergeant Grimspike")
				.rarity(Card.Rarity.SAPPHIRE)
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
				.rarity(Card.Rarity.SAPPHIRE)
				.type(Card.Type.GOBLIN)
				.attack(2)
				.health(6)
				.reward(new Reward.Builder()
						.weapon(3, 2)
						.armour(2)
						.create()));
		add(new Support.Builder()
				.name("Shug")
				.rarity(Card.Rarity.RUBY)
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
				.rarity(Card.Rarity.EMERALD)
				.type(Card.Type.POTION)
				.cost(4)
				.effect(g -> g.getPlayer().setTemporaryAttack(5)));
		add(new Creature.Builder()
				.name("Tenebra")
				.rarity(Card.Rarity.RUBY)
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
				.rarity(Card.Rarity.RUBY)
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
				.rarity(Card.Rarity.RUBY)
				.type(Card.Type.TZHAAR)
				.attack(15)
				.health(7)
				.reward(new Reward.Builder()
						.base(1)
						.armour(4)
						.create()));
		add(new Support.Builder()
				.name("Vampyre Power")
				.rarity(Card.Rarity.DIAMOND)
				.legend(Card.Legend.VANESCULA)
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
		add(new Creature.Builder()
				.name("Void Brawler")
				.rarity(Card.Rarity.BASIC)
				.legend(Card.Legend.RAPTOR)
				.attack(7)
				.health(2)
				.effect(g -> g.getPlayer().removeHealth(6))
				.reward(new Reward.Builder()
						.armour(8)
						.create()));
		add(new Support.Builder()
				.name("Worthy Opponent")
				.rarity(Card.Rarity.SAPPHIRE)
				.legend(Card.Legend.VANESCULA)
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
				.rarity(Card.Rarity.DIAMOND)
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

	public static List<Card> findAll(Predicate<Card> filter) {
		return cards.stream().filter(filter).collect(Collectors.toList());
	}

	public static Optional<Card> findAny(Predicate<Card> filter) {
		return cards.stream().filter(filter).findAny();
	}
}