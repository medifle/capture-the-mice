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
    while (gameRestart) {
//      init(12, 1, 1, 3);
//      init(8, 3, 4, 6);
//      init(20, 1, 1, 3);
//      init(50, 1, 1, 3);
//      init(16, 4, 2, 8);
//      init(30, 4, 2, 10); // IDDFS showtime
//      init(12, "7,1;-2,6;-9,1;9,6;6,10;"); // from a1 spec
//      init(12, "7,1;-2,6;-7,3;9,1;9,6;6,10;"); // cheese tie
//      init(12, "7,2;-2,6;-6,3;6,1;8,3;8,1;9,6;6,10;"); // cheese tie: bottom-right test
//      init(12, "6,9;-7,1;-5,8;10,2;0,10;"); // DFS fool
//      init(12, "11,0;-0,11;-11,1;10,3;"); // Mouse win
//      init(12, "1,5;0,9;1,11;-11,6;-6,5;10,9;0,8;10,5;9,6;");
//      init(12, "1,5;0,9;1,11;-11,6;11,3;-6,5;10,9;0,8;10,5;9,6;");
//      init(30, "12,24;-25,20;-23,11;12,16;9,0;");
//      init(12, "1,5;3,5;-11,6;11,6;-6,5;10,9;0,8;10,5;9,6;"); // BFS: big difference in searched nodes if cats sorted
//      init(12, "6,4;-11,5;-3,10;2,7;8,9;1,0;"); // DFS test
//      init(12, "6,3;-0,4;-0,10;0,0;2,0;3,9;"); // DFS test2
//      init(10, "2,4;-7,9;-6,0;7,1;8,8;"); // IDDFS test
//      init(10, "6,5;-7,1;-9,1;2,8;5,7;"); // IDDFS test2
//      init(10, "3,5;-5,8;-5,1;9,1;8,6;"); // IDDFS test3
//      init(10, "4,9;-9,2;-0,8;1,5;3,7;"); // IDDFS test4
//      init(10, "3,3;-4,1;-7,2;5,5;8,5;"); // IDDFS test5
//      init(10, "6,3;-4,1;-9,9;"); // IDDFS test6 no solution



      /* AStar optimal test */
//      init(8, 3, 5, 6);
//      init(8, "7,7;1,7;6,2;-0,2;0,7;3,6;2,4;5,6;-3,4;1,3;7,1;7,3;5,1;6,4;");
//      init(8, "2,7;1,3;1,5;-3,1;1,6;0,0;1,7;6,1;-0,2;1,4;0,4;0,7;6,2;7,4;"); // AStar showtime, g factor >15
//      init(16, 3, 3, 8);
//      init(16, "6,4;4,5;5,7;-3,6;4,7;2,1;5,1;-4,3;1,0;7,6;4,4;3,7;7,4;");



//      # not optimal (Solved) g factor 260
      init(30, "8,22;7,29;12,6;8,20;-6,12;26,25;-10,25;21,7;17,19;16,4;2,26;10,20;29,24;13,26;11,25;1,15;");

      // AStar test: not optimal (Solved)
//      init(16, "7,5;8,1;2,12;9,15;-5,1;4,15;-2,4;13,1;1,6;13,3;13,5;5,14;11,5;3,14;");

//      #1cat optimal
//      init(16, "10,5;-11,4;-2,4;13,1;1,6;13,3;13,5;5,14;11,5;3,14;");
//      # 2cat not optimal (Solved)
//      init(16, "10,5;-11,4;6,14;-2,4;13,1;1,6;13,3;13,5;5,14;11,5;3,14;"); // depth 0 PASS: give g more weight
//      init(16, "11,5;-10,6;7,12;-2,4;13,1;1,6;13,3;13,5;5,14;3,14;"); // depth 1: PASS
//      init(16, "-12,5;8,10;-2,4;13,1;1,6;13,3;13,5;5,14;3,14;"); // depth 2(solution): PASS

      // BFS test: storing State is more memory efficient than storing String
//      init(20, "7,4;5,11;10,19;7,9;-8,11;19,13;-10,9;4,4;2,2;16,0;10,14;8,3;8,4;17,14;");

      if (actionSeq.size() == 0) {
        Search ai = new Search(state, board, 1);
//        actionSeq = ai.BFS();
        actionSeq = ai.AStar(HeuristicType.HYBRID.getType());
//        actionSeq = ai.DFS();
//        actionSeq = ai.DLS(3, true);
//        actionSeq = ai.IDDFS();

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
