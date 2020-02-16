package a1;

import java.util.*;

public class State {
  private List<Position> mice = new ArrayList<>();
  private List<Position> cats = new ArrayList<>();
  private Set<Position> cheeses = new HashSet<>();

  public List<Position> getMice() {
    return mice;
  }

  public List<Position> getCats() {
    return cats;
  } // support for at most 2 cats

  public Set<Position> getCheeses() {
    return cheeses;
  }

  public void removeMouse(Position p) {
    mice.remove(p);
  }

  public void removeCheese(Position p) {
    cheeses.remove(p);
  }

  public boolean isCatEnd() {
    return mice.isEmpty();
  }

  public boolean isMouseEnd() {
    return !mice.isEmpty() && cheeses.isEmpty();
  }

  public State(List<Position> mice, List<Position> cats, Set<Position> cheeses) {
    this.mice = mice;
    this.cats = cats;
    this.cheeses = cheeses;
    sanitize();
  }

  public void sanitize() {
    // if mouse and cheese overlap
    for (Position mp : mice) {
      if (cheeses.contains(mp)) {
        Log.d("STATE", "sanitize Mouse&Cheese: " + mp);
        removeCheese(mp);
      }
    }

    // if cat and mouse overlap
    for (Position cp : cats) {
      if (mice.contains(cp)) {
        Log.d("STATE", "sanitize Cat&Mouse: " + cp);
        removeMouse(cp);
      }
    }
  }

  /**
   * Constructor: randomly generate state with m mice, c cats, e cheeses given a n size board
   *
   * @param n board size (square shape only for now)
   * @param m number of mice
   * @param c number of cats, at most 2
   * @param e number of cheeses
   */
  public State(int n, int m, int c, int e) {
    // use a temp board to check duplicate
    Board board = new Board(n);

    Random rand = new Random();
    uniqueGenerate(board, rand, mice, m);
    uniqueGenerate(board, rand, cats, c);
    uniqueGenerate(board, rand, cheeses, e);
  }

  public void uniqueGenerate(Board board, Random rand, Collection<Position> collection, int num) {
    int rows = board.getRows();
    int cols = board.getCols();

    int count = 0;
    while (count != num) {
      int x = rand.nextInt(cols);
      int y = rand.nextInt(rows);

      if (board.isEmptyAt(x, y)) {
        board.set(x, y, 1);
        collection.add(new Position(x, y));
        count += 1;
      }
    }
  }

  // e.g. 1,2;-4,3;-1,4;2,5;3,4;
  //       M    C        E
  public String toString() {
    StringBuilder sb = new StringBuilder();
    for (Position mouse : mice) {
      sb.append(mouse.toString()).append(";");
    }
    sb.append("-");
    for (Position cat : cats) {
      sb.append(cat.toString()).append(";");
    }
    sb.append("-");
    for (Position cheese : cheeses) {
      sb.append(cheese.toString()).append(";");
    }

    return sb.toString();
  }

  public static void main(String[] args) {

//    List<Position> mc = new ArrayList<>();
//    Position p1 = new Position(1, 1);
//    mc.add(p1);
//    mc.add(new Position(2, 0));
//
//    List<Position> mc2 = new ArrayList<>(mc);
//    System.out.println(mc2 == mc);
//    mc2.remove(p1);
//    System.out.println(mc);
//    System.out.println(mc2);
  }
}
