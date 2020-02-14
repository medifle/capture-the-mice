package a1;

import java.util.*;

public class Game {

  private static Board board;
  private static State state;

  private static boolean gameOver = true;

  // cats action states sequence
  private static Queue<State> actionSeq = new LinkedList<>();

  public static void printBoard(Board board) {
    System.out.println(board.toString());
  }

  public static void updateState(State state) {
    //move cats

    //remove dead mouse
    //gameStatus check
    //mouse move
  }

  public static void init(int n) {
    board = new Board(n);
    state = board.initState();
    gameOver = false;
  }

  public static void main(String[] args) {
    //#GAME LOOP
    init(12);


    if (actionSeq.size() == 0) {
      //todo: run algo like BFS to fill actionSeq
    }

    while (!gameOver) {
      printBoard(board);

      state.getCheeses().clear();

      if (state.isCatEnd()) {
        System.out.println("GAME: CAT WIN!\n");
      } else if (state.isMouseEnd()) {
        System.out.println("GAME: MOUSE WIN!\n");
      }

      //1s interval
      try {
        Thread.sleep(1000);
      } catch(InterruptedException ex) {
        ex.printStackTrace();
      }

      //get next action state
//      State nextActionState = actionSeq.poll();
//      if (nextActionState != null) {
//        board.loadState(nextActionState);
//      }
    }






    // update state
      //cat,mouse move
      //mouse dead, cheese eaten
  }
}
