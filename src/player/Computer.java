package player;

import java.util.Arrays;


import game.Players;

/**
 * Implements the MINIMAX game playing algorithm to
 * minimize the maximum total loss. This implementation is
 * specifically for the game 'OWARI'.
 * Alpha-Beta pruning is implemented.
 * Searches to a non-constant depth, taking into account only
 * node expansions.
 * @author Loqman
 *
 */
public class Computer implements Player {

	private String player_name;
	
	// Global definition of how many expansions to search
	private int max_expansions;
	
	// Count of total branches explored
	@SuppressWarnings("unused")
	private int total_branches;
	
	/**
	 * Definition of how many expansions to search, player name, 
	 * and Count of total branches explored
	 */
	public Computer() {
		this.player_name = "Computer";
		this.max_expansions = 30;
		this.total_branches = 0;
	}

	@Override
	public int getMove() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getMove(int[] board) {
		System.out.print("What is your move " + player_name + "?");
		long s = System.currentTimeMillis();
		int move = minimax(board);
		long f = System.currentTimeMillis();
		System.out.println("  (" + (f-s) + " ms)" );
		System.out.println(move);		
		return move;		
	}

	@Override
	public String getName() {
		return this.player_name;
	}

	@Override
	public Players getType() {
		return Players.computer;
	}
	
	/**
	 * Takes a board state as an argument.
	 * @param board
	 * @return the optimal hole to move, given the search space inspected
	 */
	private int minimax(int[] board)
	{
		// Set expansions to total number of expansions desired per top level branch
		int alpha = -100000;
		int beta = 100000;
		int expansions = 0;
		return this.maxValue(board, expansions, alpha, beta)[1] + 7;		
	}
	
	/**
	 * Takes a board state, number of expansions, alpha and
	 * beta value as arguments.
	 * @param board
	 * @param expansions
	 * @param alpha
	 * @param beta
	 * @return Returns the largest heuristic value for a given hole along a given path and the index number of that hole,
	 * only the first is processed when maxValue() returns to minValue().
	 */
	private int[] maxValue(int[] board, int expansions, int alpha, int beta)
	{
		total_branches++;
		int[] pits = new int[]{-9999, -9999, -9999, -9999, -9999, -9999};
		int best_hole = -1;
		int c_hole_vals = 0;
		int p_hole_vals = 0;
		for(int i=7; i<13; i++) c_hole_vals += board[i];
		for(int i=0; i<6; i++) p_hole_vals += board[i];
		if(expansions >= this.max_expansions) return new int[]{staticEvaluator(board)};
		else if(c_hole_vals == 0) return new int[]{staticEvaluator(board)};
		else if(p_hole_vals == 0) return new int[]{staticEvaluator(board)};
		else {						
			for(int i=7; i<13; i++)
			{
				if(!(board[i]==0))
				{
					expansions++;
					pits[i-7] = Math.max(pits[i-7], minValue(this.getBoard(Players.second_opponent, board.clone(), i), expansions+1, alpha, beta));
					if(pits[i-7] >= beta) return new int[]{pits[i-7], i};
					else if(pits[i-7] > alpha) {
						alpha = pits[i-7];
						best_hole = i;
					}
				}
			}
		}
		best_hole = indexOfMax(pits);
		return new int[]{pits[best_hole], best_hole};
	}
	
	/**
	 * Takes a board state, number of expansions, alpha and
	 * beta value as arguments.
	 * @param board
	 * @param expansions
	 * @param alpha
	 * @param beta
	 * @return the smallest heuristic value for a given hole along a path
	 */
	private int minValue(int[] board, int expansions, int alpha, int beta)
	{
		total_branches++;
		int[] pits = new int[]{9999, 9999, 9999, 9999, 9999, 9999};
		int best_hole = -1;
		int c_hole_vals = 0;
		int p_hole_vals = 0;
		for(int i=7; i<13; i++) c_hole_vals += board[i];
		for(int i=0; i<6; i++) p_hole_vals += board[i];
		// 3 terminal states: (1) we have reached the bottom of the search tree
		// or (2, 3) the game has been lost by one side.
		if(expansions >= max_expansions) return staticEvaluator(board);
		else if(c_hole_vals == 0) return staticEvaluator(board);
		else if(p_hole_vals == 0) return staticEvaluator(board);
		else {
			for(int i=0; i<6; i++)
			{
				if(!(board[i]==0)) // Do not expand holes with nothing in them
				{
					expansions++;
					// Build the minimax tree
					pits[i] = Math.min(pits[i], maxValue(getBoard(Players.first_opponent, board.clone(), i), expansions+1, alpha, beta)[0]);
					if(pits[i] <= alpha) return pits[i];
					beta = Math.min(beta, pits[i]);
				}
			}				
		}
		best_hole = indexOfMin(pits);
		return pits[best_hole];
	}
	
