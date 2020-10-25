package rocks.metaldetector.spotify.client.transformer;

import org.springframework.stereotype.Service;
import rocks.metaldetector.spotify.api.SpotifyPaginatedResult;
import rocks.metaldetector.support.Pagination;

@Service
public class SpotifyPaginationTransformer {

  public Pagination transform(SpotifyPaginatedResult paginatedResult) {
    return Pagination.builder()
        .currentPage(paginatedResult.getOffset() / paginatedResult.getLimit() + 1)
        .itemsPerPage(paginatedResult.getLimit())
        .totalPages(calculateTotalPages(paginatedResult))
        .build();
  }

  private int calculateTotalPages(SpotifyPaginatedResult paginatedResult) {
    return (int) Math.ceil((double) paginatedResult.getTotal() / (double) paginatedResult.getLimit());
  }
}
