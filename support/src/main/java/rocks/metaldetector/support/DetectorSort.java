package rocks.metaldetector.support;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@EqualsAndHashCode
public class DetectorSort {

  @Getter
  private final String field;
  @Getter
  private final Direction direction;

  public DetectorSort(String field, String direction) {
    this(field, direction == null || direction.isBlank() ? Direction.ASC : Direction.valueOf(direction.toUpperCase()));
  }

  public DetectorSort(String field, Direction direction) {
    if (field == null || field.isBlank()) {
      throw new IllegalArgumentException("field must not be null or empty");
    }
    this.field = field;
    this.direction = direction;
  }

  public enum Direction {
    ASC,
    DESC
  }
}
