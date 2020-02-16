package a1;

import java.awt.geom.Point2D;
import java.util.*;

public class Search {

  private State state;
  private Set<String> stateSpace = new HashSet<>();
  private Board board;

  public Search(State state, Board board) {
    this.state = state;
    this.board = board;
  }

  private boolean testGoal(Node u) {
    return u.state.isCatEnd();
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
    for (Position cp : cheeses) {
      double result = euclideanDistance(p, cp);
      if (result < distance) {
        distance = result;
        minCp = cp;
      }
    }
    // calc mouse next position
    Position rp = genMouseMove(p, minCp);

    Log.d("calcMouseNextPos", "closest cheese " + minCp.toString() +
      "; current position: " + p + "; next position: " + rp);

    return rp;
  }

  /**
   * 7 0 1
   * 6 M 2
   * 5 4 3
   *
   * @param p mouse position
   * @param e cheese position
   */
  private Position genMouseMove(Position p, Position e) {
    Position p0 = new Position(p.getX(), p.getY() - 1);
    Position p1 = new Position(p.getX() + 1, p.getY() - 1);
    Position p2 = new Position(p.getX() + 1, p.getY());
    Position p3 = new Position(p.getX() + 1, p.getY() + 1);
    Position p4 = new Position(p.getX(), p.getY() + 1);
    Position p5 = new Position(p.getX() - 1, p.getY() + 1);
    Position p6 = new Position(p.getX() - 1, p.getY());
    Position p7 = new Position(p.getX() - 1, p.getY() - 1);

    // store possible and valid next mouse positions
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
    if (u.state.isMouseEnd()) {
      Log.d("EXPAND", "mouse end. " + u.state.toString());
      return null;
    }

    Log.d("EXPAND", "u depth " + u.depth + "  " + u.state.toString());

    if (stateSpace.contains(u.state.toString())) {
      Log.d("EXPAND", "state hit. " + u.state.toString());
      return null;
    }

    // add to stateSpace to avoid cycle
    stateSpace.add(u.state.toString());

    List<Position> uMice = u.state.getMice();
    List<Position> uCats = u.state.getCats();
    Set<Position> uCheeses = u.state.getCheeses();
    List<Node> expandedNodes = new LinkedList<>();

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

    /**
     * branching happens, 8 for 1 cat, 8*8 for 2 cats
     */
    if (uCats.size() == 1) {
      Position up = uCats.get(0);
      List<Position> fCats = forkCatPos(up);
      for (Position fp : fCats) {
        List<Position> nextCats = new ArrayList<>();
        nextCats.add(fp);

        // we need a new Set reference but storing the same mice positions which are read only
        List<Position> nextMice = new ArrayList<>(mice);
        // similar to nextMice
        Set<Position> nextCheeses = new HashSet<>(cheeses);

        State state = new State(nextMice, nextCats, nextCheeses);
        Node node = new Node(state, u);
        expandedNodes.add(node);
      }
    } else if (uCats.size() == 2) {
      Position up0 = uCats.get(0);
      Position up1 = uCats.get(1);
      List<Position> fCats0 = forkCatPos(up0);
      List<Position> fCats1 = forkCatPos(up1);
      for (Position fp0 : fCats0) {
        for (Position fp1 : fCats1) {
          List<Position> nextCats = new ArrayList<>();
          nextCats.add(fp0);
          nextCats.add(fp1);

          // we need a new Set reference but storing the same mice positions which are read only
          List<Position> nextMice = new ArrayList<>(mice);
          // similar to nextMice
          Set<Position> nextCheeses = new HashSet<>(cheeses);

          State state = new State(nextMice, nextCats, nextCheeses);
          Node node = new Node(state, u);
          expandedNodes.add(node);
        }
      }
    }

    if (expandedNodes.size() == 0) {
      Log.d("EXPAND", "expandedNodes is empty");
      return null;
    }
    return expandedNodes;
  }

  /**
   * 7    0
   * 6        1
   * C
   * 5        2
   * 4    3
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

    return positions;
  }

  public Queue<State> BFS() {
    int nodeCount = 0;

    Queue<Node> fringe = new LinkedList<>();
    Node r = new Node(state);
    fringe.add(r);

    nodeCount += 1;

    while (!fringe.isEmpty()) {
      Node u = fringe.poll();
      if (testGoal(u)) {
        Log.d("BFS:GOAL", fringe.toString());
        Log.i("BFS", "solution found: " + nodeCount + " nodes searched");
        return genSolution(u);
      }

      List<Node> children = expand(u);
      if (children != null) {
        fringe.addAll(children);
        nodeCount += children.size();
      }
    }

    //run out of searchable nodes
    Log.i("BFS", "solution not found: " + nodeCount + " nodes searched");
    return null;
  }

  public static void main(String[] args) {

  }
}
