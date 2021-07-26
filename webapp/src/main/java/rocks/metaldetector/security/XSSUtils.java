package rocks.metaldetector.security;

import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.owasp.esapi.ESAPI;

public class XSSUtils {

  public static String stripXSS(String value) {
    if (value == null) {
      return null;
    }
    value = ESAPI.encoder()
        .canonicalize(value)
        .replaceAll("\0", "");
    return Jsoup.clean(value, Safelist.none());
  }
}
