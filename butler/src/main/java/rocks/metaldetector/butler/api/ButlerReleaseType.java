package rocks.metaldetector.butler.api;

public enum ButlerReleaseType {

  FULL_LENGTH("Full Length"),
  DEMO("Demo"),
  EP("EP"),
  COMPILATION("Compilation"),
  BOXED_SET("Boxed Set"),
  SINGLE("Single"),
  SPLIT("Split"),
  LIVE_ALBUM("Live Album"),
  VIDEO("Video");

  private final String name;

  ButlerReleaseType(String name) {
    this.name = name;
  }

  public String toDisplayString() {
    return this.name;
  }
}
