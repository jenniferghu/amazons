package amazons;

import java.io.PrintStream;
import java.util.Random;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.function.Consumer;

import static amazons.Utils.*;
import static amazons.Piece.*;
import static amazons.Square.sq;
import static amazons.Square.SQ;

/** The input/output and GUI controller for play of Amazons.
 *  @author */
final class Controller {

    /** Controller for one or more games of Amazons, using
     *  MANUALPLAYERTEMPLATE as an exemplar for manual players
     *  (see the Player.create method) and AUTOPLAYERTEMPLATE
     *  as an exemplar for automated players.  Reports
     *  board changes to VIEW at appropriate points.  Uses REPORTER
     *  to report moves, wins, and errors to user. If LOGFILE is
     *  non-null, copies all commands to it. If STRICT, exits the
     *  program with non-zero code on receiving an erroneous move from a
     *  player. */
    Controller(View view, PrintStream logFile, Reporter reporter,
               Player manualPlayerTemplate, Player autoPlayerTemplate) {
        _view = view;
        _playing = false;
        _logFile = logFile;
        _input = new Scanner(System.in);
        _autoPlayerTemplate = autoPlayerTemplate;
        _manualPlayerTemplate = manualPlayerTemplate;
        _nonPlayer = manualPlayerTemplate.create(EMPTY, this);
        _reporter = reporter;
    }

    /** Play Amazons. */
    void play() {
        _playing = true;
        _winner = null;
        _board.init();
        _white = _manualPlayerTemplate.create(WHITE, this);
        _black = _autoPlayerTemplate.create(BLACK, this);
        while (_playing) {
            _view.update(_board);
            String command;
            if (_winner == null) {
                if (_board.turn() == WHITE) {
//                    System.out.println("white move is called");
                    command = _white.myMove();
                } else {
//                    System.out.println("black move is called");
                    command = _black.myMove();
                }
            } else {
                command = _nonPlayer.myMove();
                if (command == null) {
                    command = "quit";
                }
            }
            try {
                executeCommand(command);
            } catch (IllegalArgumentException excp) {
                reportError("Error: %s%n", excp.getMessage());
            }
        }
        if (_logFile != null) {
            _logFile.close();
        }
    }

    /** Return the current board.  The value returned should not be
     *  modified by the caller. */
    Board board() {
        return _board;
    }

    /** Return a random integer in the range 0 inclusive to U, exclusive.
     *  Available for use by AIs that use random selections in some cases.
     *  Once setRandomSeed is called with a particular value, this method
     *  will always return the same sequence of values. */
    int randInt(int U) {
        return _randGen.nextInt(U);
    }

    /** Re-seed the pseudo-random number generator (PRNG) that supplies randInt
     *  with the value SEED. Identical seeds produce identical sequences.
     *  Initially, the PRNG is randomly seeded. */
    void setSeed(long seed) {
        _randGen.setSeed(seed);
    }

    /** Return the next line of input, or null if there is no more. First
     *  prompts for the line.  Trims the returned line (if any) of all
     *  leading and trailing whitespace. */
    String readLine() {
        System.out.print("> ");
        System.out.flush();
        if (_input.hasNextLine()) {
            return _input.nextLine().trim();
        } else {
            return null;
        }
    }

    /** Report error by calling reportError(FORMAT, ARGS) on my reporter. */
    void reportError(String format, Object... args) {
        _reporter.reportError(format, args);
    }

    /** Report note by calling reportNote(FORMAT, ARGS) on my reporter. */
    void reportNote(String format, Object... args) {
        _reporter.reportNote(format, args);
    }

    /** Report move by calling reportMove(MOVE) on my reporter. */
    void reportMove(Move move) {
        _reporter.reportMove(move);
    }

    /** A Command is pair (<pattern>, <processor>), where <pattern> is a
     *  Matcher that matches instances of a particular command, and
     *  <processor> is a functional object whose .accept method takes a
     *  successfully matched Matcher and performs some operation. */
    private static class Command {
        /** A new Command that matches PATN (a regular expression) and uses
         *  PROCESSOR to process commands that match the pattern. */
        Command(String patn, Consumer<Matcher> processor) {
            _matcher = Pattern.compile(patn).matcher("");
            _processor = processor;
        }

        /** A Matcher matching my pattern. */
        protected final Matcher _matcher;
        /** The function object that implements my command. */
        protected final Consumer<Matcher> _processor;
    }

    /** A list of Commands describing the valid textual commands to the
     *  Amazons program and the methods to process them. */
    private Command[] _commands = {
        new Command("quit$", this::doQuit),
        new Command("seed\\s+(\\d+)$", this::doSeed),
        new Command("dump$", this::doDump),
//        new Command("move\\s+[a-j](?:[1-9]|10)\\s+[a-j](?:[1-9]|10)\\s+[a-j](?:[1-9]|10)$", this::doMove),
//        new Command("move\\s+([a-j](?:[1-9]|10))\\s+([a-j](?:[1-9]|10))\\s+([a-j](?:[1-9]|10))$", this::doMove),
        new Command("manual\\s([a-z]+)$", this::manualMove),
        new Command("auto\\s([a-z]+)$", this::autoMove),
        new Command("new$", this::doNew),
        new Command("([a-z]\\d+)\\s?[-]?([a-z]\\d+)\\s?[(]?([a-z]\\d+)[)]?$", this::doMove),



        // FIXME
    };

