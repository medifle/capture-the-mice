package a1;

public enum CellType {
  EMPTY(0),
  MOUSE(1),
  CAT(2),
  CHEESE(3);

  private final int cellType;

  CellType(final int cellType) {
    this.cellType = cellType;
  }

  public int getType() {
    return cellType;
  }
}
