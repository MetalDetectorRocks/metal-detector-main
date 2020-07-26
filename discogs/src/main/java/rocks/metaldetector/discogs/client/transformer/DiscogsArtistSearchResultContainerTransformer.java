package rocks.metaldetector.discogs.client.transformer;

import org.springframework.stereotype.Service;
import rocks.metaldetector.discogs.api.DiscogsArtistSearchResult;
import rocks.metaldetector.discogs.api.DiscogsArtistSearchResultContainer;
import rocks.metaldetector.discogs.api.DiscogsPagination;
import rocks.metaldetector.discogs.facade.dto.DiscogsArtistSearchResultDto;
import rocks.metaldetector.discogs.facade.dto.DiscogsArtistSearchResultEntryDto;
import rocks.metaldetector.support.Pagination;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class DiscogsArtistSearchResultContainerTransformer {

  private static final String DISCOGS_NAME_REGEX = " \\([\\d]\\)$";

  public DiscogsArtistSearchResultDto transform(DiscogsArtistSearchResultContainer container) {
    return DiscogsArtistSearchResultDto.builder()
        .pagination(transformPagination(container.getPagination()))
        .searchResults(transformArtistSearchResults(container.getResults()))
        .build();
  }

  private Pagination transformPagination(DiscogsPagination discogsPagination) {
    return Pagination.builder()
        .currentPage(discogsPagination.getCurrentPage())
        .itemsPerPage(discogsPagination.getItemsPerPage())
        .totalPages(discogsPagination.getPagesTotal())
        .build();
  }

  private List<DiscogsArtistSearchResultEntryDto> transformArtistSearchResults(List<DiscogsArtistSearchResult> results) {
    return results.stream()
        .map(this::transformArtistSearchResult)
        .collect(Collectors.toList());
  }

  private DiscogsArtistSearchResultEntryDto transformArtistSearchResult(DiscogsArtistSearchResult result) {
    return DiscogsArtistSearchResultEntryDto.builder()
        .id(String.valueOf(result.getId()))
        .name(transformName(result.getTitle()))
        .imageUrl(result.getThumb())
        .uri(result.getUri())
        .build();
  }

  private String transformName(String artistName) {
    Pattern pattern = Pattern.compile(DISCOGS_NAME_REGEX);
    Matcher matcher = pattern.matcher(artistName);
    return matcher.find() ? artistName.replace(matcher.group(0), "")
                          : artistName;
  }
}
