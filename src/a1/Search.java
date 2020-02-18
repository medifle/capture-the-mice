package a1;

import java.awt.geom.Point2D;
import java.util.*;

public class Search {

  private State originState;
  private State state;
  private Set<State> stateSpace = new HashSet<>();
  private Board board;

  private int nodeCount;
  private int hitCount;
  private int mouseSpeed = 1;

  public Search(State state, Board board) {
    this.originState = state;
    this.state = state;
    this.board = board;
  }

  public Search(State state, Board board, int mouseSpeed) {
    this(state, board);
    this.mouseSpeed = mouseSpeed;
  }

  private double euclideanDistance(Position p1, Position p2) {
    return Point2D.distance(p1.getX(), p1.getY(), p2.getX(), p2.getY());
  }

  private Position calcMouseNextPos(Position p, Set<Position> cheeses) {
    if (cheeses.size() == 0) {
      Log.d("calcMouseNextPos", "cheese set is empty");
      return null;
    }

    // get closest cheese by calculating Euclidean distance
    double distance = Double.MAX_VALUE;
    Position minCp = null;

    // Mouse cheese preference for South-East direction
    Position[] cheeseArr = cheeses.toArray(new Position[0]);
    Arrays.sort(cheeseArr, Collections.reverseOrder());

    for (Position cp : cheeseArr) {
      double result = euclideanDistance(p, cp);
      if (result < distance) {
        distance = result;
        minCp = cp;
      }
    }
    // calc mouse next position
    Position rp = genMouseMove(p, minCp, mouseSpeed);

    Log.d("calcMouseNextPos", "closest cheese " + minCp.toString() +
      "; current position: " + p + "; next position: " + rp);

    return rp;
  }

  /**
   * 7 6 5
   * 4 M 3
   * 2 1 0
   *
   * @param p     mouse position
   * @param e     cheese position
   * @param speed mouse step range per turn
   */
  private Position genMouseMove(Position p, Position e, int speed) {
    int range = speed * 2 + 1;
    int[] xBase = new int[range];
    int[] yBase = new int[range];

    for (int i = 0, j = speed; i < xBase.length; ++i) {
      xBase[i] = j;
      j -= 1;
    }
    System.arraycopy(xBase, 0, yBase, 0, range);

    List<Position> positions = new LinkedList<>();
    for (int y : yBase) {
      for (int x : xBase) {
        if (y != 0 || x != 0) {
          Position tp = new Position(p.getX() + x, p.getY() + y);
          // store possible and valid next mouse positions
          if (board.isValidPos(tp)) {
            positions.add(tp);
          }
        }
      }
    }

    if (positions.size() == 0) {
      Log.d("genMouseMove", "mouse runs out of space");
      return null;
    }

    // get the next position closest to cheese
    double distance = Double.MAX_VALUE;
    Position rp = null;
    for (Position np : positions) {
      double result = euclideanDistance(np, e);
      if (result < distance) {
        distance = result;
        rp = np;
      }
    }

    return rp;
  }

  private List<Node> expand(Node u) {
    Log.d("EXPAND", "u depth " + u.depth + "  " + u.state.toString());

    List<Position> uMice = u.state.getMice();
    List<Position> uCats = u.state.getCats();
    Set<Position> uCheeses = u.state.getCheeses();

    // get a new cheeses Set reference but storing the same cheese Positions
    Set<Position> cheeses = new HashSet<>(uCheeses);

    // generate new mouse positions
    List<Position> mice = new LinkedList<>();
    for (Position p : uMice) {
      Position nextPos = calcMouseNextPos(p, cheeses);
      if (nextPos != null) {
        mice.add(nextPos);
      }
      // test
      else {
        Log.d("EXPAND", "calcMouseNextPos is null" + u.toString());
      }
    }
    Collections.sort(mice);

    // branching happens, 8 for 1 cat, 8*8 for 2 cats...
    List<List<Position>> nextCatsList = calcCatsNextPos(uCats);

    List<Node> expandedNodes = new LinkedList<>();
    // generate state, then node
    if (nextCatsList != null) {
      for (List<Position> nextCats : nextCatsList) {
        // we need a new Set reference but storing the same mice positions which are read only
        List<Position> nextMice = new LinkedList<>(mice);
        // similar to nextMice
        Set<Position> nextCheeses = new HashSet<>(cheeses);

        // sort cats to ensure state check is working
        // e.g. cats[(2,1),(1,2)] and cats[(1,2),(2,1)] should be considered as the same if
        // cheeses and mice positions are the same.
        Collections.sort(nextCats);
        State state = new State(nextMice, nextCats, nextCheeses);
        Node node = new Node(state, u);
        expandedNodes.add(node);
      }
    }

    if (expandedNodes.size() == 0) {
      Log.d("EXPAND", "expandedNodes is empty");
      return null;
    }
    return expandedNodes;
  }

