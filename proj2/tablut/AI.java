package tablut;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


import static tablut.Square.corners;
import static tablut.Square.sq;
import static tablut.Piece.*;

/** A Player that automatically generates moves.
 *  @author Simon Zhan
 */
class AI extends Player {

    /** A position-score magnitude indicating a win (for white if positive,
     *  black if negative). */
    private static final int WINNING_VALUE = Integer.MAX_VALUE - 20;
    /** A position-score magnitude indicating a forced win in a subsequent
     *  move.  This differs from WINNING_VALUE to avoid putting off wins. */
    private static final int WILL_WIN_VALUE = Integer.MAX_VALUE - 40;
    /** A magnitude greater than a normal value. */
    private static final int INFTY = Integer.MAX_VALUE;
    /** Infinite small value for prunning uses. */
    private static final int INFTYSMALL = Integer.MIN_VALUE;
    /** CS61B Magic number. */
    private static final int MAGIC = 25;
    /** CS61B Magic number. */
    private static final int TEST = 30;

    /** A new AI with no piece or controller (intended to produce
     *  a template). */
    AI() {
        this(null, null);
    }

    /** A new AI playing PIECE under control of CONTROLLER. */
    AI(Piece piece, Controller controller) {
        super(piece, controller);
    }

    @Override
    Player create(Piece piece, Controller controller) {
        return new AI(piece, controller);
    }

    @Override
    String myMove() {
        Move temp = findMove();
        _controller.reportMove(temp);
        return temp.toString();
    }

    @Override
    boolean isManual() {
        return false;
    }

    /** Return a move for me from the current position, assuming there
     *  is a move. */
    private Move findMove() {
        Board b = new Board(board());
        _lastFoundMove = null;
        Board temp = new Board();
        temp.copy(b);
        int sense = 1;
        if (temp.turn().equals(BLACK)) {
            sense = -1;
        }
        findMove(b, maxDepth(b), true, sense, -INFTY, INFTY);
        return _lastFoundMove;
    }

    /** The move found by the last call to one of the ...FindMove methods
     *  below. */
    private Move _lastFoundMove;

    /** Find a move from position BOARD and return its value, recording
     *  the move found in _lastFoundMove iff SAVEMOVE. The move
     *  should have maximal value or have value > BETA if SENSE==1,
     *  and minimal value or value < ALPHA if SENSE==-1. Searches up to
     *  DEPTH levels.  Searching at level 0 simply returns a static estimate
     *  of the board value and does not set _lastMoveFound. */
    private int findMove(Board board, int depth, boolean saveMove,
                         int sense, int alpha, int beta) {
        if (depth == 0 || board.winner() != null) {
            return staticScore(board);
        }
        int temp;
        if (sense == -1) {
            temp = INFTY;
        } else {
            temp = INFTYSMALL;
        }
        List<Move> allmove  = board.legalMoves(board.turn());
        Iterator<Move> itr = allmove.iterator();
        Move bestmove = null;
        Move nextmove;
        while (itr.hasNext()) {
            nextmove = itr.next();
            board.makeMove(nextmove);
            int score = findMove(board,
                    maxDepth(board) - 1, false, -sense, alpha, beta);
            board.undo();
            if (sense == -1) {
                if (score < temp) {
                    bestmove = nextmove;
                    temp = score;
                    beta = Math.min(beta, temp);
                    if (beta <= alpha) {
                        break;
                    }
                }
            } else {
                if (score > temp) {
                    bestmove = nextmove;
                    temp = score;
                    alpha = Math.max(alpha, temp);
                    if (beta <= alpha) {
                        break;
                    }
                    if (temp == WINNING_VALUE) {
                        temp = WILL_WIN_VALUE;
                    }
                }
            }
        }
        if (sense == 1) {
            if (temp == -WINNING_VALUE) {
                temp = -WILL_WIN_VALUE;
            }
        }
        if (saveMove) {
            _lastFoundMove = bestmove;
        }
        return temp;
    }

    /** Return a heuristically determined maximum search depth
     *  based on characteristics of BOARD. */
    private static int maxDepth(Board board) {
        if (board.moveCount() <= 5) {
            return 2;
        } else {
            return 3;
        }
    }

