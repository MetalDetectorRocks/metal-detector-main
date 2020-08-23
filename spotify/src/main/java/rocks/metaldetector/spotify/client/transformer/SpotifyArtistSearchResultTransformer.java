package rocks.metaldetector.spotify.client.transformer;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import rocks.metaldetector.spotify.api.search.SpotifyArtist;
import rocks.metaldetector.spotify.api.search.SpotifyArtistSearchResult;
import rocks.metaldetector.spotify.api.search.SpotifyArtistSearchResultContainer;
import rocks.metaldetector.spotify.facade.dto.SpotifyArtistDto;
import rocks.metaldetector.spotify.facade.dto.SpotifyArtistSearchResultDto;
import rocks.metaldetector.support.Pagination;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class SpotifyArtistSearchResultTransformer {

  private final SpotifyArtistTransformer artistTransformer;

  public SpotifyArtistSearchResultDto transform(SpotifyArtistSearchResultContainer searchResult) {
    return SpotifyArtistSearchResultDto.builder()
        .pagination(transformPagination(searchResult))
        .searchResults(transformArtistSearchResults(searchResult.getArtists().getItems()))
        .build();
  }

  private Pagination transformPagination(SpotifyArtistSearchResultContainer searchResult) {
    return Pagination.builder()
        .currentPage(searchResult.getArtists().getOffset() / searchResult.getArtists().getLimit() + 1)
        .itemsPerPage(searchResult.getArtists().getLimit())
        .totalPages(calculateTotalPages(searchResult.getArtists()))
        .build();
  }

  private int calculateTotalPages(SpotifyArtistSearchResult searchResult) {
    return searchResult.getTotal() % searchResult.getLimit() == 0 ? searchResult.getTotal() / searchResult.getLimit()
                                                                  : searchResult.getTotal() / searchResult.getLimit() + 1;
  }

  private List<SpotifyArtistDto> transformArtistSearchResults(List<SpotifyArtist> results) {
    return results.stream()
        .map(artistTransformer::transform)
        .collect(Collectors.toList());
  }
}
