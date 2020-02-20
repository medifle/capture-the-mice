package a1;

import java.util.List;

public class Node implements Comparable<Node> {
  State state;
  Node parent;
  int depth;
  int h = Integer.MAX_VALUE;
  List<Node> children;

  public Node(State state) {
    this.state = state;
  }

  public Node(State state, Node parent) {
    this.state = state;
    this.parent = parent;
    this.depth = parent.depth + 1;
  }

  @Override
  public int compareTo(Node node) {
    // todo: we should consider board size which affects h when it is measure in distance
    // the int 10 is g weight which makes a lot of difference, see AStar optimal test
    return (h - node.h) + (depth - node.depth) * 10 * state.getCats().size();
  }

  @Override
  public String toString() {
    return h + "," + depth * 10 * state.getCats().size() + "@" + state.toString();
  }

  public static void main(String[] args) {
//    Node nd = new Node(new State(8, 1, 1, 2));
//    System.out.println(nd.parent); //null
  }
}
