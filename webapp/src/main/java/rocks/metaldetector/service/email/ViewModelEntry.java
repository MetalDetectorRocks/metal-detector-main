package rocks.metaldetector.service.email;

record ViewModelEntry(String name, Object value, boolean prependFrontendUrl) {
  public ViewModelEntry(String name, Object value) {
    this(name, value, false);
  }
}