    /** Return a heuristic value for BOARD. */
    private int staticScore(Board board) {
        int temp = 0;
        if (board.winner().equals(WHITE)) {
            return WINNING_VALUE;
        }
        if (board.winner().equals(BLACK)) {
            return -WINNING_VALUE;
        }
        int numOppPiece = board.findingSquaresofPiece
                (myPiece().opponent()).size();
        int numMyPiece = board.findingSquaresofPiece(myPiece()).size();
        int differencenumber = numMyPiece - numOppPiece;
        int moveOfOpp = board.legalMoves(board.turn().opponent()).size();
        int moveOfMy = board.legalMoves(board.turn()).size();
        int differentcemove = moveOfMy - moveOfOpp;
        int edgescore = edgenumber(board);
        int directmove = moveneeded(board);
        int around = piecearound(board);
        int toking = distancetoking(board);
        temp = differencenumber + differentcemove
                + directmove - edgescore + toking - around;
        if (board.turn().equals(BLACK)) {
            temp = -temp;
        }
        return temp;
    }

    /** Function to return the neighbor of Square.
     * @param sq Square needed to get neighbors.
     * @return List returning */
    private List<Square> getsurrounding(Square sq) {
        List<Square> temp = new ArrayList<Square>();
        if (sq.rookMove(0, 1) != null) {
            temp.add(sq.rookMove(0, 1));
        }
        if (sq.rookMove(1, 1) != null) {
            temp.add(sq.rookMove(1, 1));
        }
        if (sq.rookMove(2, 1) != null) {
            temp.add(sq.rookMove(2, 1));
        }
        if (sq.rookMove(3, 1) != null) {
            temp.add(sq.rookMove(3, 1));
        }
        return temp;
    }

    /** Function used to find number of black pieces near the king.
     * @param board  Board passed in to check.
     * @return integer return for number of black pieces closed.*/
    int killKing(Board board) {
        int numPieces = 0;
        List<Square> kingNeighbors = getsurrounding(board.kingPosition());
        for (Square sq : kingNeighbors) {
            if (board.get(sq) == Piece.BLACK) {
                numPieces += 1;
            }
        }
        return numPieces * 100;
    }

    /** Function to calculate number of black pieces at the corner of the board.
     * @param board Board passed in to calculate.
     * @return number of black pieces. */
    int numCornerPieces(Board board) {
        int value = 0;
        if (board.get(0, 8).equals(BLACK)) {
            value += 1;
        }
        if (board.get(8, 8).equals(BLACK)) {
            value += 1;
        }
        if (board.get(0, 0).equals(BLACK)) {
            value += 1;
        }
        if (board.get(8, 0).equals(BLACK)) {
            value += 1;
        }
        return value;
    }

    /** Function to return number of moves king needed to reach the corner.
     * @param board Board passed in to calculate.
     * @return  integer returned.*/
    double kingmoveneed(Board board) {
        Square kingPosition = board.kingPosition();
        List<Move> kingmovement = getkingmove(board);
        double moveDistanceValue = 0.0;
        if (!kingmovement.isEmpty()) {
            int [] distances = new int [4];
            int index = 0;
            for (Square corner : Square.corners()) {
                distances[index] = minmovetocorner(board, corner,
                        1, kingPosition);
                index++;
            }
            for (int i = 0; i < distances.length; i++) {
                switch (distances[i]) {
                case 1:  moveDistanceValue += 15;
                    break;
                case 2:  moveDistanceValue += 1;
                    break;
                default: moveDistanceValue += 0;
                    break;
                }
            }
        }
        return moveDistanceValue;
    }
    /** CS61B Magic number.*/
    private static int mAX = 5 * 10;

    /** Helper function needed.
     * @param boardState Board passed in to calculate.
     * @param corner Square passed in to calculate.
     * @param kingPosition Square of King position.
     * @param moveCt Number of moves needed.
     * @return Integer return for further calculation. */
    public static int minmovetocorner(Board boardState, Square corner,
                                      int moveCt, Square kingPosition) {
        boolean temp = false;
        int min = mAX;
        for (Square sq : corners()) {
            if (kingPosition.equals(sq)) {
                temp = true;
            }
        }
        if (moveCt == 3 || temp) {
            return moveCt;
        }
        List<Move> kingMoves = getkingmove(boardState);
        int [] moveCounts = new int[kingMoves.size()];
        int index = 0;
        for (Move move : kingMoves) {
            if (move.from().distancebetween(corner)
                    > move.to().distancebetween(corner)) {
                moveCounts[index] = minmovetocorner(boardState,
                        corner, moveCt + 1, move.to());
                index++;
            }
        }

        for (int i = 0; i < moveCounts.length; i++) {
            int current = moveCounts[i];
            if (current != 0 && current < min) {
                min = current;
            }
        }
        return min;
    }

