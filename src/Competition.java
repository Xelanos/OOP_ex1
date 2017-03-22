import java.util.Scanner;

/**
 * The Competition class represents a Nim competition between two players, consisting of a given number of
 * rounds. It also keeps track of the number of victories of each player.
 */
public class Competition {

	private static final int PLAYER1 = 1;
	private static final int PLAYER2 = 2;
	private static final int HUMAN = 4;

	private Player player1, player2;
	private boolean displayMessage;
	private int p1Score;
	private int p2Score;

	/* You need to implement this class */

	/**
	 * The method runs a Nim competition between two players according to the three user-specified arguments.
	 * (1) The type of the first player, which is a positive integer between 1 and 4: 1 for a Random computer
	 *     player, 2 for a Heuristic computer player, 3 for a Smart computer player and 4 for a human player.
	 * (2) The type of the second player, which is a positive integer between 1 and 4.
	 * (3) The number of rounds to be played in the competition.
	 * @param args an array of string representations of the three input arguments, as detailed above.
	 */
	public static void main(String[] args) {


		int p1Type = Integer.parseInt(args[0]);
		int p2Type = Integer.parseInt(args[1]);
		int numGames = Integer.parseInt(args[2]);

		Scanner scanner = new Scanner(System.in);

		Player player1 = new Player(p1Type,PLAYER1,scanner);
		Player player2 = new Player(p2Type,PLAYER2,scanner);

		boolean verboseMode = false;
		if (player1.getPlayerType() == HUMAN || player2.getPlayerType() == HUMAN) verboseMode = true;

		Competition competition = new Competition(player1, player2, verboseMode);
		competition.playMultipleRounds(numGames);
		
		scanner.close();
	}

	/**
	 *
	 * @param player1 first player
	 * @param player2 second player to compete against
	 * @param displayMessage true if user wants to display massages regarding moves, false if not.
	 */
	public Competition(Player player1, Player player2, boolean displayMessage){
		p1Score = 0;
		p2Score = 0;
		this.player1 = player1;
		this.player2 = player2;
		this.displayMessage = displayMessage;

	}

	/**
	 *Run the game for the given number of rounds.
	 * @param numRounds number of rounds to play the competition for.
	 */
	public void playMultipleRounds(int numRounds){
		System.out.println("Starting a Nim competition of "+numRounds+" rounds" +
				" between a "+player1.getTypeName()+" player" +
				"and a "+player2.getTypeName()+" player");
		for (int i=0; i < numRounds; i++){
			Board board = new Board();
			displayMessage("Welcome to the sticks game!");
			Player winner = playSingle(board);
			addPoint(winner);

		}
		System.out.println("The result are "+getPlayerScore(PLAYER1)+":"+getPlayerScore(PLAYER2));
	}

	/**
	 * A function that runs a single round of nim between two player of the competition.
	 * @param board board object on which to play on (ideally blank at start)
	 * @return the WINNER!!!!
	 */
	private Player playSingle(Board board){
		Player currentPlayer = player1;
		while (board.getNumberOfUnmarkedSticks()> 0){
			displayMessage("Player "+currentPlayer.getPlayerId()+", it is now your turn!");
			while (true){
				Move playerMove = currentPlayer.produceMove(board);
				int valid = board.markStickSequence(playerMove);
				if (valid == 0){
					displayMessage("Player "+currentPlayer.getPlayerId()+
							", made the move: "+playerMove);
					currentPlayer = otherPlayer(currentPlayer);
					break;

				} else {
					displayMessage("Invalid move. Enter another:");
				}
			}
		}
		displayMessage("player "+currentPlayer.getPlayerId()+" won!");
		return currentPlayer;

	}

	/**
	 * gets score of the player.
	 * @param playerPosition ID of the player
	 * @return If playerPosition = 1, the results of the first player is returned. If playerPosition = 2,
	 * the result of the second player is returned. If playerPosition equals neither, -1 is returned.
	 */
	public int getPlayerScore(int playerPosition){
		if (playerPosition == 1){
			return p1Score;
		} else if (playerPosition == 2){
			return p2Score;
		}
		return -1;
	}

	/**
	 * takes a player, and adds a point to his score.
	 * @param winner a player who won the round
	 */
	private void addPoint(Player winner){
		if (winner.getPlayerId() == 1){
			p1Score++;
		} else p2Score++;
	}

	/**
	 * A function alternating between two player.
	 * @param currentPlayer the player whose currently plating
	 * @return the player whose currently NOT playing
	 */
	private Player otherPlayer(Player currentPlayer){
		if (currentPlayer.getPlayerId() == 1) return player2;
		else return player1;
	}

	/**
	 * If verbose mode is on for the competition, prints the string. If off, does nothing.
	 * @param stringToDisplay the message to display to the user if verbose mode is on
	 */
	private void displayMessage(String stringToDisplay){
		if (displayMessage) {
			System.out.println(stringToDisplay);
		}
	}

}
