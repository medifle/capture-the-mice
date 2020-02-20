package a1;

public enum HeuristicType {
  EUCLIDEAN(0),
  MANHATTAN(1),
  HYBRID(2);

  private final int heuristicType;

  HeuristicType(final int heuristicType) {
    this.heuristicType = heuristicType;
  }

  public int getType() {
    return heuristicType;
  }
}
