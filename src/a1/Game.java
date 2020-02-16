package a1;

import java.util.*;

public class Game {

  private static Board board;
  private static State state;

  private static boolean gameOver = true;
  private static boolean gameRestart = true;

  // cats action states sequence
  private static Queue<State> actionSeq;

  public static void printBoard(Board board) {
    System.out.println(board.toString());
  }

  public static void printState(State state, int rows, int cols) {
    Board bd = new Board(rows, cols);
    bd.loadState(state);
    printBoard(bd);
  }

  /**
   * Randomly initialize a board
   *
   * @param n board dimension (square)
   */
  public static void init(int n, int m, int c, int e) {
    board = new Board(n);
    state = board.initRandomState(m, c, e);
    actionSeq = new LinkedList<>();
    gameOver = false;
  }

  /**
   * Load a board with a given state
   *
   * @param n board dimension (square)
   * @param s state string representation
   */
  public static void init(int n, String s) {
    board = new Board(n);
    state = board.loadState(s);
    actionSeq = new LinkedList<>();
    gameOver = false;
  }

  public static void main(String[] args) {
    /**
     * Only support at most 2 cats!
     */
    while (gameRestart) {
//      init(12, 1, 1, 3);
      init(16, 3, 2, 6);
//      init(30, 4, 2, 10); // IDDFS showtime
//      init(12, "7,1;-2,6;-9,1;9,6;6,10;");
//      init(12, "6,9;-7,1;-5,8;10,2;0,10;");
//      init(12, "11,0;-0,11;-11,1;10,3;"); // mouse win
//      init(12, "1,5;0,9;1,11;-11,6;-6,5;10,9;0,8;10,5;9,6;");
//      init(12, "1,5;0,9;1,11;-11,6;11,3;-6,5;10,9;0,8;10,5;9,6;");

      if (actionSeq.size() == 0) {
        Search ai = new Search(state, board);
//        actionSeq = ai.BFS();
//        actionSeq = ai.DFS();
//        actionSeq = ai.DLS(5, false);
        actionSeq = ai.IDDFS();
        if (actionSeq == null) {
          Log.i("GAME", "Cat AI failed!");
          Log.i("GAME", "MOUSE WIN!");
          break;
        }
      }

      System.out.println();// add one line space
      Log.i("GAME", "Start!");
      while (!gameOver) {
        state = actionSeq.poll();
        if (state != null) {
          board.loadState(state);
        } else {
          Log.e("GAME:ERROR", "action state is null");
          gameOver = true;
          gameRestart = false;
        }

        Log.i("GAME", "next actionState " + state.toString());
        printBoard(board);

        if (state.isMouseEnd()) {
          Log.i("GAME", "MOUSE WIN!");
          gameOver = true;
          gameRestart = true;
        } else if (state.isCatEnd()) {
          Log.i("GAME", "CAT WIN!");
          gameOver = true;
          gameRestart = false;
        }


        // 1s interval
        try {
          Thread.sleep(1000);
        } catch (InterruptedException ex) {
          ex.printStackTrace();
        }
      }
    }
  }
}
