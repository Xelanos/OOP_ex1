import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;


/**
 * The Player class represents a player in the Nim game, producing Moves as a response to a Board state.
 * Each player is initialized with a type, either human or one of several computer strategies, which
 * defines the move he produces when given a board in some state. The heuristic strategy of the player is
 * already implemented.  You are required to implement the rest of the player types according to the exercise
 * description.
 * @author OOP course staff
 */
public class Player {

	//Constants that represent the different players.
	/** The constant integer representing the Random player type. */
	public static final int RANDOM = 1;
	/** The constant integer representing the Heuristic player type. */
	public static final int HEURISTIC = 2;
	/** The constant integer representing the Smart player type. */
	public static final int SMART = 3;
	/** The constant integer representing the Human player type. */
	public static final int HUMAN = 4;
	/**Used by produceHeuristicMove() for binary representation of board rows. */
	private static final int BINARY_LENGTH = 4;
	/**Used by human player for displaying the board */
	private static final int DISPLAY_BOARD = 1;
	/**Used by human player for making a move*/
	private static final int MAKE_MOVE = 2;


	private final int playerType;
	private final int playerId;
	private Scanner scanner;
	private Random random = new Random();

	/**
	 * Initializes a new player of the given type and the given id, and an initialized scanner.
	 * @param type The type of the player to create.
	 * @param id The id of the player (either 1 or 2).
	 * @param inputScanner The Scanner object through which to get user input
	 * for the Human player type.
	 */
	public Player(int type, int id, Scanner inputScanner){
		// Check for legal player type (we will see better ways to do this in the future).
		if (type != RANDOM && type != HEURISTIC
				&& type != SMART && type != HUMAN){
			System.out.println("Received an unknown player type as a parameter"
					+ " in Player constructor. Terminating.");
			System.exit(-1);
		}
		playerType = type;
		playerId = id;
		scanner = inputScanner;
	}

	/**
	 * @return an integer matching the player type.
	 */
	public int getPlayerType(){
		return playerType;
	}

	/**
	 * @return the players id number.
	 */
	public int getPlayerId(){
		return playerId;
	}


	/**
	 * @return a String matching the player type.
	 */
	public String getTypeName(){
		switch(playerType){

			case RANDOM:
				return "Random";

			case SMART:
				return "Smart";

			case HEURISTIC:
				return "Heuristic";

			case HUMAN:
				return "Human";
		}
		//Because we checked for legal player types in the
		//constructor, this line shouldn't be reachable.
		return "UnknownPlayerType";
	}

	/**
	 * This method encapsulates all the reasoning of the player about the game. The player is given the
	 * board object, and is required to return his next move on the board. The choice of the move depends
	 * on the type of the player: a human player chooses his move manually; the random player should
	 * return some random move; the Smart player can represent any reasonable strategy; the Heuristic
	 * player uses a strong heuristic to choose a move.
	 * @param board - a Board object representing the current state of the game.
	 * @return a Move object representing the move that the current player will play according to
	 * his strategy
	 */
	public Move produceMove(Board board){

		switch(playerType){

			case RANDOM:
				return produceRandomMove(board);

			case SMART:
				return produceSmartMove(board);

			case HEURISTIC:
				return produceHeuristicMove(board);

			case HUMAN:
				return produceHumanMove(board);

			//Because we checked for legal player types in the
			//constructor, this line shouldn't be reachable.
			default:
				return null;
		}
	}

	/**
	 * get a random move to play. guaranteed to be a valid move.
	 * @param board a board to play the move on.
	 * @return A guaranteed valid random move.
	 */
	private Move produceRandomMove(Board board){
	    if (board.getNumberOfUnmarkedSticks() > 1){
            while (true){
                Move randomMove =  totallyRandomMove(board);
                if (isMoveValid(randomMove, board)){
                    return randomMove;
                }
            }
        } else return removeOneMove(board); // last stick is not random , better just to search for it.

	}

