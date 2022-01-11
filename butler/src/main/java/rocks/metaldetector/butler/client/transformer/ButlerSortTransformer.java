package rocks.metaldetector.butler.client.transformer;

import org.springframework.stereotype.Component;
import rocks.metaldetector.support.DetectorSort;

import java.util.Map;

@Component
public class ButlerSortTransformer {

  private static final Map<String, String> FIELD_MAPPING = Map.of(
    "release_date", "releaseDate",
    "announcement_date", "createdDateTime"
  );

  private static final String DEFAULT_SORTING = "&sort=artist,ASC&sort=albumTitle,ASC";

  public String transform(DetectorSort sort) {
    if (sort == null) {
      return null;
    }
    String sortParam = String.format("sort=%s,%s", FIELD_MAPPING.getOrDefault(sort.getField(), sort.getField()), sort.getDirection());
    return sortParam.concat(DEFAULT_SORTING);
  }
}
