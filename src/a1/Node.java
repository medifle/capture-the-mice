package a1;

public class Node {
  State state;
  Node parent;
  int depth;

  public Node(State state) {
    this.state = state;
  }

  public Node(State state, Node parent) {
    this.state = state;
    this.parent = parent;
    this.depth = parent.depth + 1;
  }

  @Override
  public String toString() {
    return depth + "@" + state.toString();
  }

  public static void main(String[] args) {
//    Node nd = new Node(new State(8, 1, 1, 2));
//    System.out.println(nd.parent); //null
  }
}
