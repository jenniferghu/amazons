package amazons;


import java.util.*;

import static amazons.Piece.*;
import static amazons.Move.mv;
import static java.lang.Math.abs;
import static java.lang.Math.max;

import java.lang.*;


/** The state of an Amazons Game.
 *  @author Jennifer Hu
 */
class Board {

    /** The number of squares on a side of the board. */
    static final int SIZE = 10;

   /** Board representation. */
    private Piece[][] board;

    /** Number of moves. */
    private int nummoves;

    /** Stack of moves. */
    private Stack<Square[]> movelist;

    /** Square. */
    private Square check;

    /** Square. */
    private Square curr;

    /** Legal move. */
    private Move legalMove;

    /** Counts queens. */
    private int queenCount;

    /** Initializes a game board with SIZE squares on a side in the
     *  initial position. */
    Board() {
        init();
    }

    /** Initializes a copy of MODEL. */
    Board(Board model) {
        copy(model);
    }

    /** Copies MODEL into me. */
    void copy(Board model) {
        if (model == this) {
            return;
        } else {
            init();
            for (int i = 0; i < 10; i++) {
                for (int j = 0; j < 10; j++) {
                    this.board[i][j] = model.board[i][j];
                }
            }
            for (Square[] i : model.movelist) {
                this.movelist.push(i);
            }



        }

    }

    /** Clears the board to the initial position. */
    void init() {

        this.board = new Piece[10][10];
        this.movelist = new Stack<>();
        for (int col = 0; col != 10; col++) {
            for (int row = 0; row != 10; row++) {
                board[col][row] = EMPTY;
            }
        }
        put(WHITE, Square.sq(0, 3));
        put(BLACK, Square.sq(0, 6));
        put(WHITE, Square.sq(9, 3));
        put(BLACK, Square.sq(9, 6));
        put(WHITE, Square.sq(3, 0));
        put(BLACK, Square.sq(3, 9));
        put(WHITE, Square.sq(6, 0));
        put(BLACK, Square.sq(6, 9));
        _turn = WHITE;
        _winner = null;
        nummoves = 0;
    }

    /** Return the Piece whose move it is (WHITE or BLACK). */
    Piece turn() {
        return _turn;
    }

    /** Return the number of moves (that have not been undone) for this
     *  board. */
    int numMoves() {
        return movelist.size();
    }

    /** Return the winner in the current position, or null if the game is
     *  not yet finished. */
    Piece winner() {
        if (_winner == null) {
            return null;
        } else {
            return _winner;
        }
//        Iterator checkmoves= legalMoves(_turn);
//        if (!checkmoves.hasNext()){
//            _winner = _turn.opponent();
//            return  _winner;
//        }
//        else{
//            return null;
//        }
    }

    /** Return the contents the square at S. */
    final Piece get(Square s) {
        return board[s.col()][s.row()];
    }

    /** Return the contents of the square at (COL, ROW), where
     *  0 <= COL, ROW <= 9. */
    final Piece get(int col, int row) {
        return board[col][row];
    }

    /** Return the contents of the square at COL ROW. */
    final Piece get(char col, char row) {
        return get(col - 'a', row - '1');
    }

    /** Set square S to P. */
    final void put(Piece p, Square s) {
        board[s.col()][s.row()] = p;
    }

    /** Set square (COL, ROW) to P. */
    final void put(Piece p, int col, int row) {
        put(p, Square.sq(col, row));
//        _winner = EMPTY;
    }

    /** Set square COL ROW to P. */
    final void put(Piece p, char col, char row) {
        put(p, col - 'a', row - '1');
    }

