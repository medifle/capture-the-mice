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
  }

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
        Log.s("STATE", "sanitize Mouse&Cheese: " + mp);
        removeCheese(mp);
      }
    }

    // if cat and mouse overlap
    for (Position cp : cats) {
      if (mice.contains(cp)) {
        Log.s("STATE", "sanitize Cat&Mouse: " + cp);
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

  /**
   * Auto generated equals
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    State state = (State) o;
    return mice.equals(state.mice) &&
      cats.equals(state.cats) &&
      cheeses.equals(state.cheeses);
  }

  /**
   * Auto generated hashCode
   * Make sure HashSet, HashMap works for State
   * @return
   */
  @Override
  public int hashCode() {
    return Objects.hash(mice, cats, cheeses);
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
    /* Test State equals and hashCode */
//    List<Position> mc = new ArrayList<>();
//    Position p1 = new Position(1, 1);
//    mc.add(p1);
//    mc.add(new Position(2, 0));
//
//    List<Position> ct = new ArrayList<>();
//    ct.add(new Position(3, 3));
//    ct.add(new Position(3, 3));
//    ct.add(new Position(5, 4));
//
//    Set<Position> es = new HashSet<>();
//    es.add(new Position(7, 1));
//    es.add(new Position(6, 3));
//
//    State state = new State(mc, ct, es);
//    State state2 = new State(mc, ct, es);
//    System.out.println(state.equals(state2));
//
//    Set<State> hset = new HashSet<>();
//    hset.add(state);
//    hset.add(state2);
//
//    State[] arr = hset.toArray(new State[0]);
//    for (State p : arr) {
//      System.out.println(p);
//    }


    /* Test List equals */
//    List<Position> mc2 = new ArrayList<>(mc);
//    System.out.println(mc2 == mc);
//    System.out.println(mc2.equals(mc));
//    mc2.remove(p1);
//    System.out.println("remove p1");
//    System.out.println(mc2.equals(mc));
//    System.out.println(mc);
//    System.out.println(mc2);
  }
}
