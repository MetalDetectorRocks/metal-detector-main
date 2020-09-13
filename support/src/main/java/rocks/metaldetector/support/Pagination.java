package rocks.metaldetector.support;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pagination {

  private int totalPages;
  private int currentPage;
  private int itemsPerPage;

  public Pagination(long totalItems, int page, int size) {
    this.totalPages = (int) Math.ceil((double) totalItems / size);
    this.currentPage = page;
    this.itemsPerPage = size;
  }
}
