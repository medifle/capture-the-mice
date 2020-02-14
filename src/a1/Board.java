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

  public boolean isEmptyAt(int row, int col) {
    if (board[row][col] == 0) {
      return true;
    }
    return false;
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

  public State initState() {
    clear();
    State initialState = new State(rows, 1, 1, 3);
    loadState(initialState);
    return initialState;
  }

  public void loadState(State state) {
    Set<Position> mice = state.getMice();
    Set<Position> cats = state.getCats();
    Set<Position> cheeses = state.getCheeses();
    for (Position p : mice) {
      set(p.getY(), p.getX(), MOUSE);
    }
    for (Position p : cats) {
      set(p.getY(), p.getX(), CAT);
    }
    for (Position p : cheeses) {
      set(p.getY(), p.getX(), CHEESE);
    }
  }

  public void loadCatsState(State state) {
    Set<Position> cats = state.getCats();
    for (Position p : cats) {
      set(p.getY(), p.getX(), CAT);
    }
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
      sb.append("#\n");
    }
    sb.append("***".repeat(cols)).append("**\n");
    return sb.toString();
  }


  public static void main(String[] args) {
//    for (CellType c : CellType.values()) {
//      System.out.println(c);
//    }

//    Board board = new Board(12);
//    board.initState();
//    System.out.println(board.toString());
  }
}