  private List<List<Position>> calcCatsNextPos(List<Position> uCats) {
    Queue<List<Position>> forkCatsList = new LinkedList<>();
    for (Position up : uCats) {
      List<Position> fCats = forkCatPos(up);
      if (fCats != null) {
        forkCatsList.add(fCats);
      }
    }

    return hydrateCats(forkCatsList);
  }

  /**
   * Recursively generate a hydrated list of nextCats
   *
   * @param forkCatsList a list containing all cats next possible positions
   * @return a hydrated list a nextCats
   */
  private List<List<Position>> hydrateCats(Queue<List<Position>> forkCatsList) {

    List<List<Position>> hydratedCats = new LinkedList<>();

    if (forkCatsList.isEmpty()) {
      return null;
    }

    // base case
    if (forkCatsList.size() == 1) {
      List<Position> fCats = forkCatsList.peek();
      for (Position fp : fCats) {
        List<Position> nextCats = new LinkedList<>();
        nextCats.add(fp);
        hydratedCats.add(nextCats);
      }
      return hydratedCats;
    }

    // divide and conquer
    List<Position> unHydratedCats = forkCatsList.poll();
    List<List<Position>> restHydratedCats = hydrateCats(forkCatsList);

    // combine
    for (Position uhp : unHydratedCats) {
      assert restHydratedCats != null;
      for (List<Position> baseNextCats : restHydratedCats) {
        List<Position> nextCats = new LinkedList<>(baseNextCats);
        nextCats.add(uhp);
        hydratedCats.add(nextCats);
      }
    }

    return hydratedCats;
  }

  /*
   *   7    0
   * 6        1
   *     C
   * 5        2
   *   4    3
   */
  private List<Position> forkCatPos(Position p) {
    Position p0 = new Position(p.getX() + 1, p.getY() - 2);
    Position p1 = new Position(p.getX() + 2, p.getY() - 1);
    Position p2 = new Position(p.getX() + 2, p.getY() + 1);
    Position p3 = new Position(p.getX() + 1, p.getY() + 2);
    Position p4 = new Position(p.getX() - 1, p.getY() + 2);
    Position p5 = new Position(p.getX() - 2, p.getY() + 1);
    Position p6 = new Position(p.getX() - 2, p.getY() - 1);
    Position p7 = new Position(p.getX() - 1, p.getY() - 2);

    // branching for one cat position is at most 8
    List<Position> positions = new LinkedList<>();
    if (board.isValidPos(p0)) positions.add(p0);
    if (board.isValidPos(p1)) positions.add(p1);
    if (board.isValidPos(p2)) positions.add(p2);
    if (board.isValidPos(p3)) positions.add(p3);
    if (board.isValidPos(p4)) positions.add(p4);
    if (board.isValidPos(p5)) positions.add(p5);
    if (board.isValidPos(p6)) positions.add(p6);
    if (board.isValidPos(p7)) positions.add(p7);

    if (positions.size() == 0) {
      return null;
    }

    return positions;
  }

  private Queue<State> genSolution(Node u) {
    LinkedList<State> q = new LinkedList<>();

    while (u != null) {
      q.push(u.state);
      Log.i("genSolution", "depth " + u.depth + "  " + u.state.toString());
      u = u.parent;
    }

    return q;
  }

  private boolean testGoal(Node u) {
    return u.state.isCatEnd();
  }

  /**
   * valid: not Mouse End
   * new: not in state space
   */
  private boolean isValidNewState(Node u, String logTag) {
    if (u.state.isMouseEnd()) {
      Log.d(logTag, "Mouse end. " + u.state.toString());
      return false;
    }
    if (stateSpace.contains(u.state)) {
      hitCount += 1;
      Log.d(logTag, "state hit. " + u.state.toString());
      return false;
    }
    return true;
  }