    /** Return true iff FROM - TO is an unblocked queen move on the current
     *  board, ignoring the contents of ASEMPTY, if it is encountered.
     *  For this to be true, FROM-TO must be a queen move and the
     *  squares along it, other than FROM and ASEMPTY, must be
     *  empty. ASEMPTY may be null, in which case it has no effect. */
    boolean isUnblockedMove(Square from, Square to, Square asEmpty) {
        boolean valid = false;
        if (!from.isQueenMove(to)) {
            return false;
        }
        int dir = from.direction(to);
        float steps = max(abs(from.col() - to.col()),
                abs(from.row() - to.row()));
        for (int i = 1; i <= steps; i++) {
            Square check = from.queenMove(dir, i);
            if (!from.isQueenMove(check)) {
                return false;
            }
            if (from == to) {
                return false;
            }
            if (get(check) != EMPTY && check != asEmpty) {
                return false;
            }
            if (get(check) != EMPTY && check == asEmpty) {
                valid = true;
            }
            if (check.row() > 9 || check.row() < 0 || check.col() > 9
                    || check.row() < 0) {
                return false;
            }
            if (get(check) == EMPTY) {
                valid = true;
            }

        }
//            if (!from.isQueenMove(to)){
//                return false;
//            }if (!from.isQueenMove(from.queenMove(dir, i))){
//                return false;
//            }if (from == to){
//                return false;
//            } Square check = from.queenMove(dir, i);
//            if (check.row() > 9 || check.row() < 0
// || check.col() > 9 || check.row() < 0){
//                return false;
//            }else if (board[check.col()][check.row()] == EMPTY) {
//                valid = true;
//            }
//            else if ((board[check.col()][check.row()] == WHITE
// || board[check.col()][check.row()] == BLACK)
//                    && board[check.col()][check.row()]
// == board[asEmpty.col()][asEmpty.row()]){
//                valid = true;
//            }
//            else if (check.isQueenMove(to) && board[check.col()][check.row()]
// != EMPTY && board[check.col()][check.row()]
// == board[asEmpty.col()][asEmpty.row()]) {
//                valid = true;
//            }
//                return false;
//
//        }
        return valid;
    }
    /** Checks if next square is unblocked. */
    boolean unblocked(Square to) {
        if (board[to.col()][to.row()] != EMPTY) {
            return false;
        } else {
            return true;
        }
    }


    /** Return true iff FROM is a valid starting square for a move. */
    boolean isLegal(Square from) {
        Boolean validstart = false;

        if (board[from.col()][from.row()] == SPEAR) {
            return false;
        }
        if (turn() == BLACK) {
            if (board[from.col()][from.row()] == BLACK) {
                validstart = true;
            } else {
                return false;
            }
        }
        if (turn() == WHITE) {
            if (board[from.col()][from.row()] == WHITE) {
                validstart = true;
            } else {
                return false;
            }
        }


        return validstart;
    }

    /** Return true iff FROM-TO is a valid first part of move, ignoring
     *  spear throwing. */
    boolean isLegal(Square from, Square to) {
        return isUnblockedMove(from, to, null);
    }

    /** Return true iff FROM-TO(SPEAR) is a legal move in the current
     *  position. */
    boolean isLegal(Square from, Square to, Square spear) {
        return isUnblockedMove(from, to, from);

    }

    /** Return true iff MOVE is a legal move in the current
     *  position. */
    boolean isLegal(Move move) {
        if (move == null) {
            return false;
        } else {
            Square movefrom = move.from();
            Square moveto = move.to();
            return isLegal(movefrom, moveto);
        }
    }

    /** Move FROM-TO(SPEAR), assuming this is a legal move. */
    void makeMove(Square from, Square to, Square spear) {
        if (!isLegal(from, to, spear)) {
            return;
        }
        Square[] addmoves = {from, to, spear};
        movelist.push(addmoves);
        if (get(from) == BLACK) {
            put(BLACK, to);
        } else {
            put(WHITE, to);
        }
        put(EMPTY, from);
        put(SPEAR, spear);
        _turn = _turn.opponent();
        Iterator checkmoves = legalMoves(_turn);
        if (!checkmoves.hasNext()) {
            _winner = _turn.opponent();
        } else {
            _winner = null;
        }

    }

    /** Move according to MOVE, assuming it is a legal move. */
    void makeMove(Move move) {
        makeMove(move.from(), move.to(), move.spear());

    }

    /** Undo one move.  Has no effect on the initial board. */
    void undo() {
        if (numMoves() > 0) {

            Square[] moves = movelist.pop();
            Square from = moves[0];
            Square to = moves[1];
            Square spear = moves[2];
            Piece turn = get(from);
            Piece putval = get(to);
            put(EMPTY, to);
            put(EMPTY, spear);
            put(putval, from);
            turn().opponent();
//            _winner = null;
        }

    }

