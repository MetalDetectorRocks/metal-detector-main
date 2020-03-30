package rocks.metaldetector.butler.api;

public enum ButlerReleaseSource {

  METAL_ARCHIVES("Encyclopaedia Metallum: The Metal Archives"),
  METAL_HAMMER_DE("Metal Hammer Germany");

  final String name;

  ButlerReleaseSource(String name) {
    this.name = name;
  }
}
