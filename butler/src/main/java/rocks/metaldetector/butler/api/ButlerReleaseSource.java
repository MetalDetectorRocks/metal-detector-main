package rocks.metaldetector.butler.api;

public enum ButlerReleaseSource {

  METAL_ARCHIVES("Encyclopaedia Metallum: The Metal Archives"),
  METAL_HAMMER_DE("Metal Hammer Germany");

  private final String name;

  ButlerReleaseSource(String name) {
    this.name = name;
  }

  public String toDisplayString() {
    return this.name;
  }
}