    /** Return an Iterator over the Squares that are reachable by an
     *  unblocked queen move from FROM. Does not pay attention to what
     *  piece (if any) is on FROM, nor to whether the game is finished.
     *  Treats square ASEMPTY (if non-null) as if it were EMPTY.  (This
     *  feature is useful when looking for Moves, because after moving a
     *  piece, one wants to treat the Square it came from as empty for
     *  purposes of spear throwing.) */
    Iterator<Square> reachableFrom(Square from, Square asEmpty) {
        return new ReachableFromIterator(from, asEmpty);
    }

    /** Return an Iterator over all legal moves on the current board. */
    Iterator<Move> legalMoves() {
        System.out.println("turn" + _turn);
        return new LegalMoveIterator(_turn);
    }

    /** Return an Iterator over all legal moves on the current board for
     *  SIDE (regardless of whose turn it is). */
    Iterator<Move> legalMoves(Piece side) {
        return new LegalMoveIterator(side);
    }

    /** An iterator used by reachableFrom. */
    private class ReachableFromIterator implements Iterator<Square> {

        /** Iterator of all squares reachable by queen move from FROM,
         *  treating ASEMPTY as empty. */
        ReachableFromIterator(Square from, Square asEmpty) {
//            _from = from;
//            _dir = 0;
//            _steps = 0;
//            _asEmpty = asEmpty;
//            toNext();

            _from = from;
            _dir = 0;
            _steps = 1;
            _asEmpty = asEmpty;
            hasNext();
        }


        @Override
        public boolean hasNext() {
            if (_dir < 8 && _dir >= 0) {
                Square check = _from.queenMove(_dir, _steps);
                if ( check == null || board[check.col()][check.row()]
                        != EMPTY && check !=_asEmpty) {
                    _dir += 1;
                    _steps = 1;
                    return hasNext();
                }
                return true;
            }
            return false;
        }

        @Override
        public Square next() {
//            check = _from.queenMove(_dir,_steps);
//            toNext();
//            return check;

            check = _from.queenMove(_dir, _steps);
            _steps += 1;
            return check;
        }

        /** Advance _dir and _steps, so that the next valid Square is
         *  _steps steps in direction _dir from _from. */
        private void toNext() {
            if (_dir > 7) {
                return;
            } else if (!_from.isQueenMove(_from.queenMove(_dir, _steps + 1))
                    || !unblocked(_from.queenMove(_dir, _steps + 1))) {
                _steps = 0;
                _dir += 1;
                toNext();
            } else {
                _steps += 1;
            }
//            System.out.println("---------------------");
//            System.out.println("toNext :" + _dir);
//                boolean canmove = false;
//            System.out.println("steps mf: " + _steps);
//            System.out.println(_from.isQueenMove
// (_from.queenMove(_dir, _steps)));
//                if(_from.isQueenMove(_from.queenMove(_dir, _steps))){
//                    check = _from.queenMove(_dir,_steps);
//                    if (board[check.col()][check.row()] == WHITE
// || board[check.col()][check.row()] == BLACK){
//                        canmove = false;
////                        _steps = 1;
////                        _dir += 1;
//                        System.out.println("Checking if this is
// valid move(may be false) : " + check);
//                        System.out.println("Steps : " + _steps);
//                    }
//                    else {
//                        System.out.println("Checking if this is
// valid move(may be false) : " + check);
//                        System.out.println("Steps : " + _steps);
//                        canmove = true;
//                    }
//                }
//            System.out.println("Direction : " + _dir);
//            System.out.println("Canmove : " + canmove);
//            System.out.println("Move is unblocked : "
// +isUnblockedMove(_from, check, _asEmpty));
//                if (canmove && isUnblockedMove(_from, check, _asEmpty)){
//                    _steps += 1;
//                    if (_from.isQueenMove
// (_from.queenMove(_dir, _steps)) == false){
//                        _steps = 1;
//                        _dir += 1;
//                    }
//                    System.out.println("Valid(canmove is true : " + check);
//                }
//                else {
//                    _steps = 1;
//                    _dir += 1;
//                }
            }


        /** Starting square. */
        private Square _from;
        /** Current direction. */
        private int _dir;
        /** Current distance. */
        private int _steps;
        /** Square treated as empty. */
        private Square _asEmpty;
    }

    /** An iterator used by legalMoves. */
    private class LegalMoveIterator implements Iterator<Move> {

        /** All legal moves for SIDE (WHITE or BLACK). */
        LegalMoveIterator(Piece side) {
            _startingSquares = Square.iterator();
            _spearThrows = NO_SQUARES;
            _pieceMoves = NO_SQUARES;
            _fromPiece = side;
            toNext();
        }

