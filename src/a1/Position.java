package a1;

import java.util.*;

public class Position implements Comparable<Position> {
  private int x;
  private int y;

  public Position(int x, int y) {
    this.x = x;
    this.y = y;
  }

  // "1,2"
  public Position(String s) {
    String[] sArr = s.split(",");
    this.x = Integer.parseInt(sArr[0]);
    this.y = Integer.parseInt(sArr[1]);
  }

  public int getX() {
    return x;
  }

  public int getY() {
    return y;
  }

//  public void update(int x, int y) {
//    this.x = x;
//    this.y = y;
//  }

  /**
   * Auto generated equals
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Position position = (Position) o;
    return x == position.x &&
      y == position.y;
  }

  /**
   * Auto generated hashCode
   * Make sure HashSet, HashMap works for Position
   */
  @Override
  public int hashCode() {
    return Objects.hash(x, y);
  }

  @Override
  public int compareTo(Position p) {
    return (x + y) - (p.getX() + p.getY());
  }

  @Override
  public String toString() {
    return x + "," + y;
  }

  public static void main(String[] args) {
    Position aa = new Position(1, 1);
    Position dd = new Position(1, 1);
    Position bb = new Position(3, 4);
    Position cc = new Position(7, 4);
//    System.out.println(aa.equals(dd));
//
//    Set<Position> hset = new HashSet<>();
//    hset.add(aa);
//    hset.add(cc);
//    hset.add(dd);
//
//    Position[] arr = hset.toArray(new Position[0]);
//    Arrays.sort(arr, Collections.reverseOrder());
//    for (Position p : arr) {
//      System.out.println(p);
//    }


    // PriorityQueue element update pitfall
//    Queue<Position> open = new PriorityQueue<>();
//    open.add(aa);
//    open.add(cc);
//    open.add(dd);
//    open.add(bb);
//
//    aa.update(9,9);// queue order will not be updated!
//
//    open.remove(aa);
//    open.add(aa);
//
//    while (!open.isEmpty()) {
//      System.out.println(open.poll());
//    }
  }
}
