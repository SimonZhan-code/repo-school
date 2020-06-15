package tablut;

import java.util.ArrayList;
import java.util.Formatter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import static tablut.Piece.*;
import static tablut.Square.*;
import static tablut.Move.mv;


/**
 * The state of a Tablut Game.
 *
 * @author Simon Zhan
 */
class Board {

    /**
     * The number of squares on a side of the board.
     */
    static final int SIZE = 9;

    /**
     * The throne (or castle) square and its four surrounding squares..
     */
    static final Square THRONE = sq(4, 4),
            NTHRONE = sq(4, 5),
            STHRONE = sq(4, 3),
            WTHRONE = sq(3, 4),
            ETHRONE = sq(5, 4);

    /**
     * Initial positions of attackers.
     */
    static final Square[] INITIAL_ATTACKERS = {
            sq(0, 3), sq(0, 4), sq(0, 5), sq(1, 4),
            sq(8, 3), sq(8, 4), sq(8, 5), sq(7, 4),
            sq(3, 0), sq(4, 0), sq(5, 0), sq(4, 1),
            sq(3, 8), sq(4, 8), sq(5, 8), sq(4, 7)
    };

    /**
     * Initial positions of defenders of the king.
     */
    static final Square[] INITIAL_DEFENDERS = {
        NTHRONE, ETHRONE, STHRONE, WTHRONE,
        sq(4, 6), sq(4, 2), sq(2, 4), sq(6, 4)
    };

    /**
     * Initializes a game board with SIZE squares on a side in the
     * initial position.
     */
    Board() {
        init();
    }

    /**
     * Initializes a copy of MODEL.
     */
    Board(Board model) {
        copy(model);
    }

