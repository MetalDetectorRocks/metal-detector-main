package rocks.metaldetector.spotify.client.transformer;

import org.springframework.stereotype.Service;
import rocks.metaldetector.spotify.api.SpotifyPaginatedResult;
import rocks.metaldetector.support.Pagination;

@Service
public class SpotifyPaginationTransformer {

  private static final int SPOTIFY_MAX_ITEMS = 2_000;

  public Pagination transform(SpotifyPaginatedResult paginatedResult) {
    return Pagination.builder()
        .currentPage(paginatedResult.getOffset() / paginatedResult.getLimit() + 1)
        .itemsPerPage(paginatedResult.getLimit())
        .totalPages(calculateTotalPages(paginatedResult))
        .build();
  }

  private int calculateTotalPages(SpotifyPaginatedResult paginatedResult) {
    int total = Math.min(paginatedResult.getTotal(), SPOTIFY_MAX_ITEMS); // Spotify Bug Workaround (see https://bit.ly/34iuJ3Q)
    return divideAndRoundUp(total, paginatedResult.getLimit());
  }

  private int divideAndRoundUp(int dividend, int divisor) {
    return (int) Math.ceil((double) dividend / (double) divisor);
  }
}
