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

  String typeName;

  ButlerReleaseType(String typeName) {
    this.typeName = typeName;
  }
}
