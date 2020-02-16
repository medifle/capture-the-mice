package a1;

import java.util.*;

public class Board {
  /** cell type id
   * 0: empty
   * 1: mouse
   * 2: cat
   * 3: cheese
   */
  private int[][] board;
  private int rows;
  private int cols;

  private int EMPTY = CellType.EMPTY.getType();
  private int MOUSE = CellType.MOUSE.getType();
  private int CAT = CellType.CAT.getType();
  private int CHEESE = CellType.CHEESE.getType();

  public Board(int n) {
    rows = n;
    cols = n;
    board = new int[n][n];
  }

  public Board(int rows, int cols) {
    this.rows = rows;
    this.cols = cols;
    board = new int[rows][cols];
  }

  public boolean isValidPos(Position p) {
    return p.getX() >= 0 && p.getX() < rows && p.getY() >= 0 && p.getY() < cols;
  }

  public boolean isEmptyAt(int row, int col) {
    return board[row][col] == EMPTY;
  }

  public int getRows() {
    return rows;
  }

  public int getCols() {
    return rows;
  }

  public void set(int row, int col, int value) {
    board[row][col] = value;
  }

  public void clear() {
    board = new int[rows][cols];
  }

  public State initRandomState(int m, int c, int e) {
    clear();

    State initialState = new State(rows, m, c, e);
    loadState(initialState);
    return initialState;
  }

  public void loadState(State state) {
    clear();

    List<Position> mice = state.getMice();
    List<Position> cats = state.getCats();
    Set<Position> cheeses = state.getCheeses();
    for (Position p : cheeses) {
      set(p.getY(), p.getX(), CHEESE);
    }
    for (Position p : mice) {
      set(p.getY(), p.getX(), MOUSE);
    }
    //if the cat and cheese are at the same position, show cat
    for (Position p : cats) {
      set(p.getY(), p.getX(), CAT);
    }
  }

  public State loadState(String s) {
    String[] sArr = s.split("-");
    String m = sArr[0];
    String c = sArr[1];
    String e = sArr[2];

    String[] mArr = m.split(";");
    String[] cArr = c.split(";");
    String[] eArr = e.split(";");

    List<Position> mice = new ArrayList<>();;
    List<Position> cats = new ArrayList<>();
    Set<Position> cheeses = new HashSet<>();
    for (String ms : mArr) {
      Position mp = new Position(ms);
      mice.add(mp);
    }

    for (String cs : cArr) {
      Position cp = new Position(cs);
      cats.add(cp);
    }

    for (String es : eArr) {
      Position ep = new Position(es);
      cheeses.add(ep);
    }

    State state = new State(mice, cats, cheeses);
    loadState(state);

    return state;
  }

  public String toString() {
    StringBuilder sb = new StringBuilder();
    String CHEESE_EMOJI = "\uD83E\uDDC0";
    String MOUSE_EMOJI = "\uD83D\uDC39";
//    String CAT_EMOJI = "\uD83D\uDE3A";
    String CAT_EMOJI = "\uD83D\uDC79";

    sb.append("***".repeat(cols)).append("**\n");
    for (int i = 0; i < rows; ++i) {
      sb.append("# ");
      for (int j = 0; j < cols; ++j) {
        if (board[i][j] == EMPTY) {
          sb.append("⬜ ");
        } else if (board[i][j] == MOUSE) {
          sb.append(MOUSE_EMOJI).append(" ");
        } else if (board[i][j] == CAT) {
          sb.append(CAT_EMOJI).append(" ");
        } else if (board[i][j] == CHEESE) {
          sb.append(CHEESE_EMOJI).append(" ");
        }
      }
      sb.append(i).append("\n");
    }
    sb.append("***".repeat(cols)).append("**\n");
    return sb.toString();
  }


  public static void main(String[] args) {

    Board board = new Board(12);
//    board.loadState("7,1;-2,6;-9,1;9,6;6,10;");
    board.loadState("7,1;-2,6;-6,10;9,1;9,6;");
    System.out.println(board.toString());

    //    Board board = new Board(12);
    //    board.initRandomState();
    //    System.out.println(board.toString());

  }
}
