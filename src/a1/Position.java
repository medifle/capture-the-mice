package a1;

import java.util.Objects;

public class Position{
  private int x;
  private int y;

  public Position(int x, int y){
    this.x = x;
    this.y = y;
  }

  public int getX(){ return x; }
  public int getY(){ return y; }

  public void update(int x, int y) {
    this.x = x;
    this.y = y;
  }

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
  public String toString(){
    return x+","+y;
  }

  public static void main(String[] args) {
    Position aa = new Position(3, 4);
    Position bb = new Position(3, 4);
    System.out.println(aa.equals(bb));
  }
}
