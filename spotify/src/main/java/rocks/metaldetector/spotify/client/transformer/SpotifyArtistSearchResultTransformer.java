package rocks.metaldetector.spotify.client.transformer;

import org.springframework.stereotype.Service;
import rocks.metaldetector.spotify.api.search.SpotifyArtist;
import rocks.metaldetector.spotify.api.search.SpotifyArtistSearchResult;
import rocks.metaldetector.spotify.api.search.SpotifyArtistSearchResultContainer;
import rocks.metaldetector.spotify.facade.dto.SpotifyArtistSearchResultDto;
import rocks.metaldetector.spotify.facade.dto.SpotifyArtistSearchResultEntryDto;
import rocks.metaldetector.support.Pagination;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SpotifyArtistSearchResultTransformer {

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

  private List<SpotifyArtistSearchResultEntryDto> transformArtistSearchResults(List<SpotifyArtist> results) {
    return results.stream()
        .map(this::transformArtistSearchResult)
        .collect(Collectors.toList());
  }

  private SpotifyArtistSearchResultEntryDto transformArtistSearchResult(SpotifyArtist result) {
    return SpotifyArtistSearchResultEntryDto.builder()
        .id(result.getId())
        .name(result.getName())
        .imageUrl(result.getImages().get(0).getUrl())
        .uri(result.getUri())
        .genres(result.getGenres())
        .popularity(result.getPopularity())
        .build();
  }
}
