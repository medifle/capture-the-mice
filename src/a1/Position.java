package a1;

import java.util.*;

public class Position implements Comparable<Position>{
  private int x;
  private int y;

  public Position(int x, int y){
    this.x = x;
    this.y = y;
  }

  // "1,2"
  public Position(String s) {
    String[] sArr = s.split(",");
    this.x = Integer.parseInt(sArr[0]);
    this.y = Integer.parseInt(sArr[1]);
  }

  public int getX(){ return x; }
  public int getY(){ return y; }

//  public void update(int x, int y) {
//    this.x = x;
//    this.y = y;
//  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (!(o instanceof Position)) {
      return false;
    }
    Position p = (Position) o;
    return x == p.x && y == p.y;
  }

  @Override
  public int hashCode() {
    return Objects.hash(x, y);
  }

  @Override
  public int compareTo(Position p) {
    return (x+y) - (p.getX() + p.getY());
  }

  @Override
  public String toString(){
    return x+","+y;
  }

  public static void main(String[] args) {
//    Position aa = new Position(1, 1);
//    Position dd = new Position(1, 1);
//    Position bb = new Position(3, 4);
//    Position cc = new Position(7, 4);
//    System.out.println(aa.equals(bb));
//
//    Set<Position> hset = new HashSet<>();
//    hset.add(aa);
//    hset.add(cc);
//    hset.add(bb);
//
//    Position[] arr = hset.toArray(new Position[0]);
//    Arrays.sort(arr, Collections.reverseOrder());
//    for (Position p : arr) {
//      System.out.println(p);
//    }
  }
}
