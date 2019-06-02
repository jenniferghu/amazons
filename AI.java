package amazons;


import static java.lang.Math.*;


import java.util.Iterator;


import static amazons.Piece.*;


/** A Player that automatically generates moves.
 *  @author Jen Hu
 */
class AI extends Player {

    /**
     * A position magnitude indicating a win (for white if positive, black
     * if negative).
     */
    private static final int WINNING_VALUE = Integer.MAX_VALUE - 1;
    /**
     * A magnitude greater than a normal value.
     */
    private static final int INFTY = Integer.MAX_VALUE;

    /**
     * Iterator for squares.
     */
    private Iterator<Square> squares;

    /**
     * Number of moves.
     */
    private int numMoves;

    /**
     * A new AI with no piece or controller (intended to produce
     * a template).
     */
    AI() {
        this(null, null);
    }

    /**
     * A new AI playing PIECE under control of CONTROLLER.
     */
    AI(Piece piece, Controller controller) {
        super(piece, controller);
    }

    @Override
    Player create(Piece piece, Controller controller) {
        return new AI(piece, controller);
    }

    @Override
    String myMove() {
        Move move = findMove();
        _controller.reportMove(move);
        return move.toString();
    }

    /**
     * Return a move for me from the current position, assuming there
     * is a move.
     */
    private Move findMove() {
        Board b = new Board(board());
        //if (_myPiece == WHITE) {
        findMove(b, maxDepth(b), true, 1, -INFTY, INFTY);
        //} else {
        //    findMove(b, maxDepth(b), true, -1, -INFTY, INFTY);
        //}
        return _lastFoundMove;
    }

    /**
     * The move found by the last call to one of the ...FindMove methods
     * below.
     */
    private Move _lastFoundMove;

    /**
     * Find a move from position BOARD and return its value, recording
     * the move found in _lastFoundMove iff SAVEMOVE. The move
     * should have maximal value or have value > BETA if SENSE==1,
     * and minimal value or value < ALPHA if SENSE==-1. Searches up to
     * DEPTH levels.  Searching at level 0 simply returns a static estimate
     * of the board value and does not set _lastMoveFound.
     */
    private int findMove(Board board, int depth, boolean saveMove, int sense,
                         int alpha, int beta) {

        if (depth == 0 || board.winner() != null && board.winner() != EMPTY) {

            if (sense == 1) {
                return staticScore(board);
            } else {
                return -staticScore(board);
            }
        }
        if (sense == 1) {
            Iterator<Move> moves = board.legalMoves(_myPiece);
            int bestVal = Integer.MIN_VALUE;
            while (moves.hasNext()) {
                Move check = moves.next();
                int oldval = bestVal;
                board.makeMove(check);
                bestVal = max(bestVal, findMove(board, depth - 1,
                        false, sense * -1, alpha, beta));
                if (saveMove && oldval < bestVal) {
                    _lastFoundMove = check;
                }
                board.undo();
                if (bestVal > beta) {
                    return bestVal;
                }
                alpha = max(alpha, bestVal);
            }
            return bestVal;
        } else {
            int bestVal2 = Integer.MAX_VALUE;
            Iterator<Move> moves2 = board.legalMoves(_myPiece);
            while (moves2.hasNext()) {
                Move check = moves2.next();
                board.makeMove(check);
                int oldval = bestVal2;
                bestVal2 = min(bestVal2, findMove(board, depth - 1,
                        false, sense * -1, alpha, beta));
                if (saveMove && oldval < bestVal2) {
                    _lastFoundMove = check;
                }
                board.undo();
                if (bestVal2 < alpha) {
                    return bestVal2;
                }
                beta = min(beta, bestVal2);
            }
            return bestVal2;
        }
    }

    /**
     * Return a heuristically determined maximum search depth
     * based on characteristics of BOARD.
     */
    private int maxDepth(Board board) {
        int N = board.numMoves();
        if (N < 50) {
            return 1;
        } else if (N < 65) {
            return 2;
        } else if (N < 75) {
            return 3;
        } else if (N < 85) {
            return 4;
        } else {
            return 5;
        }

    }


    /**
     * Return a heuristic value for BOARD.
     */
    private int staticScore(Board board) {
        int queenWhiteCount = 0;
        int whiteMoves = 0;
        int queenBlackCount = 0;
        int blackMoves = 0;

        Piece winner = board.winner();
        if (winner == BLACK) {
            return -WINNING_VALUE;
        } else if (winner == WHITE) {
            return WINNING_VALUE;
        }
        squares = Square.iterator();
        while (squares.hasNext() && queenWhiteCount
                < 5 && queenBlackCount < 5) {
            Square curr = squares.next();
            if (board.get(curr) == WHITE) {
                queenWhiteCount += 1;
                Iterator<Square> iterator = board.reachableFrom(curr, curr);
                while (iterator.hasNext()) {
                    whiteMoves += 1;
                    iterator.next();
                }

            }
            if (board.get(curr) == BLACK) {
                queenBlackCount += 1;
                Iterator<Square> iterator = board.reachableFrom(curr, curr);
                while (iterator.hasNext()) {
                    blackMoves += 1;
                    iterator.next();
                }

            }
            numMoves = whiteMoves - blackMoves;

        }
        return numMoves;
    }


}


//            return numMoves;


//        int queenWhiteCount = 0;
//        int queen
//        int count = 0;
//
//        Piece winner = board.winner();
//        if (winner == BLACK) {
//            return -WINNING_VALUE;
//        } else if (winner == WHITE) {
//            return WINNING_VALUE;
//        }
//        squares = Square.iterator();
//        while(squares.hasNext() && queenWhiteCount < 5 && Qu){
//            Square curr = squares.next();
//            if (board.get(curr) == WHITE){
//                queenWhiteCount += 1;
//                Iterator <Square> iterator = board.reachableFrom(curr, curr);
//                numMoves += Iterators.size(iterator);
//            }
//
//        }
//
//        return numMoves;
//        return 0;