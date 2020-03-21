package rocks.metaldetector.discogs.facade.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DiscogsSearchResultDto<T> {

  private List<T> entries;

  // ToDo DanielW: Welche braucht man wirklich?
  private int itemsPerPage;
  private int itemsTotal;
  private int currentPage;
  private int pagesTotal;
}
