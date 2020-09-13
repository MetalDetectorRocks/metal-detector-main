package rocks.metaldetector.support;

import org.apache.commons.text.WordUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EnumPrettyPrinter {

  private static final List<String> EXEMPTIONS = List.of(
          "EP"
  );

  public String prettyPrintEnumValue(String value) {
    if (value == null) {
      return null;
    }
    else if (EXEMPTIONS.contains(value)) {
      return value;
    }
    else {
      return WordUtils.capitalizeFully(value.replace("_", " ")).trim();
    }
  }
}
