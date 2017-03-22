import java.util.Random;

/**
 * Created by or323 on 20/03/2017.
 */
public class Test {
    public static void main(String args[]) {
        Random random = new Random();
        Board board = new Board();
        while (true){
            int randomRow = random.nextInt(board.getNumberOfRows()) + 1;
            int randomRowLength = board.getRowLength(randomRow);
            int randomLeftStick = random.nextInt(randomRowLength) + 1;
            int randomRightStick = random.nextInt(randomRowLength + 1 - randomLeftStick) + randomLeftStick;
            System.out.println(new Move(randomRow, randomLeftStick, randomRightStick));
        }
    }

}
