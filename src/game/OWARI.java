package game;

import java.util.Arrays;

import player.Player;

/**
 * OWARI is played on a board with 14 holes arranged as two rows of 6
 * with two holes in the middle on the ends of the rows.  
 * The STATE of the game is very simple to describe.  All you need
 * to keep track of are the number of stones in each of the fourteen
 * holes (and whose turn it is, but that can be done automatically).
 * Let's represent the board as a list of 14 numbers.  The first
 * six will be the player's holes, the seventh will be the
 * player's goal hole, the next six will be the computer's holes
 * (counting counter-clockwise around the board) and the 14th will
 * be the computer's goal hole.  So the list 
 * 		(1 3 0 2 2 2 6 4 2 1 0 8 1 11)
 * would represent the board position:
 *    1  8  0  1  2  4
 * 11                   6
 *    1  3  0  2  2  2
 * For the purposes of this program, holes are numbered from 0 to 13,
 * beginning with the player's first hole and going counterclockwise.
 * So above, there is 1 stone in hole number 0, 3 in hole number 1,
 * 4 in number 7, 11 in number 13, etc.
 * 
 * @author Loqman
 *  
 */
public class OWARI {

	private int[] board;
	private int first_opponent_goal_pit;
	private int second_opponent_goal_pit;
	private int value_first_opponent_goal_pit;
	private int value_second_opponent_goal_pit;
	private int[] first_opponent_pits;
	private int[] second_opponent_pits;
	private int[] value_first_opponent_pits;
	private int[] value_second_opponent_pits;
	
	private Player first_opponent;
	private Player second_opponent;
	private Players current_player;
	
	
	/**
	 * Takes players as arguments.  OWARI is the top-level call.  It sets the
	 * board to the initial position. 
	 */
	public OWARI(Player first_opponent, Player second_opponent)
	{
		this.first_opponent = first_opponent;
		this.second_opponent = second_opponent;
		this.current_player = Players.first_opponent;
		// Sets the board to the initial position 
		board = new int[]{3, 3, 3, 3, 3, 3, 0, 3, 3, 3, 3, 3, 3, 0};
		
		// The numbers of the goal pits
		first_opponent_goal_pit = 6;
		second_opponent_goal_pit = 13;
		
		// The values in goal pits
		value_first_opponent_goal_pit = 0;
		value_second_opponent_goal_pit = 0;
		
		// The numbers of other pits
		first_opponent_pits = new int[]{0, 1, 2, 3, 4, 5};
		second_opponent_pits = new int[]{7, 8, 9, 10, 11, 12};
		
		// The values in other pits
		value_first_opponent_pits = new int[]{3, 3, 3, 3, 3, 3};
		value_second_opponent_pits = new int[]{3, 3, 3, 3, 3, 3};		
	}
	
	
	/**
	 * Takes no arguments. iteratively prints the board,
	 * asks the player for a move, handles the player's move, and repeats.
	 * It also checks for the end-of-game condition after every move
	 * by each side, and prints final statistics if the end is reached.
	 */
	public void start()
	{		
		printBoard();
		while(!endOfGame())
		{	
			int[] b = board.clone();
			if(current_player == Players.first_opponent)
			{
				int pit = 0; 
				if(this.first_opponent.getType() == Players.human) 
				{
					pit = this.first_opponent.getMove();
				} else if(this.first_opponent.getType() == Players.computer) {
					pit = this.first_opponent.getMove(board);
				}
				if(!doMove(pit, Players.first_opponent)) continue;
				current_player = Players.second_opponent;
				printBoard();
			} else {
				int pit = 0;
				if(this.second_opponent.getType() == Players.human) 
				{
					pit = this.second_opponent.getMove();
				} else if(this.second_opponent.getType() == Players.computer) {
					pit = this.second_opponent.getMove(b);				
				}
				if(!doMove(pit, Players.second_opponent)) continue;
				current_player = Players.first_opponent;
				printBoard();
			}			
		}
		finalBoard();
	}
	
	/**
	 * Prints the board, calculates the final score,
	 * and prints that as well.  It assumes that checking has already
	 * been done to ensure that the game really is over.
	 */
	private void finalBoard()
	{
		int first_opponent_score = 0;
		int second_opponent_score = 0;
		first_opponent_score = value_first_opponent_goal_pit + sumArray(value_first_opponent_pits);
		second_opponent_score = value_second_opponent_goal_pit + sumArray(value_second_opponent_pits);
		if(first_opponent_score == second_opponent_score)
		{
			System.out.println("It's a draw!");
		} else if(first_opponent_score > second_opponent_score) {
			System.out.println(first_opponent.getName() + " has won! score: " + first_opponent_score);
		} else {
			System.out.println(second_opponent.getName() + " has won! score: " + second_opponent_score);
		}
	}
	
	/**
	 * Prints the current state of the board
	 */
	private void printBoard()
	{
		System.out.println("======================================================");
		System.out.print("   ");
		for(int i=12; i>6; i--)
			System.out.print(board[i] + "  ");
		System.out.println();
		System.out.println(board[13] + "                   " + board[6]);
		System.out.print("   ");
		for(int i=0; i<6; i++)
			System.out.print(board[i] + "  ");
		System.out.println();
	}
	
