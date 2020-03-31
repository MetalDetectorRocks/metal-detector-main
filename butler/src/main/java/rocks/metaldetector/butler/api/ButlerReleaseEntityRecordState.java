package rocks.metaldetector.butler.api;

public enum ButlerReleaseEntityRecordState {

  DEMO("Demo"),
  NOT_SET("Not set"),
  NEEDS_IMPROVEMENT("Needs improvement"),
  OK("Ok"),
  ARCHIVED("Archived");

  private final String name;

  ButlerReleaseEntityRecordState(String name) {
    this.name = name;
  }

  public String toDisplayString() {
    return this.name;
  }
}
