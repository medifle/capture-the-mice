package a1;

import javafx.geometry.Pos;

import java.util.*;

public class State {
  private Set<Position> mice = new HashSet<>();
  private Set<Position> cats = new HashSet<>();
  private Set<Position> cheeses = new HashSet<>();

  public Set<Position> getMice() {
    return mice;
  }
  public Set<Position> getCats() {
    return cats;
  }
  public Set<Position> getCheeses() {
    return cheeses;
  }

  public boolean isExist(Position p) {
    return mice.contains(p) || cats.contains(p) || cheeses.contains(p);
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

  /**
   * randomly generate state with m mice, c cats, e cheeses given a n size board
   * @param n board size (square shape only for now)
   * @param m number of mice
   * @param c number of cats
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

  public void uniqueGenerate(Board board, Random rand, Set<Position> hset, int num) {
    int rows = board.getRows();
    int cols = board.getCols();

    int count = 0;
    while (count != num) {
      int x = rand.nextInt(cols);
      int y = rand.nextInt(rows);

      if (board.isEmptyAt(x,y)) {
        board.set(x, y, 1);
        hset.add(new Position(x,y));
        count += 1;
      }
    }
  }

  public String toString() {
    // e.g. M1,2;C4,3;E1,4;2,5;3,4;
    StringBuilder sb = new StringBuilder();
    sb.append("M");
    for (Position mouse : mice) {
      sb.append(mouse.toString()).append(";");
    }
    sb.append("C");
    for (Position cat : cats) {
      sb.append(cat.toString()).append(";");
    }
    sb.append("E");
    for (Position cheese : cheeses) {
      sb.append(cheese.toString()).append(";");
    }

    return sb.toString();
  }
}