	/**
	 * Gets a totally random move, within the board parameters. (will never returns out of bounds move)
	 * @param board board to get move for.
	 * @return A random move within the board parameters.
	 */
	private Move totallyRandomMove(Board board){
		int randomRow = random.nextInt(board.getNumberOfRows()) + 1;
		int randomRowLength = board.getRowLength(randomRow);
		int randomLeftStick = random.nextInt(randomRowLength) + 1;
		// right stick randomized from left and onwards
		int randomRightStick = random.nextInt(randomRowLength + 1 - randomLeftStick) + randomLeftStick;
		return new Move(randomRow,randomLeftStick,randomRightStick);
	}

	/**
	 * Assuming inputted move is within board bounds.
	 * checks if the move inputted is valid from the marked sticks perspectives.
	 * @param move a move to check
	 * @param board a board to check validity on.
	 * @return true if the move is valid. false if invalid.
	 */
	private boolean isMoveValid(Move move, Board board){
		boolean valid = true;
		for(int j=move.getLeftBound();j<=move.getRightBound();j++) {
			if (!board.isStickUnmarked(move.getRow(), j)) {
				valid = false;
			}
		}
		return valid;
	}

	/*
	 * Produce some intelligent strategy to produce a move
	 */
	private Move produceSmartMove(Board board){
		Move smartMove;
		// plays dumb until the end to make it more efficient.
		if (board.getNumberOfUnmarkedSticks() > 6){
			while (true){
				smartMove = totallyRandomMove(board);
				if (isMoveValid(smartMove, board)){
					return smartMove;
				}
			}
		} else {
			smartMove = calculatedMove(board);
			if (isMoveValid(smartMove, board)) {
				return smartMove;
			}
		}
        return smartMove;
    }

	/**
	 * get a move which is a calculated  move towards VICTORY!!! see implementation notes for explanation
	 * @param board a board to play the move on.
	 * @return a smart move to play on the board.
	 */
	private Move calculatedMove(Board board) {
	    if (board.getNumberOfUnmarkedSticks() % 2 == 1){
            return removeTwoMove(board);
        }
        else if (board.getNumberOfUnmarkedSticks() % 2 == 0) {
            return removeOneMove(board);

        }
        return null; // will not get to this
    }

    /**
     * returns a valid move which markes two matches. if can't find a valid two-match move, returns a single
     * match move.
     * @param board board to check move from
     * @return
     */
    private Move removeTwoMove(Board board) {
	    Move twoMove;
	    int potentialTwoStartStick;
	    int potentialTwoEndStick;
        for (int row=1 ; row<= board.getNumberOfRows(); row++){
            for (int stick=1; stick <= board.getRowLength(row); stick++){
                if (board.isStickUnmarked(row,stick)){
                    potentialTwoStartStick = stick;
                    potentialTwoEndStick = potentialTwoStartStick + 1;
                    if (potentialTwoEndStick <= board.getRowLength(row)){
                        if (board.isStickUnmarked(row , potentialTwoEndStick)){
                            twoMove = new Move(row, potentialTwoStartStick, potentialTwoEndStick);
                            return twoMove;
                        }
                    }
                    twoMove = new Move(row, potentialTwoStartStick, potentialTwoStartStick);
                    return twoMove;
                }
            }
        }
        return null; // will not get to this line
    }

    private Move removeOneMove(Board board) {
	    Move removeOne = null;
	    for (int i=1 ; i<= board.getNumberOfRows(); i++){
	        for (int j=1; j <= board.getRowLength(i); j++){
	            if (board.isStickUnmarked(i,j)){
	                removeOne = new Move(i,j,j);
	                return removeOne;
                }
            }
        }
        return removeOne;
    }


    /**
	 * Produce a move according to inputs by the player.
	 * @param board a board to play the move on (or display the current state of the game)
	 * @return A move dictated by user inputs
	 */
	private Move produceHumanMove(Board board){
		while (true){
			System.out.println("Press 1 to display the board. Press 2 to make a move:");
			int userDecision = scanner.nextInt();

			if (userDecision == DISPLAY_BOARD){
				System.out.println(board);

			} else if (userDecision == MAKE_MOVE){
				System.out.println("Enter the row number:");
				int rowNumber = scanner.nextInt();
				System.out.println("Enter the index of the leftmost stick:");
				int leftBound = scanner.nextInt();
				System.out.println("Enter the index of the rightmost stick:");
				int rightBound = scanner.nextInt();
				return new Move(rowNumber, leftBound, rightBound);

			} else {
				System.out.println("Unsupported command");
			}

		}
	}