	/**
	 * Checks if the move is legal or not
	 * @param pit
	 * @param player
	 * @return false if move is illegal, true if it is legal 
	 */
	private boolean doMove(int pit, Players player)
	{
		// Check if the move is legal 
		if(player == Players.first_opponent)
		{
			if(!arrayContain(first_opponent_pits, pit))
			{
				return false;
			} else if(value_first_opponent_pits[pit] == 0) {
				return false;
			}
		} else {
			if(!arrayContain(second_opponent_pits, pit))
			{
				return false;
			} else if(value_second_opponent_pits[pit - 7] == 0) {
				return false;
			}
		}
		
		// If the move is legal process the move
		processMove(pit, player);
		return true;
	}
	
	/**
	 * Is the auxiliary function for doMove(). It is given:
	 * whose turn it is to play and the hole number we're about to
	 * make move from that. When there are no more stones left
	 * in the player's hand, processMove() determines whether any stones
	 * have been captured, and finally update the state.
	 * 
	 * Style note: This function is too long to leave with just a header
	 * comment, so I should probably break it into smaller functions. I
	 * have chosen instead to use in-line comments.
	 * 
	 * @param pit
	 * @param player
	 */
	private void processMove(int pit, Players player)
	{
		int board_pit;
		int stones_in_hand = 0;
		if(player == Players.first_opponent)
		{	
			board_pit = first_opponent_pits[pit];			
			stones_in_hand = board[board_pit];
			board[board_pit] = 0;
			int next_pit = board_pit + 1;
			for(int i=0; i<stones_in_hand; i++)
			{
				// If we've gone all the way around the circle, go back to the start
				if(next_pit >= 14) next_pit = 0;
				
				// Opponent's goal pit, if reached, skip it
				if(next_pit == second_opponent_goal_pit) 
				{
					next_pit++;
					i--;
					continue;
				}
				
				// Players last stone if fall into an empty pit on the moving player side of the board
				// then any stones in the pit opposite to it are captured and placed in the moving
				// player's goal pit
				if(i == (stones_in_hand - 1) && arrayContain(first_opponent_pits, next_pit))
				{
					if(board[next_pit] == 0) capture((12 - next_pit), first_opponent_goal_pit);
					board[next_pit]++;					
					continue;
				}
				
				// We still have stones, so move to the next hole
				board[next_pit]++;
				next_pit++;
			}
		} else {
			board_pit = second_opponent_pits[pit - 7];
			stones_in_hand = board[board_pit];
			board[board_pit] = 0;
			int next_pit = board_pit + 1;
			for(int i=0; i<stones_in_hand; i++)
			{
				// If we've gone all the way around the circle, go back to the start
				if(next_pit >= 14) next_pit = 0;
				
				// Opponent's goal pit, if reached, skip it
				if(next_pit == first_opponent_goal_pit) 
				{
					next_pit++;
					i--;
					continue;
				}
				
				// Players last stone if fall into an empty pit on the moving player side of the board
				// then any stones in the pit opposite to it are captured and placed in the moving
				// player's goal pit
				if(i == (stones_in_hand - 1) && arrayContain(second_opponent_pits, next_pit))
				{
					if(board[next_pit] == 0) capture((12 - next_pit), second_opponent_goal_pit);
					board[next_pit]++;					
					continue;
				}
				
				// We still have stones, so move to the next hole
				board[next_pit]++;
				next_pit++;
			}
		}
		update();
	}
	
	/**
	 * Update the players pits value according to corresponding ones in the board array
	 */
	private void update()
	{
		for(int i=0; i<6; i++)
			value_first_opponent_pits[i] = board[i];
		value_first_opponent_goal_pit = board[6];
		for(int i=7; i<13; i++)
			value_second_opponent_pits[i - 7] = board[i];
		value_second_opponent_goal_pit = board[13];
	}
	
	/**
	 * Is called by processMove() at the point when
	 * all stones have been distributed.  At that point, if there is
	 * exactly one stone in the last hole, and if that hole is on the
	 * current player's side of the board, then any stones on the other
	 * side are captured.
	 * @param pit
	 * @param goal_pit
	 */
	private void capture(int pit, int goal_pit)
	{		
		board[goal_pit] += board[pit];
		board[pit] = 0;
	}
	
	
	/**
	 * Checks if the game ends or not. Since none of the board positions can have
	 * a negative number of stones, then as long as the sum of the values is 0,
	 * all the individual values must be zero as well.
	 * @return true if game is over, false if the game is not over
	 */
	private boolean endOfGame()
	{
		int first_opponent_sum = sumArray(value_first_opponent_pits);
		int second_opponent_sum = sumArray(value_second_opponent_pits);
		if(first_opponent_sum == 0 || second_opponent_sum == 0) return true;
		return false;
	}
	
	/**
	 * This is a utility function for the program to get the sum of the given
	 * array's elements. 
	 * @param a
	 * @return the sum of the given array's elements
	 */
	private int sumArray(int[] a)
	{
		int length = a.length;
		int result = 0;
		for(int i=0; i<length; i++)
			result += a[i];
		return result;
	}
	
	/**
	 * Check if the given array contain the specified element
	 * @param a
	 * @param key
	 * @return true if array contains the element, otherwise false
	 */
	private boolean arrayContain(int[] a, int key)
	{
		int p = Arrays.binarySearch(a, key);
		if(p >= 0) return true;
		return false;
	}	
	
}