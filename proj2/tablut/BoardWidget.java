package tablut;

import ucb.gui2.Pad;

import java.util.concurrent.ArrayBlockingQueue;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Font;
import java.awt.event.MouseEvent;

import static tablut.Piece.*;
import static tablut.Square.sq;
import static tablut.Move.mv;

/**
 * A widget that displays a Tablut game.
 *
 * @author Simon Zhan
 */
class BoardWidget extends Pad {

    /* Parameters controlling sizes, speeds, colors, and fonts. */

    /**
     * Squares on each side of the board.
     */
    static final int SIZE = Board.SIZE;

    /**
     * Colors of empty squares, pieces, grid lines, and boundaries.
     */
    static final Color
            SQUARE_COLOR = new Color(238, 207, 161),
            THRONE_COLOR = new Color(180, 255, 180),
            ADJACENT_THRONE_COLOR = new Color(200, 220, 200),
            CLICKED_SQUARE_COLOR = new Color(255, 255, 100),
            GRID_LINE_COLOR = Color.black,
            WHITE_COLOR = Color.white,
            BLACK_COLOR = Color.black;

    /**
     * Margins.
     */
    static final int
            OFFSET = 2,
            MARGIN = 16;

    /**
     * Side of single square and of board (in pixels).
     */
    static final int
            SQUARE_SIDE = 30,
            BOARD_SIDE = SQUARE_SIDE * SIZE + 2 * OFFSET + MARGIN;

    /**
     * The font in which to render the "K" in the king.
     */
    static final Font KING_FONT = new Font("Serif", Font.BOLD, 18);
    /**
     * The font for labeling rows and columns.
     */
    static final Font ROW_COL_FONT = new Font("SanSerif", Font.PLAIN, 10);

    /**
     * Squares adjacent to the throne.
     */
    static final Square[] ADJACENT_THRONE = {
        Board.NTHRONE, Board.ETHRONE, Board.STHRONE, Board.WTHRONE
    };


    /**
     * A graphical representation of a Tablut board that sends commands
     * derived from mouse clicks to COMMANDS.
     */
    BoardWidget(ArrayBlockingQueue<String> commands) {
        _commands = commands;
        setMouseHandler("click", this::mouseClicked);
        setPreferredSize(BOARD_SIDE, BOARD_SIDE);
        _acceptingMoves = false;
    }

