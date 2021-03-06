package rocks.metaldetector.support;

public enum ImageSize {
  XS, S, M, L;

  public static ImageSize ofHeight(int height) {
    if (height >= 600) { return L; }
    else if (height >= 300) { return M; }
    else if (height >= 150) { return S; }
    else { return XS; }
  }
}
