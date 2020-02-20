package a1;

import java.util.List;

public class ANode extends Node implements Comparable<ANode> {
  int h = Integer.MAX_VALUE;
  ANode parent;
  List<ANode> children;

  public ANode(State state) {
    super(state);
  }

  public ANode(State state, Node parent) {
    super(state, parent);
  }

  @Override
  public int compareTo(ANode node) {
    // todo: we should consider board size which affects h when it is measure in distance
    // the int(g factor) is g weight which makes a lot of difference, see AStar optimal test
    // if g factor is too larger with respect to the board size, it becomes BFS
    // smaller board needs smaller g factor, is the relationship linear? not known yet
    return (h - node.h) + (depth - node.depth) * 260 * state.getCats().size();
  }

  @Override
  public String toString() {
    return h + "," + depth * 260 * state.getCats().size() + "@" + state;
  }

//  public static void main(String[] args) {
//
//  }
}
