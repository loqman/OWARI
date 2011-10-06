package main;

import game.OWARI;
import player.*;

public class Main {

	public static void main(String[] args) {
		Player loqman = new Human("Loqman");
		Player bot = new Computer();
		OWARI game = new OWARI(loqman, bot);
		game.start();
	}
}
