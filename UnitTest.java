package amazons;

import org.junit.Test;

import static amazons.Piece.*;
import static amazons.Board.*;
import static org.junit.Assert.*;
import ucb.junit.textui;

import java.util.Iterator;

/** The suite of all JUnit tests for the amazons package.
 *  @author
 */
public class UnitTest {

    /** Run the JUnit tests in this package. Add xxxTest.class entries to
     *  the arguments of runClasses to run other JUnit tests. */
    public static void main(String[] ignored) {
        textui.runClasses(UnitTest.class);
    }

    /** Tests basic correctness of put and get on the initialized board. */
    @Test
    public void testBasicPutGet() {
        Board b = new Board();
        b.put(BLACK, Square.sq(3, 5));
        assertEquals(BLACK, b.get(3, 5));
        b.put(WHITE, Square.sq(9, 9));
        assertEquals(b.get(9, 9), WHITE);
        b.put(EMPTY, Square.sq(3, 5));
        assertEquals(b.get(3, 5), EMPTY);
    }

    /** Tests stack. */
    @Test
    public void testStack() {
        Board b = new Board();
        b.makeMove(Square.sq(3), Square.sq(4), Square.sq(14));
        b.makeMove(Square.sq(6), Square.sq(17), Square.sq(14));
        b.undo();
        assertEquals(1, b.numMoves());
//        b.makeMove(Square.sq(93), Square.sq(84), Square.sq(74));
//        assertEquals(3, b.numMoves());
//        b.undo();
//        assertEquals(3, b.numMoves());
    }

    /** Tests proper identification of legal/illegal queen moves. */
    @Test
    public void testIsQueenMove() {
        assertFalse(Square.sq(1, 5).isQueenMove(Square.sq(1, 5)));
        assertFalse(Square.sq(1, 5).isQueenMove(Square.sq(2, 7)));
        assertFalse(Square.sq(0, 0).isQueenMove(Square.sq(5, 1)));
        assertTrue(Square.sq(1, 1).isQueenMove(Square.sq(9, 9)));
        assertTrue(Square.sq(2, 7).isQueenMove(Square.sq(8, 7)));
        assertTrue(Square.sq(3, 0).isQueenMove(Square.sq(3, 4)));
        assertTrue(Square.sq(7, 9).isQueenMove(Square.sq(0, 2)));
        assertTrue(Square.sq(3).isQueenMove(Square.sq(12)));
        assertTrue(Square.sq(3).isQueenMove(Square.sq(14)));
    }

    /** Tests proper identification of unblocked moves. */
    @Test
    public void testUnblocked(){
        Board b = new Board();
        b.makeMove(Square.sq(93), Square.sq(43), Square.sq(45));
        boolean check1 = b.isUnblockedMove(Square.sq(3), Square.sq(53), null);
        assertFalse(check1);
        boolean check2 = b.isUnblockedMove(Square.sq(3), Square.sq(5), null);
        assertTrue(check2);

    }

    @Test
    public void testUnblockedMove() {
        Board b = new Board();
        b.put(BLACK, Square.sq(33));

        assertEquals(true, b.isUnblockedMove(Square.sq(3, 5), Square.sq(3, 7), null));
        assertEquals(true, b.isUnblockedMove(Square.sq(12), Square.sq(32), null));
        assertEquals(false, b.isUnblockedMove(Square.sq(32), Square.sq(38), null));
        assertEquals(false, b.isUnblockedMove(Square.sq(37), Square.sq(31), null));

    }
    @Test
    public void testIsUnblockedMove1() {
        Board b = new Board();
        makeSmile(b);
        /**This block will test non-valid-queen moves. */
        assertFalse(b.isUnblockedMove(Square.sq(0, 0), Square.sq(9, 9), Square.sq(5, 5)));
        assertFalse(b.isUnblockedMove(Square.sq(2, 7), Square.sq(2, 1), Square.sq(2, 6)));
        assertFalse(b.isUnblockedMove(Square.sq(1, 1), Square.sq(5, 3), Square.sq(0, 0)));
        assertFalse(b.isUnblockedMove(Square.sq(4, 5), Square.sq(2, 8), Square.sq(9, 9)));

        /**Now, this will test valid moves. */
//        Square from = Square.sq(0,0);
//        Square to = Square.sq(9,0);
//        Square asEmpty = Square.sq(5,0);
//        System.out.println(b.isUnblockedMove(from, to, asEmpty));
//        System.out.println(from.isQueenMove(to));
//        System.out.println(from.direction(to));
        assertTrue(b.isUnblockedMove(Square.sq(0, 0), Square.sq(9, 0), Square.sq(5, 0)));
        assertTrue(b.isUnblockedMove(Square.sq(0, 0), Square.sq(5, 5), Square.sq(3, 2)));
        assertTrue(b.isUnblockedMove(Square.sq(7, 2), Square.sq(7, 5), Square.sq(7, 3)));
        assertTrue(b.isUnblockedMove(Square.sq(9, 9), Square.sq(7, 7), Square.sq(8, 8)));
    }