        @Override
        /** Will return false when there are no more spearthrows, no more piecemoves, and no more queens.. */
        public boolean hasNext() {
//        if (!_spearThrows.hasNext() && !_pieceMoves.hasNext() && queenCount > 4){
//            return false;
//        }
//        else if(!_spearThrows.hasNext() && _pieceMoves.hasNext()){
//            return true;
//        }
//        else if(_spearThrows.hasNext() && _pieceMoves.hasNext()){
//            return true;
//        }
            //        else if(!_spearThrows.hasNext() && _pieceMoves.hasNext()){
//            return true;
//        }
        if (!_spearThrows.hasNext()) {
            return false;
        }

            return true;
        }



        @Override
        public Move next() {
            legalMove = mv(_start, _nextSquare, _spearThrows.next());
            toNext();
            return legalMove;
        }

        /** Advance so that the next valid Move is
         *  _start-_nextSquare(sp), where sp is the next value of
         *  _spearThrows. */
        private void toNext() {


//            while (!_spearThrows.hasNext()){
//                if ( _spearThrows.hasNext()){
//                    break;
//                }
//                else if(_pieceMoves.hasNext()){
//            }


//            if (!_pieceMoves.hasNext()) {
//                curr = _startingSquares.next();
//                if (board[curr.col()][curr.row()] == _fromPiece) {
//                    _start = Square.sq(curr.col(), curr.row());
//                    _pieceMoves = reachableFrom(curr, curr);
//                } else {
//                    toNext();
//                }
//
//
//                else {
//                    if (_pieceMoves.hasNext()) {
//                        _spearThrows = reachableFrom(_nextSquare = _pieceMoves.next(), null);
//                    }
//                    legalMove = Move.mv(_start, _nextSquare, _spearThrows.next());
//
//                }
//
//
//            }

            if (!_spearThrows.hasNext()) {
                if (!_pieceMoves.hasNext()) {
                    if ( _startingSquares.hasNext()) {
                        curr = _startingSquares.next();
                        if (board[curr.col()][curr.row()] == _fromPiece) {
                            _start = curr;
                            _pieceMoves = reachableFrom(_start, null);
                            toNext();
                        } else{
                            toNext();
                        }
                    } else {
                        return;
                    }
                } else{
                   _nextSquare =  _pieceMoves.next();
                    _spearThrows = reachableFrom(_nextSquare , _start);
                    toNext();
                }
            }
        }

        /** Color of side whose moves we are iterating. */
        private Piece _fromPiece;
        /** Current starting square. */
        private Square _start;
        /** Remaining starting squares to consider. */
        private Iterator<Square> _startingSquares;
        /** Current piece's new position. */
        private Square _nextSquare;
        /** Remaining moves from _start to consider. */
        private Iterator<Square> _pieceMoves;
        /** Remaining spear throws from _piece to consider. */
        private Iterator<Square> _spearThrows;
    }

    @Override
    public String toString() {
        Formatter out = new Formatter();
        for (int row = 9; row != -1; row--) {
            out.format("   ");
            for (int col = 0; col != 10; col++) {
                Piece x = get(col, row);
                String p = "";
                if (col != 9) {
                    if (x.equals(EMPTY)) {
                        p = "-";
                    } else if (x.equals(BLACK)) {
                        p = "B";
                    } else if (x.equals(WHITE)) {
                        p = "W";
                    } else if (x.equals(SPEAR)) {
                        p = "S";
                    }
                    out.format("%s ", p);
                }
                else {
                    if (x.equals(EMPTY)) {
                        p = "-";
                    } else if (x.equals(BLACK)) {
                        p = "B";
                    } else if (x.equals(WHITE)) {
                        p = "W";
                    } else if (x.equals(SPEAR)) {
                        p = "S";
                    }
                    out.format("%s", p);
                }
            }
            out.format("%s", "\n");
        }

        return out.toString();
    }

    /** An empty iterator for initialization. */
    private static final Iterator<Square> NO_SQUARES =
        Collections.emptyIterator();

    /** Piece whose turn it is (BLACK or WHITE). */
    private Piece _turn;
    /** Cached value of winner on this board, or EMPTY if it has not been
     *  computed. */
    private Piece _winner;
}
