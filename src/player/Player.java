package player;

import game.Players;

/**
 * Abstraction layer for players
 * @author Loqman
 * @see Human, Computer
 */
public interface Player {

	/**
	 * General method for making the decision of the next move
	 * @return the pit number that the player decide to start the move from
	 */
	public int getMove();
	/**
	 * Takes a board state as an argument.
	 * Usually used when the type of the player is computer.
	 * @param board
	 * @return the optimal node to move, given the limited size of the search space inspected.
	 */
	public int getMove(int[] board);
	/**
	 * @return the name of the player 
	 */
	public String getName();
	/**
	 * @return the type of the player (computer or human)
	 */
	public Players getType();
}
