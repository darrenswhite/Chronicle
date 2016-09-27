package com.darrenswhite.chronicle.simulator.combo;

import com.darrenswhite.chronicle.card.Card;
import com.darrenswhite.chronicle.game.Game;
import com.darrenswhite.chronicle.permutation.PermutationConsumer;
import com.darrenswhite.chronicle.player.Player;
import org.apache.commons.compress.compressors.CompressorException;
import org.apache.commons.compress.compressors.CompressorStreamFactory;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author Darren White
 */
public class GamePermutationConsumer extends PermutationConsumer<Game> {

	private final Path path;
	private final int numCards;
	private PrintWriter out;

	public GamePermutationConsumer(Path path, int numCards) {
		this.path = path;
		this.numCards = numCards;
	}

	@Override
	public void accept(Game g) {
		if (g != null) {
			printGame(g);
		}
	}

	private synchronized void printGame(Game g) {
		StringBuilder sb = new StringBuilder();
		Player p = g.getPlayer();
		Player r = g.getRival();

		for (Card card : g.getCards()) {
			sb.append(card.getId()).append('\t');
		}

		sb.append(p.getAttack()).append('\t');
		sb.append(p.getGold()).append('\t');
		sb.append(p.getHealth()).append('\t');
		sb.append(p.getArmour()).append('\t');
		sb.append(p.getWeapon() != null ? p.getWeapon() : "").append('\t');
		sb.append(p.getMaxHealth()).append('\t');

		sb.append(r.getAttack()).append('\t');
		sb.append(r.getGold()).append('\t');
		sb.append(r.getHealth()).append('\t');
		sb.append(r.getArmour()).append('\t');
		sb.append(r.getWeapon() != null ? r.getWeapon() : "").append('\t');
		sb.append(r.getMaxHealth()).append('\t');

		out.println(sb.toString());
	}

	@Override
	public boolean start() {
		super.start();

		try {
			out = new PrintWriter(new CompressorStreamFactory()
					.createCompressorOutputStream(CompressorStreamFactory.BZIP2,
							Files.newOutputStream(path)), true);

			for (int i = 0; i < numCards; i++) {
				out.print("card" + i + "\t");
			}

			out.println("attack0\tgold0\thealth0\tarmour0\tweapon0\tmaxHealth0\tattack1\tgold1\thealth1\tarmour1\tweapon1\tmaxHealth1");
			return true;
		} catch (CompressorException | IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public void stop() {
		super.stop();
		out.close();
	}
}