    /** Tests toString for initial board state and a smiling board state. :) */
    @Test
    public void testToString() {
        Board b = new Board();
        assertEquals(INIT_BOARD_STATE, b.toString());
        makeSmile(b);
        assertEquals(SMILE, b.toString());
    }

    @Test
    public void testIterator() {
        Board b = new Board();
        Iterator<Square> iterator = b.reachableFrom(Square.sq(3), Square.sq(0));
        int count = 0;
        while (iterator.hasNext()) {
            iterator.next();
            count += 1;
        }
        System.out.println(count);
    }

    private void makeSmile(Board b) {
        b.put(EMPTY, Square.sq(0, 3));
        b.put(EMPTY, Square.sq(0, 6));
        b.put(EMPTY, Square.sq(9, 3));
        b.put(EMPTY, Square.sq(9, 6));
        b.put(EMPTY, Square.sq(3, 0));
        b.put(EMPTY, Square.sq(3, 9));
        b.put(EMPTY, Square.sq(6, 0));
        b.put(EMPTY, Square.sq(6, 9));
        for (int col = 1; col < 4; col += 1) {
            for (int row = 6; row < 9; row += 1) {
                b.put(SPEAR, Square.sq(col, row));
            }
        }
        b.put(EMPTY, Square.sq(2, 7));
        for (int col = 6; col < 9; col += 1) {
            for (int row = 6; row < 9; row += 1) {
                b.put(SPEAR, Square.sq(col, row));
            }
        }
        b.put(EMPTY, Square.sq(7, 7));
        for (int lip = 3; lip < 7; lip += 1) {
            b.put(WHITE, Square.sq(lip, 2));
        }
        b.put(WHITE, Square.sq(2, 3));
        b.put(WHITE, Square.sq(7, 3));
    }

    static final String INIT_BOARD_STATE =
            "   - - - B - - B - - -\n" +
                    "   - - - - - - - - - -\n" +
                    "   - - - - - - - - - -\n" +
                    "   B - - - - - - - - B\n" +
                    "   - - - - - - - - - -\n" +
                    "   - - - - - - - - - -\n" +
                    "   W - - - - - - - - W\n" +
                    "   - - - - - - - - - -\n" +
                    "   - - - - - - - - - -\n" +
                    "   - - - W - - W - - -\n";

    static final String SMILE =
            "   - - - - - - - - - -\n" +
                    "   - S S S - - S S S -\n" +
                    "   - S - S - - S - S -\n" +
                    "   - S S S - - S S S -\n" +
                    "   - - - - - - - - - -\n" +
                    "   - - - - - - - - - -\n" +
                    "   - - W - - - - W - -\n" +
                    "   - - - W W W W - - -\n" +
                    "   - - - - - - - - - -\n" +
                    "   - - - - - - - - - -\n";
//    @Test
//    public void testQueenMove() {
//        assertFalse(Square.sq(1, 5).isQueenMove(Square.sq(1, 5)));
//        assertFalse(Square.sq(1, 5).isQueenMove(Square.sq(2, 7)));
//        assertFalse(Square.sq(0, 0).isQueenMove(Square.sq(5, 1)));
//        assertTrue(Square.sq(1, 1).isQueenMove(Square.sq(9, 9)));
//        assertTrue(Square.sq(2, 7).isQueenMove(Square.sq(8, 7)));
//        assertTrue(Square.sq(3, 0).isQueenMove(Square.sq(3, 4)));
//        assertTrue(Square.sq(7, 9).isQueenMove(Square.sq(0, 2)));
//
//        queenMove(int dir, int steps)
//
//
//    }


}

