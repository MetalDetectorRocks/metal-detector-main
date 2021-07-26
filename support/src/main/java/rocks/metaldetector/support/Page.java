package rocks.metaldetector.support;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Collections;
import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class Page<T> {

  private final List<T> items;
  private final Pagination pagination;

  public static <T> Page<T> empty() {
    return new Page<>(
            Collections.emptyList(),
            new Pagination(0L, 1, 0)
    );
  }
}
