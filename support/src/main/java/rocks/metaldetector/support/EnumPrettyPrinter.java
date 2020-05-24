package rocks.metaldetector.support;

import org.apache.commons.text.WordUtils;
import org.springframework.stereotype.Service;

@Service
public class EnumPrettyPrinter {

  public String prettyPrintEnumValue(String value) {
    return value != null ? WordUtils.capitalizeFully(value.replace("_", " ")).trim() : null;
  }
}