    /** Function returining all legal moves of King.
     * @param board Pass in board to retrieve value.
     * @return returning the list of all legalmoves. */
    static List<Move> getkingmove(Board board) {
        List<Move> legalMoves = board.legalMoves(KING);
        return legalMoves;
    }

    /** Return the calculation of the move KING need to reach the side.
     * @param board The board needed for calculation.
     * @return moves needed to reach the side. */
    private int moveneeded(Board board) {
        int t = 0;
        Square king = board.kingPosition();
        if (board.isLegal(king, sq(0, king.row()))) {
            t += TEST;
        }
        if (board.isLegal(king, sq(8, king.row()))) {
            t += TEST;
        }
        if (board.isLegal(king, sq(king.col(), 0))) {
            t += TEST;
        }
        if (board.isLegal(king, sq(king.col(), 8))) {
            t += TEST;
        }
        if (t >= TEST * 2) {
            t += WILL_WIN_VALUE;
        }
        return t;
    }

    /** Calculate the adjacent black pieces in the board.
     * @param board Board passed in to calculate the value.
     * @return Integer return to test. */
    private int piecearound(Board board) {
        int temp = 0;
        List<Square> kingNeighbors = getsurrounding(board.kingPosition());
        int col = board.kingPosition().col();
        int row = board.kingPosition().row();
        if (col >= 2 &&  col <= 6 && row >= 2 && row <= 6) {
            for (Square sq : kingNeighbors) {
                if (board.get(sq).equals(BLACK)) {
                    temp += MAGIC;
                }
            }
            Square temp1 = sq(col + 2, row);
            if (board.get(temp1) == BLACK) {
                temp += MAGIC / 5;
            }
            Square temp2 = sq(col - 2, row);
            if (board.get(temp2) == BLACK) {
                temp += MAGIC / 5;
            }
            Square temp3 = sq(col, row + 2);
            if (board.get(temp3) == BLACK) {
                temp += MAGIC / 5;
            }
            Square temp4 = sq(col, row - 2);
            if (board.get(temp4) == BLACK) {
                temp += MAGIC / 5;
            }
            if (board.get(col + 1, row + 1) == BLACK) {
                temp += 2;
            }
            if (board.get(col + 1, row - 1) == BLACK) {
                temp += 2;
            }
            if (board.get(col - 1, row + 1) == BLACK) {
                temp += 2;
            }
            if (board.get(col - 1, row - 1) == BLACK) {
                temp += 2;
            }
        }
        return temp;
    }

    /** Function to check legal move of each Piece and return score.
     * @param board  Board passed in to run test.
     * @return score return to calculate. */
    private int checklegalmove(Board board) {
        int temp = 0;
        int t1 = board.legalMoves(myPiece()).size();
        int t2 = board.legalMoves(myPiece().opponent()).size();
        return temp + t1 - t2;
    }

    /** Function to calculate the distance
     * between each black Piece to the King.
     * @param board Board passed in to see check the total distance.
     * @return Integer return to check move. */
    private int distancetoking(Board board) {
        int temp = 0;
        Square kingpos = board.kingPosition();
        for (Square sq : board.findingSquaresofPiece(BLACK)) {
            temp += Math.abs(sq.col() - kingpos.col())
                    + Math.abs(sq.row() - kingpos.row());
        }
        return temp;
    }

    /** Checking how many edge of board black has taken.
     * @param board  Board passed in to calculate.
     * @return number returned for calculation. */
    private int edgenumber(Board board) {
        int temp = 0;
        for (Square sq : corners()) {
            if (board.get(sq).equals(BLACK)) {
                temp += MAGIC;
            }
        }
        for (int i = 0; i < board.SIZE; i++) {
            for (int j = 0; j < board.SIZE; j++) {
                Square square = sq(i, j);
                if (square.isEdge() && board.get(square).equals(BLACK)) {
                    temp += MAGIC;
                }
            }
        }
        return temp;
    }



}
