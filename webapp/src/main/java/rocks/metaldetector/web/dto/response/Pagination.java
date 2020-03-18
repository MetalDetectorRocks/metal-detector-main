package rocks.metaldetector.web.dto.response;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@EqualsAndHashCode
public class Pagination {

  private int totalPages;
  private int currentPage;
  private int itemsPerPage;

  public Pagination(long total, int page, int size) {
    this.totalPages = (int) Math.ceil((double) total / size);
    this.currentPage = page;
    this.itemsPerPage = size;
  }

}