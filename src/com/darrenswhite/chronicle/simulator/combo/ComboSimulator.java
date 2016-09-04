package com.darrenswhite.chronicle.simulator.combo;

import com.darrenswhite.chronicle.Game;
import com.darrenswhite.chronicle.card.Card;
import com.darrenswhite.chronicle.card.CardBuilder;
import com.darrenswhite.chronicle.card.CardCollection;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

/**
 * @author Darren White
 */
public class ComboSimulator {

	private final Game game = new Game();
	private final List<List<Card>> combos = new LinkedList<List<Card>>() {{
		add(new LinkedList<>());
	}};
	private int index = 0;

	public void addCard(Card c) {
		combos.get(index).add(c);
	}

	public void addCard(String name) {
		Optional<Card> card = CardCollection.findAny(c -> c.getName().equalsIgnoreCase(name) ||
				c.getName().toLowerCase().startsWith(name.toLowerCase()));

		card.ifPresent(this::addCard);
	}

	public void addCards(String... names) {
		for (String name : names) {
			addCard(name);
		}
	}

	public void addCards(Collection<Card> cards) {
		cards.forEach(this::addCard);
	}

	public void addCards(Card... cards) {
		for (Card c : cards) {
			addCard(c);
		}
	}

	public void addCards(CardBuilder... cards) {
		for (CardBuilder c : cards) {
			addCard(c.create());
		}
	}

	private String getCardsString(Collection<Card> cards) {
		StringBuilder sb = new StringBuilder();

		for (Card c : cards) {
			if (sb.length() > 0) {
				sb.append(" -> ");
			}

			if (c != null) {
				sb.append(c.getName());
			} else {
				sb.append('_');
			}
		}

		return sb.toString();
	}

	public static void main(String[] args) {
		ComboSimulator sim = new ComboSimulator();

		sim.addCards("Tormented Demon", "Shug", "Tenebra", "Alpha Werewolf");
		sim.simulate();

		/*System.out.println();
		System.out.println("--------------------------------------------------------------------------------------------------------");
		System.out.println();

		sim.reset();
		sim.addCards("Sergeant Slimetoes", "Mithril Dragon", "Fight Cauldron", "Kalphite Soldier");
		sim.simulate();

		System.out.println();
		System.out.println("--------------------------------------------------------------------------------------------------------");
		System.out.println();

		sim.simulate(0);*/
	}

	public void reset() {
		combos.add(new LinkedList<>());
		index = combos.size() - 1;
	}

	public void simulate() {
		simulate(index);
	}

	public void simulate(int index) {
		System.out.println("Player: " + game.getPlayer());
		System.out.println("Rival: " + game.getRival());
		System.out.println();

		game.addCards(combos.get(index));
		game.start();

		System.out.println(getCardsString(game.getCards()));
		System.out.println();
		System.out.println("Player: " + game.getPlayer());
		System.out.println("Rival: " + game.getRival());

		game.reset();
	}
}