    /**
     * Draw the bare board G.
     */
    private void drawGrid(Graphics2D g) {
        g.setColor(SQUARE_COLOR);
        g.fillRect(0, 0, BOARD_SIDE, BOARD_SIDE);
        g.setColor(THRONE_COLOR);
        g.fillRect(cx(Board.THRONE), cy(Board.THRONE),
                SQUARE_SIDE, SQUARE_SIDE);
        g.setColor(ADJACENT_THRONE_COLOR);
        for (Square a : ADJACENT_THRONE) {
            g.fillRect(cx(a), cy(a), SQUARE_SIDE, SQUARE_SIDE);
        }
        if (_clicksquare != null) {
            g.setColor(CLICKED_SQUARE_COLOR);
            g.fillRect(cx(_clicksquare),
                    cy(_clicksquare), SQUARE_SIDE, SQUARE_SIDE);
        }
        g.setColor(GRID_LINE_COLOR);
        g.setFont(ROW_COL_FONT);
        int[] number = new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9};
        String alpha = "abcdefghi";
        for (int i = 0; i < number.length; i++) {
            g.drawString(String.valueOf(number[i]),
                    valuex(0, 10), valuey(i, 15));
            g.drawString(Character.toString(alpha.charAt(i)),
                    valuex(i, 10), valuey(0, 5 * 9));
        }
        for (int k = 0; k <= SIZE; k += 1) {
            g.drawLine(cx(0), cy(k - 1), cx(SIZE), cy(k - 1));
            g.drawLine(cx(k), cy(-1), cx(k), cy(SIZE - 1));
        }
    }

    /** Function to get rid of stupid magic number.
     * @param a number need to be calculate.
     * @param b  number need to be calculate.
     * @return number return */
    private int valuex(int a, int b) {
        return cx(a) + b;
    }

    /** Function to get rid of stupid magic number.
     * @param a square need to be calculate.
     * @param b  number need to be calculate.
     * @return return number. */
    private int valuex(Square a, int b) {
        return cx(a) + b;
    }

    /** Function to get rid of magic number.
     * @param b number to calculate.
     * @param a number to calculate.
     * @return return number. */
    private int valuey(int a, int b) {
        return cy(a) + b;
    }

    /** Function to get rid of magic number.
     * @param b Square to calculate.
     * @param a number to calculate.
     * @return return number. */
    private int valuey(Square a, int b) {
        return cy(a) + b;
    }

    @Override
    public synchronized void paintComponent(Graphics2D g) {
        drawGrid(g);
        Square.SQUARE_LIST.iterator().forEachRemaining(s -> drawPiece(g, s));
    }

    /** Magic number. */
    private static int wID = 11 + 12;

    /** Magic number. */
    private static int mU = 11 + 10;

    /**
     * Draw the contents of S on G.
     */
    private void drawPiece(Graphics2D g, Square s) {
        int x = valuex(s, 3);
        int y = valuey(s, 3);
        int x1 = valuex(s, 8);
        int y1 = valuey(s, mU);
        int width = wID;
        if (_board.get(s).equals(BLACK)) {
            g.setColor(BLACK_COLOR);
            g.fillOval(x, y, width, width);
        }

        if (_board.get(s).equals(WHITE)) {
            g.setColor(WHITE_COLOR);
            g.fillOval(x, y, width, width);
        }

        if (_board.get(s).equals(KING)) {
            g.setColor(WHITE_COLOR);
            g.setFont(KING_FONT);
            g.fillOval(x, y, width, width);
            g.setColor(Color.BLUE);
            g.drawString("K", x1, y1);
        }
    }

    /**
     * Handle a click on S.
     */
    private void click(Square s) {
        if (_clicksquare != null) {
            if (_board.isLegal(_clicksquare, s)) {
                _commands.offer(mv(_clicksquare, s).toString());
            }
            s.setSelected();
            _clicksquare = null;
            repaint();
        }
        if (!_board.get(s).equals(EMPTY)) {
            s.setSelected();
            if (s.selectreturn()) {
                _clicksquare = s;
            } else {
                _clicksquare = null;
            }
        }
        repaint();
    }

    /**
     * Handle mouse click event E.
     */
    private synchronized void mouseClicked(String unused, MouseEvent e) {
        int xpos = e.getX(), ypos = e.getY();
        int x = (xpos - OFFSET - MARGIN) / SQUARE_SIDE,
                y = (OFFSET - ypos) / SQUARE_SIDE + SIZE - 1;
        if (_acceptingMoves
                && x >= 0 && x < SIZE && y >= 0 && y < SIZE) {
            click(sq(x, y));
        }
    }

    /**
     * Revise the displayed board according to BOARD.
     */
    synchronized void update(Board board) {
        _board.copy(board);
        repaint();
    }

    /**
     * Turn on move collection iff COLLECTING, and clear any current
     * partial selection.  When move collection is off, ignore clicks on
     * the board.
     */
    void setMoveCollection(boolean collecting) {
        _acceptingMoves = collecting;
        repaint();
    }

    /**
     * Return x-pixel coordinate of the left corners of column X
     * relative to the upper-left corner of the board.
     */
    private int cx(int x) {
        return x * SQUARE_SIDE + OFFSET + MARGIN;
    }

    /**
     * Return y-pixel coordinate of the upper corners of row Y
     * relative to the upper-left corner of the board.
     */
    private int cy(int y) {
        return (SIZE - y - 1) * SQUARE_SIDE + OFFSET;
    }

    /**
     * Return x-pixel coordinate of the left corner of S
     * relative to the upper-left corner of the board.
     */
    private int cx(Square s) {
        return cx(s.col());
    }

    /**
     * Return y-pixel coordinate of the upper corner of S
     * relative to the upper-left corner of the board.
     */
    private int cy(Square s) {
        return cy(s.row());
    }

    /**
     * Queue on which to post move commands (from mouse clicks).
     */
    private ArrayBlockingQueue<String> _commands;

    /**
     * Board being displayed.
     */
    private final Board _board = new Board();

    /**
     * True iff accepting moves from user.
     */
    private boolean _acceptingMoves;

    /**
     * Store the clicked square from user.
     */
    private Square _clicksquare = null;

}