	/**
	 * Static evaluator for OWARI game: 
	 * If the player has no more stones in its holes, add the stones left on
	 * the computer's side to the computer's mancala and multiple the relative 
	 * mancala value by 100.
	 * If the computer has no more stones in its holes, add the stones left on
	 * the player's side to the player's mancala and multiple the relative
	 * mancala value by 100.
	 * Simple heuristic, relative value of the computer's mancala:
	 * 		(computer_goal_value) - (person_goal_value)
	 * More complicated heuristic that engages the relative value
	 * of the computer's mancala and gives the computer a degree
	 * of unpredictability. 
	 * 		(computer_goal_value) - (person_goal_value) + random(3) ; Add a degree of random play
	 * @param a board state 
	 * @return the heuristic value for a given board
	 */
	private int staticEvaluator(int[] board)
	{
		int result = 0;
		int c_hole_vals = 0;
		int p_hole_vals = 0;
		for(int i=7; i<13; i++) c_hole_vals += board[i];
		for(int i=0; i<6; i++) p_hole_vals += board[i];		
		if(p_hole_vals == 0)
		{
			result = (c_hole_vals + board[13] - board[6]) * 100;
		} else if(c_hole_vals == 0) {
			result = (board[13] - (p_hole_vals + board[6])) * 100;
		} else {
			result = board[13] - board[6];
		}
		return result;
	}
	
	/**
	 * is the auxiliary function for minValue() and maxValue. It is given:
	 * whose turn it is to play, the hole number we're about to start from,
	 * and the BOARD as it looks so far.  When there are no more stones left
	 * in the player's hand, getBoard() determines whether any stones
	 * have been captured, and finally returns the new BOARD.
	 * 
	 * Style note: This function is too long to leave with just a header
	 * comment, so I should probably break it into smaller functions.  I
	 * have chosen instead to use in-line comments.
	 * @param player whose turn it is
	 * @param board as it looks so far
	 * @param pit number we are about to start from
	 * @return the new board
	 */
	private int[] getBoard(Players player, int[] board, int pit)
	{
		int board_pit;
		int stones_in_hand = 0;
		if(player == Players.first_opponent)
		{	
			board_pit = pit;			
			stones_in_hand = board[board_pit];
			board[board_pit] = 0;
			int next_pit = board_pit + 1;
			for(int i=0; i<stones_in_hand; i++)
			{
				// If we've gone all the way around the circle, go back to the start
				if(next_pit >= 14) next_pit = 0;
				
				// Opponent's goal pit, if reached, skip it
				if(next_pit == 13) 
				{
					next_pit++;
					i--;
					continue;
				}
				
				// Players last stone if fall into an empty pit on the moving player side of the board
				// then any stones in the pit opposite to it are captured and placed in the moving
				// player's goal pit
				if(i == (stones_in_hand - 1) && arrayContain(new int[]{0, 1, 2, 3, 4, 5}, next_pit))
				{
					if(board[next_pit] == 0) {
						board[6] += board[12 - next_pit];
						board[12 - next_pit] = 0;						
					}
					board[next_pit]++;					
					continue;
				}
				
				// We still have stones, so move to the next hole
				board[next_pit]++;
				next_pit++;
			}
		} else {
			board_pit = pit;
			stones_in_hand = board[board_pit];
			board[board_pit] = 0;
			int next_pit = board_pit + 1;
			for(int i=0; i<stones_in_hand; i++)
			{
				// If we've gone all the way around the circle, go back to the start
				if(next_pit >= 14) next_pit = 0;
				
				// Opponent's goal pit, if reached, skip it
				if(next_pit == 6) 
				{
					next_pit++;
					i--;
					continue;
				}
				
				// Players last stone if fall into an empty pit on the moving player side of the board
				// then any stones in the pit opposite to it are captured and placed in the moving
				// player's goal pit
				if(i == (stones_in_hand - 1) && arrayContain(new int[]{7, 8, 9, 10, 11, 12}, next_pit))
				{
					if(board[next_pit] == 0) {
						board[13] += board[12 - next_pit];
						board[12 - next_pit] = 0;						
					}
					board[next_pit]++;					
					continue;
				}
				
				// We still have stones, so move to the next hole
				board[next_pit]++;
				next_pit++;
			}
		}
		return board;
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
	
	/**
	 * A utility function for this class
	 * @param an integer array
	 * @return the index of maximum element in the array
	 */
	private int indexOfMax(int[] a)
	{
		int max = Integer.MAX_VALUE * -1;
		int index = -1;
		for(int i=0; i<a.length; i++)
		{
			if(max < a[i]) {
				max = a[i];
				index = i;
			}
		}
		return index;		
	}
	
	/**
	 * A utility function for this class
	 * @param an integer array
	 * @return the index of minimum element in the array
	 */
	private int indexOfMin(int[] a)
	{
		int min = Integer.MAX_VALUE;
		int index = -1;
		for(int i=0; i<a.length; i++)
		{
			if(min > a[i]) {
				min = a[i];
				index = i;
			}
		}
		return index;		
	}

}