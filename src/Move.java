/**
 *
 * @author xelanos
 */
public class Move {
    private int row;
    private int leftBound;
    private int rightBound;


    public Move(int inRow, int inLeft, int inRight){
        row = inRow;
        leftBound = inLeft;
        rightBound = inRight;

    }

    /**
     * string representation
     * @return string representation
     */
    public String toString(){
        return row+":"+ leftBound +"-"+ rightBound;
    }

    /**
     * Left Bound getter
     * @return the integer of the left bound
     */
    public int getLeftBound() {
        return leftBound;
    }

    /**
     * Right bound getter
     * @return the integer of right bound
     */
    public int getRightBound() {
        return rightBound;
    }

    /**
     * Row getter
     * @return the integer of the row.
     */
    public int getRow() {
        return row;
    }
}