    /**
     * Copies MODEL into me.
     */
    void copy(Board model) {
        if (model == this) {
            return;
        }
        _board = new Piece[9][9];
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                _board[i][j] = EMPTY;
            }
        }
        _findSquares = new HashMap<>();
        _findSquares.put(BLACK, new HashSet<>());
        _findSquares.put(WHITE, new HashSet<>());
        _findSquares.put(KING, new HashSet<>());
        _turn = model._turn;
        _repeated = model._repeated;
        _moveCount = model._moveCount;
        _record = model._record;
        _memory = model._memory;

        for (Square sq : model._findSquares.get(BLACK)) {
            _board[sq.col()][sq.row()] = BLACK;
            _findSquares.get(BLACK).add(sq);
        }
        for (Square sq : model._findSquares.get(WHITE)) {
            _board[sq.col()][sq.row()] = WHITE;
            _findSquares.get(WHITE).add(sq);
        }
        Square kingPos = model.kingPosition();
        if (kingPos != null) {
            _board[kingPos.col()][kingPos.row()] = KING;
            _findSquares.get(KING).add(kingPos);
        }
    }

    /**
     * Clears the board to the initial position.
     */
    void init() {
        _turn = BLACK;
        _winner = null;
        _moveCount = 0;
        _limitmove = 0;
        _record = new ArrayList<>();
        _board = new Piece[9][9];
        _findSquares = new HashMap<>();
        _memory = new ArrayList<>();
        _findSquares.put(BLACK, new HashSet<>());
        _findSquares.put(WHITE, new HashSet<>());
        _findSquares.put(KING, new HashSet<>());

        for (int i = _board.length - 1; i >= 0; i--) {
            for (int j = 0; j < _board[i].length; j++) {
                _board[i][j] = EMPTY;
            }
        }
        for (Square sq : INITIAL_ATTACKERS) {
            _board[sq.col()][sq.row()] = BLACK;
            _findSquares.get(BLACK).add(sq);
        }
        for (Square sq : INITIAL_DEFENDERS) {
            _board[sq.col()][sq.row()] = WHITE;
            _findSquares.get(WHITE).add(sq);
        }

        _findSquares.get(KING).add(Square.sq(4, 4));
        _board[4][4] = KING;
    }

    /**
     * Set the move limit to LIM.  It is an error if 2*LIM <= moveCount().
     * @param n number to set limit.
     */
    void setMoveLimit(int n) {
        if (2 * n <= _limitmove) {
            throw new IllegalArgumentException("Wrong number of movement. ");
        }
        _limitmove = n;
    }

    /**
     * Returning findingSquares data structure.
     * @param piece Return square of each piece.
     * @return Hashset of return piece.
     */
    HashSet<Square> findingSquaresofPiece(Piece piece) {
        return _findSquares.get(piece);
    }

    /**
     * Return a Piece representing whose move it is (WHITE or BLACK).
     */
    Piece turn() {
        return _turn;
    }

    /**
     * Return the winner in the current position, or null if there is no winner
     * yet.
     */
    Piece winner() {
        return _winner;
    }

    /**
     * Returns true iff this is a win due to a repeated position.
     */
    boolean repeatedPosition() {
        return _repeated;
    }

    /**
     * Record current position and set winner() next mover if the current
     * position is a repeat.
     */
    private void checkRepeated() {
        Board last = _record.get(_record.size() - 1);
        for (int i = 0; i < _record.size() - 1; i++) {
            Board temp = _record.get(i);
            if (last.toString().equals(temp.toString())) {
                if (last.turn().equals(temp._turn)) {
                    _repeated = true;
                    _winner = _turn.opponent();
                }
            }
        }

    }

    /**
     * Return the number of moves since the initial position that have not been
     * undone.
     */
    int moveCount() {
        return _moveCount;
    }

    /**
     * Return location of the king.
     */
    Square kingPosition() {
        Object[] temp = _findSquares.get(KING).toArray();
        if (temp.length == 0) {
            return null;
        }
        return (Square) temp[0];
    }

    /**
     * Return the contents the square at S.
     */
    final Piece get(Square s) {
        return get(s.col(), s.row());
    }

    /**
     * Return the contents of the square at (COL, ROW), where
     * 0 <= COL, ROW <= 9.
     */
    final Piece get(int col, int row) {
        return _board[col][row];
    }

    /**
     * Return the contents of the square at COL ROW.
     */
    final Piece get(char col, char row) {
        return get(col - 'a', row - '1');
    }

    /**
     * Set square S to P.
     */
    final void put(Piece p, Square s) {
        if (p.equals(EMPTY)) {
            _findSquares.get(_board[s.col()][s.row()]).remove(s);
        } else {
            _findSquares.get(p).add(s);
        }
        _board[s.col()][s.row()] = p;
    }

    /**
     * Set square S to P and record for undoing.
     */
    final void revPut(Piece p, Square s) {
        put(p, s);
    }

    /**
     * Set square COL ROW to P.
     */
    final void put(Piece p, char col, char row) {
        put(p, sq(col - 'a', row - '1'));
    }

    /**
     * Return true iff FROM - TO is an unblocked rook move on the current
     * board.  For this to be true, FROM-TO must be a rook move and the
     * squares along it, other than FROM, must be empty.
     */
    boolean isUnblockedMove(Square from, Square to) {
        if (from.col() == to.col()) {
            if (from.row() < to.row()) {
                for (int i = from.row() + 1; i <= to.row(); i++) {
                    if (!get(from.col(), i).equals(EMPTY)) {
                        return false;
                    }
                }
            }
            if (from.row() > to.row()) {
                for (int i = from.row() - 1; i >= to.row(); i--) {
                    if (!get(from.col(), i).equals(EMPTY)) {
                        return false;
                    }
                }
            }
        }
        if (from.row() == to.row()) {
            if (from.col() < to.col()) {
                for (int i = from.col() + 1; i <= to.col(); i++) {
                    if (!get(i, from.row()).equals(EMPTY)) {
                        return false;
                    }
                }
            }
            if (from.col() > to.col()) {
                for (int i = from.col() - 1; i >= to.col(); i--) {
                    if (!get(i, from.row()).equals(EMPTY)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * Return true iff FROM is a valid starting square for a move.
     */
    boolean isLegal(Square from) {
        return get(from).side() == _turn;
    }

    /**
     * Return true iff FROM-TO is a valid move.
     */
    boolean isLegal(Square from, Square to) {
        boolean flag = true;
        if (get(from).equals(KING)
                && _turn.equals(BLACK)) {
            return false;
        }
        if (from.equals(to)) {
            flag = false;
        }
        if (from.row() != to.row()
                && from.col() != to.col()) {
            return false;
        }
        if (!isUnblockedMove(from, to)) {
            flag = false;
        }
        if (!get(from).equals(KING) && to.equals(THRONE)) {
            flag = false;
        }
        return flag && isLegal(from);
    }

    /**
     * Return true iff MOVE is a legal move in the current
     * position.
     */
    boolean isLegal(Move move) {
        return isLegal(move.from(), move.to());
    }

    /**
     * Move FROM-TO, assuming this is a legal move.
     */
    void makeMove(Square from, Square to) {
        assert isLegal(from, to);
        _moveCount += 1;
        if (2 * _limitmove == _moveCount) {
            _winner = _turn;
        }
        Piece temp = get(from);
        Board screenshot = new Board();
        screenshot.copy(this);
        _record.add(screenshot);

        _board[from.col()][from.row()] = EMPTY;
        _findSquares.get(temp).remove(from);

        _board[to.col()][to.row()] = temp;
        _findSquares.get(temp).add(to);

        checkCapture(to);
        if (kingPosition() == null) {
            _winner = BLACK;
        } else if (kingPosition().isEdge()) {
            _winner = WHITE;
        }
        for (String tempstring : _memory) {
            if (tempstring.equals(this.toString())) {
                _repeated = true;
                _winner = _turn.opponent();
            }
        }
        _memory.add(this.toString());
        _turn = _turn.side().opponent();
    }

    /** Helper function to check capture square.
     * @param sq Square needed to deter whether it is captured. */
    private void checkCapture(Square sq) {
        for (int i = 0; i < 4; i++) {
            int[] temp = DIR[i];
            try {
                capture(sq, Square.sq(sq.col() + 2 * temp[0],
                        sq.row() + 2 * temp[1]));
            } catch (IllegalArgumentException ignore) {
                int k = 0;
            }
        }
    }


    /**
     * Move according to MOVE, assuming it is a legal move.
     */
    void makeMove(Move move) {
        makeMove(move.from(), move.to());
    }

    /** Return hostile square for Black.
     * @param p Piece take in to collect hostile square.
     * @return return the hashset collecting hostile squares. */
    HashSet<Square> hostile(Piece p) {
        HashSet<Square> temp = new HashSet<>(pieceLocations(p));
        if (get(THRONE).equals(EMPTY)) {
            temp.add(THRONE);
        }
        if (p.equals(WHITE)) {
            temp.add(kingPosition());
            temp.add(THRONE);
        }
        if (p.equals(KING)) {
            temp.addAll(pieceLocations(WHITE));
        }
        return temp;
    }

    /**
     * Capture the piece between SQ0 and SQ2, assuming a piece just moved to
     * SQ0 and the necessary conditions are satisfied.
     */

    private void capture(Square sq0, Square sq2) {
        HashSet<Square> hostilesquare = hostile(_turn);
        boolean vertical = (Math.abs(sq0.row() - sq2.row()) == 2
                && sq0.col() == sq2.col());
        boolean horizontal = (Math.abs(sq0.col() - sq2.col()) == 2
                && sq0.row() == sq2.row());
        if (vertical || horizontal) {
            Piece mid = get(sq0.between(sq2));
            Square midsquare = sq0.between(sq2);
            if (mid.equals(_turn.opponent())) {
                if (hostilesquare.contains(sq2)) {
                    put(EMPTY, sq0.between(sq2));
                }
            }
            if (mid.equals(KING) && _turn.equals(BLACK)) {
                if (hostilesquare.contains(sq2)) {
                    if (midsquare.equals(THRONE)
                            || midsquare.equals(ETHRONE)
                            || midsquare.equals(NTHRONE)
                            || midsquare.equals(STHRONE)
                            || midsquare.equals(WTHRONE)) {
                        if (vertical) {
                            if (hostilesquare.contains(
                                    sq(midsquare.col() + 1,
                                    midsquare.row()))
                                    && hostilesquare.contains(
                                            sq(midsquare.col() - 1,
                                    midsquare.row()))) {
                                put(EMPTY, midsquare);
                                _winner = BLACK;
                            }
                        }
                        if (horizontal) {
                            if (hostilesquare.contains(sq(midsquare.col(),
                                    midsquare.row() + 1))
                                    && hostilesquare.contains
                                    (sq(midsquare.col(),
                                            midsquare.row() - 1))) {
                                put(EMPTY, midsquare);
                                _winner = BLACK;
                            }
                        }
                    } else {
                        put(EMPTY, midsquare);
                        _winner = BLACK;
                    }
                }
            }
        }
    }

    /**
     * Undo one move.  Has no effect on the initial board.
     */
    void undo() {
        if (_moveCount > 0) {
            Board temp = _record.get(_record.size() - 1);
            this.copy(temp);
            undoPosition();
        }
    }

    /**
     * Remove record of current position in the set of positions encountered,
     * unless it is a repeated position or we are at the first move.
     */
    private void undoPosition() {
        if (_record.size() != 0 || !_repeated) {
            _memory.remove(_memory.size() - 1);
            _record.remove(_record.size() - 1);
        }
    }

    /**
     * Clear the undo stack and board-position counts. Does not modify the
     * current position or win status.
     */
    void clearUndo() {
        _record.clear();
        _moveCount = 0;
    }

    /**
     * Return a new mutable list of all legal moves on the current board for
     * SIDE (ignoring whose turn it is at the moment).
     */
    List<Move> legalMoves(Piece side) {
        ArrayList<Move> moving = new ArrayList<>();
        HashSet<Square> allsquare = pieceLocations(side);
        for (Square a : allsquare) {
            findMoveHelper(moving, a);
        }
        if (side.equals(WHITE)) {
            for (Square a : pieceLocations(KING)) {
                findMoveHelper(moving, a);
            }
        }
        return moving;
    }

    /** Function to find move.
     * @param a Square input.
     * @param moving ArrayList record the move.*/
    private void findMoveHelper(ArrayList<Move> moving, Square a) {
        for (int i = 0; i < 4; i++) {
            int[] temp = DIR[i];
            int x = a.col() + temp[0];
            int y = a.row() + temp[1];
            while (Square.exists(x, y)) {
                Square q = sq(x, y);
                if (isLegal(a, q)) {
                    moving.add(mv(a, q));
                }
                x += temp[0];
                y += temp[1];
            }
        }
    }

    /**
     * Return true iff SIDE has a legal move.
     */
    boolean hasMove(Piece side) {
        List att = legalMoves(side);
        if (att.size() == 0) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return toString(true);
    }

    /**
     * Return a text representation of this Board.  If COORDINATES, then row
     * and column designations are included along the left and bottom sides.
     */
    String toString(boolean coordinates) {
        Formatter out = new Formatter();
        for (int r = SIZE - 1; r >= 0; r -= 1) {
            if (coordinates) {
                out.format("%2d", r + 1);
            } else {
                out.format("  ");
            }
            for (int c = 0; c < SIZE; c += 1) {
                out.format(" %s", get(c, r));
            }
            out.format("%n");
        }
        if (coordinates) {
            out.format("  ");
            for (char c = 'a'; c <= 'i'; c += 1) {
                out.format(" %c", c);
            }
            out.format("%n");
        }
        return out.toString();
    }

    /**
     * Return the locations of all pieces on SIDE.
     */
    private HashSet<Square> pieceLocations(Piece side) {
        assert side != EMPTY;
        return _findSquares.get(side);
    }

    /**
     * Return all the square for testing.
     */
    public List<Square> allSquares() {
        List<Square> squares = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                squares.add(Square.sq(i, j));
            }
        }
        return squares;
    }

    /**
     * Return the contents of _board in the order of SQUARE_LIST as a sequence
     * of characters: the toString values of the current turn and Pieces.
     */
    String encodedBoard() {
        char[] result = new char[Square.SQUARE_LIST.size() + 1];
        result[0] = turn().toString().charAt(0);
        for (Square sq : SQUARE_LIST) {
            result[sq.index() + 1] = get(sq).toString().charAt(0);
        }
        return new String(result);
    }

    /** Final direction group. */
    private static final int[][] DIR = {
            {0, 1}, {1, 0}, {0, -1}, {-1, 0}
    };

    /**
     * Piece whose turn it is (WHITE or BLACK).
     */
    private Piece _turn;
    /**
     * Cached value of winner on this board, or null if it has not been
     * computed.
     */
    private Piece _winner;
    /**
     * Number of (still undone) moves since initial position.
     */
    private int _moveCount;
    /**
     * True when current board is a repeated position (ending the game).
     */
    private boolean _repeated;
    /**
     * The 2D Array used to construct the board.
     */
    private Piece[][] _board;
    /**
     * HashMap used to construct relationship between square and piece.
     */
    private HashMap<Piece, HashSet<Square>> _findSquares;
    /**
     * Stack used to store square for latest move.
     */
    private ArrayList<Board> _record;
    /**
     * Temporary move limits of each game.
     */
    private int _limitmove;

    /** List used for repeated check. */
    private List<String> _memory;
}
