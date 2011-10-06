package player;

import game.Players;

import java.util.Scanner;

/**
 * This class is very simple, it just get an integer from
 * console and return it to the game, it also has the player's name
 * @author Loqman
 *
 */
public class Human implements Player {

	private String player_name;	 
	
	/**
	 * Constructor of the class that sets the player name
	 * @param player_name
	 */
	public Human(String player_name) {
		this.player_name = player_name;
	}
	
	@Override	
	public int getMove() {
		System.out.println("What is your move " + player_name + "?");
		Scanner cin = new Scanner(System.in);
		return cin.nextInt();
	}

	@Override
	public String getName() {
		return this.player_name;
	}

	@Override
	public int getMove(int[] board) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Players getType() {
		return Players.human;
	}

}