    /** A Matcher whose Pattern matches comments. */
    private final Matcher _comment = Pattern.compile("#.*").matcher("");

    /** Check that CMND is one of the valid Amazons commands and execute it, if
     *  so, raising an IllegalArgumentException otherwise. */
    private void executeCommand(String cmnd) {
        if (_logFile != null) {
            _logFile.println(cmnd);
            _logFile.flush();
        }

        _comment.reset(cmnd);
        cmnd = _comment.replaceFirst("").trim().toLowerCase();

        if (cmnd.isEmpty()) {
            return;
        }
        for (Command parser : _commands) {
            parser._matcher.reset(cmnd);
            if (parser._matcher.matches()) {
                parser._processor.accept(parser._matcher);
                return;
            }
        }
        throw error("Bad command: %s", cmnd);
    }

    /** Command "new". */
    private void doNew(Matcher unused) {
        _board.init();
        _winner = null;
    }


    /** Command "move". */
    private void doMove(Matcher mat) {
//        System.out.println("is do move being called");
        String a = mat.group(1);
        String b = mat.group(2);
        String c = mat.group(3);
//        System.out.println(a);
//        String updated = a.substring(5);
//        String from = updated.substring(0, 2);
//        String to = updated.substring(3, 5);
//        String spear = updated.substring(6, 8);
//        System.out.println(updated);
//        System.out.println(from);
//        System.out.println(to);
//        System.out.println(spear);
//

//        System.out.println(a);
//        System.out.println(b);
//        System.out.println(c);
        Square fromsquare = sq(a);
        Square tosquare = sq(b);
        Square tospear = sq(c);
//        if (_board.winner() == null){
//        if (_board.winner() == null  && _board.isUnblockedMove(fromsquare, tosquare, tospear) && _board.isLegal(fromsquare, tosquare, tospear)){
            _board.makeMove(fromsquare, tosquare, tospear);
//            System.out.println(_board);
//            System.out.println(_board.winner());
//        } else {
//new
        if (_winner == null){
            _winner = board().winner();
            if (_winner == WHITE){
                reportNote("White wins.");
            }
            else if(_winner == BLACK){
                reportNote("Black wins.");
            }
//            System.out.println(_board.winner());
        }
//        if (board().winner() == BLACK){
//            _winner = BLACK;
//            reportNote("Black wins");
//        }
//        else if( board().winner() == WHITE){
//            _winner = WHITE;
//            reportNote("White ");
//        }
//        System.out.println(fromsquare);
//        System.out.println(fromsquare.col() + fromsquare.row());
//        System.out.println(tosquare);
//        System.out.println(tospear);
//        Square from = mat.group(0);

//        Move move = Move.mv(updated);
//         _board.makeMove(move);


    }

    /** Command "quit". */
    private void doQuit(Matcher unused) {
        _playing = false;
    }

    /** Command "seed N" where N is the first group of MAT. */
    private void doSeed(Matcher mat) {
        try {
            setSeed(Long.parseLong(mat.group(1)));
        } catch (NumberFormatException excp) {
            throw error("number too large");
        }
    }

    /** Dump the contents of the board on standard output. */
    private void doDump(Matcher unused) {
        System.out.printf("===%n%s===%n", _board);
    }

    private void manualMove(Matcher mat){
        String color = mat.group(1).toUpperCase();
//        System.out.println(color);
        if (color.equals("WHITE")){
//            System.out.println("Manual white");
            _white = _manualPlayerTemplate.create(WHITE, this);
        }
        else if (color.equals("BLACK")){
//            System.out.println("Manual black");
            _black = _manualPlayerTemplate.create(BLACK, this);
        }

    }

    private void autoMove(Matcher mat){
        String color = mat.group(1).toUpperCase();
        if (color.equals("WHITE")){
            _white = _autoPlayerTemplate.create(WHITE, this);
//            System.out.println(color);
        }
        else if (color.equals("BLACK")){
            _black = _autoPlayerTemplate.create(BLACK, this);
//            System.out.println(color);
        }

    }


    /** The board. */
    private Board _board = new Board();

    /** The winning side of the current game. */
    private Piece _winner;

    /** True while game is still active. */
    private boolean _playing;

    /** The object that is displaying the current game. */
    private View _view;

    /** My pseudo-random number generator. */
    private Random _randGen = new Random();

    /** Log file, or null if absent. */
    private PrintStream _logFile;

    /** Input source. */
    private Scanner _input;

    /** The current White and Black players, each created from
     *  _autoPlayerTemplate or _manualPlayerTemplate. */
    private Player _white, _black;

    /** A dummy Player used to return commands but not moves when no
     *  game is in progress. */
    private Player _nonPlayer;

    /** The current templates for manual and automated players. */
    private Player _autoPlayerTemplate, _manualPlayerTemplate;

    /** Reporter for messages and errors. */
    private Reporter _reporter;

}
