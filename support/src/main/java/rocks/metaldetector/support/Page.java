package rocks.metaldetector.support;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class Page<T> {

  private final List<T> items;
  private final Pagination pagination;

}