	/*
	 * Uses a winning heuristic for the Nim game to produce a move.
	 */
	private Move produceHeuristicMove(Board board){

		int numRows = board.getNumberOfRows();
		int[][] bins = new int[numRows][BINARY_LENGTH];
		int[] binarySum = new int[BINARY_LENGTH];
		int bitIndex,higherThenOne=0,totalOnes=0,lastRow=0,lastLeft=0,lastSize=0,lastOneRow=0,lastOneLeft=0;

		for(bitIndex = 0;bitIndex<BINARY_LENGTH;bitIndex++){
			binarySum[bitIndex] = 0;
		}

		for(int k=0;k<numRows;k++){

			int curRowLength = board.getRowLength(k+1);
			int i = 0;
			int numOnes = 0;

			for(bitIndex = 0;bitIndex<BINARY_LENGTH;bitIndex++){
				bins[k][bitIndex] = 0;
			}

			do {
				if(i<curRowLength && board.isStickUnmarked(k+1,i+1) ){
					numOnes++;
				} else {

					if(numOnes>0){

						String curNum = Integer.toBinaryString(numOnes);
						while(curNum.length()<BINARY_LENGTH){
							curNum = "0" + curNum;
						}
						for(bitIndex = 0;bitIndex<BINARY_LENGTH;bitIndex++){
							bins[k][bitIndex] += curNum.charAt(bitIndex)-'0'; //Convert from char to int
						}

						if(numOnes>1){
							higherThenOne++;
							lastRow = k +1;
							lastLeft = i - numOnes + 1;
							lastSize = numOnes;
						} else {
							totalOnes++;
						}
						lastOneRow = k+1;
						lastOneLeft = i;

						numOnes = 0;
					}
				}
				i++;
			}while(i<=curRowLength);

			for(bitIndex = 0;bitIndex<BINARY_LENGTH;bitIndex++){
				binarySum[bitIndex] = (binarySum[bitIndex]+bins[k][bitIndex])%2;
			}
		}


		//We only have single sticks
		if(higherThenOne==0){
			return new Move(lastOneRow,lastOneLeft,lastOneLeft);
		}

		//We are at a finishing state
		if(higherThenOne<=1){

			if(totalOnes == 0){
				return new Move(lastRow,lastLeft,lastLeft+(lastSize-1) - 1);
			} else {
				return new Move(lastRow,lastLeft,lastLeft+(lastSize-1)-(1-totalOnes%2));
			}

		}

		for(bitIndex = 0;bitIndex<BINARY_LENGTH-1;bitIndex++){

			if(binarySum[bitIndex]>0){

				int finalSum = 0,eraseRow = 0,eraseSize = 0,numRemove = 0;
				for(int k=0;k<numRows;k++){

					if(bins[k][bitIndex]>0){
						eraseRow = k+1;
						eraseSize = (int)Math.pow(2,BINARY_LENGTH-bitIndex-1);

						for(int b2 = bitIndex+1;b2<BINARY_LENGTH;b2++){

							if(binarySum[b2]>0){

								if(bins[k][b2]==0){
									finalSum = finalSum + (int)Math.pow(2,BINARY_LENGTH-b2-1);
								} else {
									finalSum = finalSum - (int)Math.pow(2,BINARY_LENGTH-b2-1);
								}

							}

						}
						break;
					}
				}

				numRemove = eraseSize - finalSum;

				//Now we find that part and remove from it the required piece
				int numOnes=0,i=0;
				while(numOnes<eraseSize){

					if(board.isStickUnmarked(eraseRow,i+1)){
						numOnes++;
					} else {
						numOnes=0;
					}
					i++;

				}
				return new Move(eraseRow,i-numOnes+1,i-numOnes+numRemove);
			}
		}

		//If we reached here, and the board is not symmetric, then we only need to erase a single stick
		if(binarySum[BINARY_LENGTH-1]>0){
			return new Move(lastOneRow,lastOneLeft,lastOneLeft);
		}

		//If we reached here, it means that the board is already symmetric, and then we simply mark one stick
		// from the last sequence we saw:
		return new Move(lastRow,lastLeft,lastLeft);
	}


}
