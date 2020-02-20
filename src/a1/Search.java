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

  private double manhattanDistance(Position p1, Position p2) {
    return Math.abs(p1.getX() - p2.getX()) + Math.abs(p1.getY() - p2.getY());
  }

  private double hybridDistance(Position p1, Position p2) {
    return (euclideanDistance(p1, p2) + manhattanDistance(p1, p2)) / 2;
  }

  private Position calcClosestPos(Position p, Position[] posArr) {
    double distance = Double.MAX_VALUE;
    Position minPos = null;
    for (Position cp : posArr) {
      double result = euclideanDistance(p, cp);
      if (result < distance) {
        distance = result;
        minPos = cp;
      }
    }
    return minPos;
  }

  private Position calcMouseNextPos(Position p, Set<Position> cheeses) {
    if (cheeses.size() == 0) {
      Log.d("calcMouseNextPos", "cheese set is empty");
      return null;
    }

    // Mouse cheese preference for South-East direction
    Position[] cheeseArr = cheeses.toArray(new Position[0]);
    Arrays.sort(cheeseArr, Collections.reverseOrder());

    // get closest cheese by calculating Euclidean distance
    Position minCp = calcClosestPos(p, cheeseArr);

    // calc mouse next position
    Position rp = genMouseMove(p, minCp, mouseSpeed);

    Log.s("calcMouseNextPos", "closest cheese " + minCp.toString() +
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

    List<Position> positions = new ArrayList<>();
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

  /**
   * Expand a node to get its children
   * @param u the node to be expanded
   * @param nodeType  0: Node, 1: Anode
   */
  private List<Node> expand(Node u, int nodeType) {
    Log.d("EXPAND", "" + u);

    List<Position> uMice = u.state.getMice();
    List<Position> uCats = u.state.getCats();
    Set<Position> uCheeses = u.state.getCheeses();

    // get a new cheeses Set reference but storing the same cheese Positions
    Set<Position> cheeses = new HashSet<>(uCheeses);

    // generate new mouse positions
    List<Position> mice = new ArrayList<>();
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

    List<Node> expandedNodes = new ArrayList<>();
    // generate state, then node
    if (nextCatsList != null) {
      for (List<Position> nextCats : nextCatsList) {
        // we need a new Set reference but storing the same mice positions which are read only
        List<Position> nextMice = new ArrayList<>(mice);
        // similar to nextMice
        Set<Position> nextCheeses = new HashSet<>(cheeses);

        // sort cats to ensure state check is working
        // e.g. cats[(2,1),(1,2)] and cats[(1,2),(2,1)] should be considered as the same if
        // cheeses and mice positions are the same.
        Collections.sort(nextCats);
        State state = new State(nextMice, nextCats, nextCheeses);
        Node node = null;
        if (nodeType == 0) {
          node = new Node(state, u);
        } else if (nodeType == 1) {
          node = new ANode(state, (ANode)u);
        }
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

    List<List<Position>> hydratedCats = new ArrayList<>();

    if (forkCatsList.isEmpty()) {
      return null;
    }

    // base case
    if (forkCatsList.size() == 1) {
      List<Position> fCats = forkCatsList.peek();
      for (Position fp : fCats) {
        List<Position> nextCats = new ArrayList<>();
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
        List<Position> nextCats = new ArrayList<>(baseNextCats);
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
    List<Position> positions = new ArrayList<>();
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
   */
  private boolean isValidState(State state, String logTag) {
    if (state.isMouseEnd()) {
      Log.d(logTag, "Mouse end. " + state.toString());
      return false;
    }
    return true;
  }

  /**
   * new: not in state space
   */
  private boolean isNewSetState(State state, String logTag) {
    if (stateSpace.contains(state)) {
      hitCount += 1;
      Log.d(logTag, "state hit. " + state.toString());
      return false;
    }
    return true;
  }

  private boolean isValidNewSetState(State state, String logTag) {
    return isValidState(state, logTag) && isNewSetState(state, logTag);
  }

  /**
   * Check if the ANode state is a key in map
   */
  private boolean isNewMapState(Map<State, ANode> map, State state, String logTag) {
    if (map.containsKey(state)) {
      hitCount += 1;
      Log.d(logTag, "state hit. " + state.toString());
      return false;
    }
    return true;
  }

  public Queue<State> BFS() {
    int nodeCount = 0;

    Queue<Node> fringe = new LinkedList<>();
    Node r = new Node(state);
    nodeCount += 1;

    Log.i("BFS", "initial state: " + state);

    if (testGoal(r)) {
      Log.i("BFS", "solution found: " + nodeCount + " nodes searched");
      return genSolution(r);
    }
    if (isValidNewSetState(r.state, "BFS")) {
      // add to stateSpace to avoid cycle
      stateSpace.add(r.state);
      fringe.add(r);
    }

    while (!fringe.isEmpty()) {
      Node u = fringe.poll();
      List<Node> children = expand(u, 0);
      if (children != null) {
        nodeCount += children.size();
        for (Node child : children) {
          if (testGoal(child)) {
            Log.i("BFS", "solution found: " + nodeCount + " nodes searched");
            Log.i("BFS", "hitCount: " + hitCount);
            return genSolution(child);
          }
          if (isValidNewSetState(child.state, "BFS")) {
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

    Log.i("DFS", "initial state: " + state);

    if (testGoal(r)) {
      Log.i("DFS", "solution found: " + nodeCount + " nodes searched");
      return genSolution(r);
    }
    if (isValidNewSetState(r.state, "DFS")) {
      stateSpace.add(r.state);
      fringe.push(r);
    }

    while (!fringe.empty()) {
      Node u = fringe.pop();
      List<Node> children = expand(u, 0);
      if (children != null) {
        nodeCount += children.size();
        Collections.reverse(children); // optional
        for (Node child : children) {
          if (testGoal(child)) {
            Log.i("DFS", "solution found: " + nodeCount + " nodes searched");
            Log.i("DFS", "hitCount: " + hitCount);
            return genSolution(child);
          }
          if (isValidNewSetState(child.state, "DFS")) {
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
    if (isValidNewSetState(r.state, "DLS")) {
      stateSpace.add(r.state);
      if (r.depth < depth) {
        fringe.push(r);
      }
    }

    while (!fringe.empty()) {
      Node u = fringe.pop();
      List<Node> children = expand(u, 0);
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
          if (isValidNewSetState(child.state, "DLS")) {
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
    Log.i("IDDFS", "initial state: " + state);
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

  private void AStarNodeUpdate(Queue<ANode> open, Map<State, ANode> map, ANode u, int gDiff) {
    Log.d("AStar_AStarNodeUpdate", "" + u + "  gDiff " + gDiff);
    ANode old = map.get(u.state);
    Log.d("AStar_AStarNodeUpdate_OLD", "" + old);
    if (u.compareTo(old) < 0) { // thisNode has a better f(n)
      // change oldNode parent
      boolean isRemoved = old.parent.children.remove(old);
      old.parent = u.parent;
      boolean isRemoved2 = old.parent.children.remove(u);
      boolean isAdded = old.parent.children.add(old);
      Log.i("AStar_TEST_AStarNodeUpdate_CHANGE_PARENT", "" + isRemoved + isRemoved2 + isAdded);
      // update old Node cost g, which is depth. h is the same, no need to change
      gDiff = old.depth - u.depth;
      old.depth -= gDiff;
      // update its order in open priority queue
      open.remove(old);
      open.add(old);
    }
    if (gDiff > 0) {
      // update children and subtrees g cost using iterative DFS
      Stack<ANode> cStack = new Stack<>();
      if (old.children != null) {
        cStack.addAll(old.children);
        while (!cStack.empty()) {
          ANode child = cStack.pop();
          child.depth -= gDiff;
          AStarNodeUpdate(open, map, child, gDiff);
        }
      }
    }
  }

  /**
   * A* Search
   * @param heuristic 0: Euclidean distance, 1: Manhattan distance, 2: hybrid of 0 and 1
   */
  public Queue<State> AStar(int heuristic) {
    nodeCount = 0;
    Map<State, ANode> stateNodeMap = new HashMap<>();
    Queue<ANode> open = new PriorityQueue<>();

    ANode r = new ANode(state);
    nodeCount += 1;

    Log.i("AStar", "initial state: " + state);

    if (testGoal(r)) {
      Log.i("AStar", "solution found: " + nodeCount + " nodes searched");
      return genSolution(r);
    }
    if (isValidState(r.state, "AStar")) {
      r.h = evaluate(r.state, heuristic);
      stateNodeMap.put(r.state, r);
      open.add(r);
    }

    while (!open.isEmpty()) {
      ANode u = open.poll();

      if (u.children == null) {
        u.children = new ArrayList<>();
      }
      List<Node> children = expand(u, 1);
      if (children != null) {
        nodeCount += children.size();
        for (Node nc : children) {
          ANode child = (ANode) nc;
          child.h = evaluate(child.state, heuristic);
          Log.d("AStar", "child: " + child);

          if (testGoal(child)) {
            Log.i("AStar", "solution found: " + nodeCount + " nodes searched");
            Log.i("AStar", "hitCount: " + hitCount);
            Log.i("AStar", "node: " + child);
            return genSolution(child);
          }
          if (isValidState(child.state, "AStar")) {
            u.children.add(child);
            if (isNewMapState(stateNodeMap, child.state, "AStar")) {
              stateNodeMap.put(child.state, child);
              open.add(child);
            } else {
              AStarNodeUpdate(open, stateNodeMap, child, 0);
            }
          }
        }
      }
    }

    // run out of searchable nodes
    Log.i("AStar", "solution not found: " + nodeCount + " nodes searched");
    return null;
  }

  /**
   * Simplified A* search focusing on memory efficiency
   * As in this game we don't encounter the case that an existing node needs to change parent and update subtree
   * So we don't have to store all Node references
   * @param heuristic 0: Euclidean distance, 1: Manhattan distance, 2: hybrid of 0 and 1
   */
  public Queue<State> AStarME(int heuristic) {
    nodeCount = 0;
    Queue<ANode> open = new PriorityQueue<>();

    ANode r = new ANode(state);
    nodeCount += 1;

    Log.i("AStarME", "initial state: " + state);

    if (testGoal(r)) {
      Log.i("AStarME", "solution found: " + nodeCount + " nodes searched");
      return genSolution(r);
    }
    if (isValidState(r.state, "AStarME")) {
      r.h = evaluate(r.state, heuristic);
      stateSpace.add(r.state);
      open.add(r);
    }

    while (!open.isEmpty()) {
      Node u = open.poll();

      List<Node> children = expand(u, 1);
      if (children != null) {
        nodeCount += children.size();
        for (Node nc : children) {
          ANode child = (ANode) nc;
          child.h = evaluate(child.state, heuristic);
          Log.d("AStarME", "child: " + child);

          if (testGoal(child)) {
            Log.i("AStarME", "solution found: " + nodeCount + " nodes searched");
            Log.i("AStarME", "hitCount: " + hitCount);
            Log.i("AStarME", "node: " + child);
            return genSolution(child);
          }
          if (isValidNewSetState(child.state, "AStarME")) {
            stateSpace.add(child.state);
            open.add(child);
          }
        }
      }
    }

    // run out of searchable nodes
    Log.i("AStar", "solution not found: " + nodeCount + " nodes searched");
    return null;
  }

  /**
   * Calculate h value for Node u
   * @param heuristic 0: Euclidean distance, 1: Manhattan distance, 2: hybrid of 0 and 1
   */
  private int evaluate(State state, int heuristic) {
    List<Position> mice = state.getMice();
    List<Position> cats = state.getCats();
    Set<Position> cheeses = state.getCheeses();

    double h = 0;

    for (Position cp : cats) {
      double subH = calcH(heuristic, cp, mice);
//      System.out.println("subH " + subH);//test
      h += subH;
    }
    h += mice.size() * 100; // punishment for existing mice number
    h -= cheeses.size() * 100; // award for existing cheeses number
    return (int) Math.round(h * 10); // 10 is h_factor which correlates strongly with g_factor
  }

  private double calcH(int heuristic, Position p, Collection<Position> collection) {
    if (collection.size() == 0) {
      return 0;
    }

    double minDistanceToBase = Double.MAX_VALUE;
    for (Position cp : collection) {
      double result;
      if (heuristic == 0) {
        result = euclideanDistance(p, cp);
      } else if (heuristic == 1) {
        result = manhattanDistance(p, cp);
      } else if (heuristic == 2) {
        result = hybridDistance(p, cp);
      } else {
        return 0;
      }
      if (result < minDistanceToBase) {
        minDistanceToBase = result;
      }
    }
    return minDistanceToBase;
  }

  /**
   * Get h for a given state and print state to board
   */
  public static void analyzeState(int rows, int cols, int heuristic, String stateStr) {
    Board bd = new Board(rows, cols);
    State state = bd.loadState(stateStr);
    Game.printState(state, rows, cols);

    Search ai = new Search(state, bd, 1);
    int h = ai.evaluate(state, heuristic);
    System.out.println(h);
  }

  public static void main(String[] args) {

//    analyzeState(16, 16, 2, "12,5;-11,4;8,10;-2,4;13,1;1,6;13,3;13,5;5,14;3,14;");
//    analyzeState(16, 16, 2, "11,5;-10,6;7,12;-2,4;13,1;1,6;13,3;13,5;5,14;3,14;");
  }
}