  public Queue<State> BFS() {
    int nodeCount = 0;

    Queue<Node> fringe = new LinkedList<>();
    Node r = new Node(state);
    nodeCount += 1;

    if (testGoal(r)) {
      Log.i("BFS", "solution found: " + nodeCount + " nodes searched");
      return genSolution(r);
    }
    if (isValidNewState(r, "BFS")) {
      // add to stateSpace to avoid cycle
      stateSpace.add(r.state);
      fringe.add(r);
    }

    while (!fringe.isEmpty()) {
      Node u = fringe.poll();
      List<Node> children = expand(u);
      if (children != null) {
        nodeCount += children.size();
        for (Node child : children) {
          if (testGoal(child)) {
            Log.i("BFS", "solution found: " + nodeCount + " nodes searched");
            Log.i("BFS", "hitCount: " + hitCount);
            return genSolution(child);
          }
          if (isValidNewState(child, "BFS")) {
            stateSpace.add(child.state);
            fringe.add(child);
          }
        }
      }
    }

    // run out of searchable nodes
    Log.i("BFS", "solution not found: " + nodeCount + " nodes searched");
    return null;
  }

  /**
   * Iterative version of Depth-first Search using Stack
   */
  public Queue<State> DFS() {
    int nodeCount = 0;

    Stack<Node> fringe = new Stack<>();
    Node r = new Node(state);
    nodeCount += 1;

    if (testGoal(r)) {
      Log.i("DFS", "solution found: " + nodeCount + " nodes searched");
      return genSolution(r);
    }
    if (isValidNewState(r, "DFS")) {
      stateSpace.add(r.state);
      fringe.push(r);
    }

    while (!fringe.empty()) {
      Node u = fringe.pop();
      List<Node> children = expand(u);
      if (children != null) {
        nodeCount += children.size();
        Collections.reverse(children); // optional
        for (Node child : children) {
          if (testGoal(child)) {
            Log.i("DFS", "solution found: " + nodeCount + " nodes searched");
            Log.i("DFS", "hitCount: " + hitCount);
            return genSolution(child);
          }
          if (isValidNewState(child, "DFS")) {
            stateSpace.add(child.state);
            fringe.push(child);
          }
        }
      }
    }

    // run out of searchable nodes
    Log.i("DFS", "solution not found: " + nodeCount + " nodes searched");
    return null;
  }

  /**
   * Iterative version of Depth-limited Search
   *
   * @param depth start from 0 at root
   */
  public Queue<State> DLS(int depth, boolean clearNodeCount) {
    stateSpace.clear();

    Stack<Node> fringe = new Stack<>();
    Node r = new Node(originState);

    if (clearNodeCount) {
      nodeCount = 0;
    }
    nodeCount += 1;

    if (testGoal(r)) {
      Log.i("DLS", "solution found: depth " + depth
        + "  " + nodeCount + " nodes searched");
      return genSolution(r);
    }
    if (isValidNewState(r, "DLS")) {
      stateSpace.add(r.state);
      if (r.depth < depth) {
        fringe.push(r);
      }
    }

    while (!fringe.empty()) {
      Node u = fringe.pop();
      List<Node> children = expand(u);
      if (children != null) {
        nodeCount += children.size();
        Collections.reverse(children); // optional
        // Children are all at the same depth, so we pick one to check the depth
        // (children size must > 0 at this line)
        int childDepth = children.get(0).depth;
        for (Node child : children) {
          if (testGoal(child)) {
            Log.i("DLS", "solution found: depth " + depth
              + "  " + nodeCount + " nodes searched");
            Log.i("DLS", "hitCount: " + hitCount);
            return genSolution(child);
          }
          if (isValidNewState(child, "DLS")) {
            stateSpace.add(child.state);
            // If child depth is the same as depth arg, discard it. No need to expand it(put to fringe)
            if (childDepth < depth) {
              fringe.push(child);
            }
          }
        }
      }
    }

    // run out of searchable nodes
    Log.i("DLS", "solution not found: depth " + depth
      + "  " + nodeCount + " nodes searched");
    return null;
  }

  /**
   * Iterative deepening depth-first search
   */
  public Queue<State> IDDFS() {
    nodeCount = 0;
    Log.i("IDDFS", "Start searching...");

    for (int i = 0; i < Integer.MAX_VALUE; ++i) {
      Queue<State> result = DLS(i, false);
      if (result != null) {
        return result;
      }
    }

    // run out of searchable nodes
    Log.i("IDDFS", "solution not found: " + nodeCount + " nodes searched");
    return null;
  }

  /**
   * A* Search
   */
//  public Queue<State> Astar() {
//
//  }

//  public static void main(String[] args) {
//
//  }
}
