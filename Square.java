package amazons;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import static java.lang.Math.abs;

import static amazons.Utils.*;

/** Represents a position on an Amazons board.  Positions are numbered
 *  from 0 (lower-left corner) to 99 (upper-right corner).  Squares
 *  are immutable and unique: there is precisely one square created for
 *  each distinct position.  Clients create squares using the factory method
 *  sq, not the constructor.  Because there is a unique Square object for each
 *  position, you can freely use the cheap == operator (rather than the
 *  .equals method) to compare Squares, and the program does not waste time
 *  creating the same square over and over again.
 *  @author
 */
final class Square {

    /** The regular expression for a square designation (e.g.,
     *  a3). For convenience, it is in parentheses to make it a
     *  group.  This subpattern is intended to be incorporated into
     *  other pattern that contain square designations (such as
     *  patterns for moves). */
    static final String SQ = "([a-j](?:[1-9]|10))";

    /** Return my row position, where 0 is the bottom row. */
    int row() {
        return _row;
    }

    /** Return my column position, where 0 is the leftmost column. */
    int col() {
        return _col;
    }

    /** Return my index position (0-99).  0 represents square a1, and 99
     *  is square j10. */
    int index() {
        return _index;
    }

    /** Return true iff THIS - TO is a valid queen move. */
    boolean isQueenMove(Square to) {
        if(to == null) {
            return false;
        }
        if(!exists(this.col(), this.row()) || !exists(to.col(), to.row())){
            return false;
        }
        int tocol = to._col;
        int torow = to._row;
        int thiscol = this._col;
        int thisrow = this._row;
        int x = abs(tocol - thiscol);
        int y = abs(torow - thisrow);
        boolean ismove = false;
        if ((tocol == thiscol) && (torow == thisrow)){
            return false;
        }
        if (tocol != thiscol && torow != thisrow && x != y){
            return false;
        }
        else if ((tocol == thiscol) || (torow == thisrow) || (x == y)){
            ismove = true;
        }
        return ismove;
    }

    /** Definitions of direction for queenMove.  DIR[k] = (dcol, drow)
     *  means that to going one step from (col, row) in direction k,
     *  brings us to (col + dcol, row + drow). */
    private static final int[][] DIR = {
        { 0, 1 }, { 1, 1 }, { 1, 0 }, { 1, -1 },
        { 0, -1 }, { -1, -1 }, { -1, 0 }, { -1, 1 }
    };

    /** Return the Square that is STEPS>0 squares away from me in direction
     *  DIR, or null if there is no such square.
     *  DIR = 0 for north, 1 for northeast, 2 for east, etc., up to 7 for west.
     *  If DIR has another value, return null. Thus, unless the result
     *  is null the resulting square is a queen move away rom me.
     *  0 = north
     *  1 = northeast
     *  2 = east
     *  3 = southeast
     *  4 = south
     *  5 = southwest
     *  6 = west
     *  7 = northwest
     *   DIR = 0 for north, 1 for northeast, 2 for east, etc., up to 7 for
     *      *  northwest. If DIR has another value, return null. Thus, unless the
     *      *  result is null the resulting square is a queen move away from me. */


    Square queenMove(int dir, int steps) {
    if (dir < 0 || dir > 7){
        return null;
    }
    Square curr = null;
    int[] x = DIR[dir];
    int colx = x[0];
    int rowx = x[1];
    int addcol = colx * steps;
    int addrow = rowx * steps;
    if (exists(this.col() + addcol, this.row() + addrow)){
        curr =  sq(this.col()+addcol, this.row() + addrow);
    }
    else{
        return null;
    }
    return curr;
    }

    /** Return the direction (an int as defined in the documentation
     *  for queenMove) of the queen move THIS-TO. */
    int direction(Square to) {
//        assert isQueenMove(to);

        int dir = 0;
        int rowdiffs = to.row() - this.row();
        int coldiffs = to.col() - this.col();


        if (coldiffs == 0){
            if (rowdiffs > 0){
                dir = 0;
            }
            if (rowdiffs < 0){
                dir = 4;
            }
        }

        else if (rowdiffs == 0){
            if (coldiffs > 0){
                dir = 2;
            }
            if (coldiffs < 0){
                dir = 6;
            }
        }

        else {
//            int rowx = rowdiffs/rowdiffs;
//            int colx = coldiffs/coldiffs;
            if (coldiffs > 0){
                if (rowdiffs > 0){
                    dir = 1;
                }
                else{
                    dir = 3;
                }
            }
            else{
                if (rowdiffs > 0){
                    dir = 7;
                }
                else{
                    dir = 5;
                }
            }
        }

        return dir;  // FIXME
    }

    @Override
    public String toString() {
        return _str;
    }

    /** Return true iff COL ROW is a legal square. */
    static boolean exists(int col, int row) {
        return row >= 0 && col >= 0 && row < Board.SIZE && col < Board.SIZE;
    }

    /** Return the (unique) Square denoting COL ROW. */
    static Square sq(int col, int row) {
        if (!exists(row, col)) {
            throw error("row or column out of bounds");
        }
        return sq(col + (row * 10));  // FIXME
    }

    /** Return the (unique) Square denoting the position with index INDEX. */
    static Square sq(int index) {
        return SQUARES[index];
    }

    /** Return the (unique) Square denoting the position COL ROW, where
     *  COL ROW is the standard text format for a square (e.g., a4). */
    static Square sq(String col, String row) {
        String posn = col + row;
        return sq(posn); // updated
    }

    /** Return the (unique) Square denoting the position in POSN, in the
     *  standard text format for a square (e.g. a4). POSN must be a
     *  valid square designation. */
    static Square sq(String posn) {
        assert posn.matches(SQ);
        int row = Integer.valueOf(posn.substring(1)) - 1;
//        int row = (int)(posn.charAt(1));
//        System.out.println("row : " + row);
        int col = (int)(posn.charAt(0) - 97);
//        System.out.println("col : " + col);
        return sq(col, row);  // updated
    }

    /** Return an iterator over all Squares. */
    static Iterator<Square> iterator() {
        return SQUARE_LIST.iterator();
    }

    /** Return the Square with index INDEX. */
    private Square(int index) {
        _index = index;
        _row = index / 10;  // updated
        _col = index % 10;  // updated
        _str = Character.toString((char)((97 + _col))) + (_row + 1); // updated
    }

    /** The cache of all created squares, by index. */
    private static final Square[] SQUARES =
        new Square[Board.SIZE * Board.SIZE];

    /** SQUARES viewed as a List. */
    private static final List<Square> SQUARE_LIST = Arrays.asList(SQUARES);

    static {
        for (int i = Board.SIZE * Board.SIZE - 1; i >= 0; i -= 1) {
            SQUARES[i] = new Square(i);
        }
    }

    /** My index position. */
    private final int _index;

    /** My row and column (redundant, since these are determined by _index). */
    private final int _row, _col;

    /** My String denotation. */
    private final String _str;

}
