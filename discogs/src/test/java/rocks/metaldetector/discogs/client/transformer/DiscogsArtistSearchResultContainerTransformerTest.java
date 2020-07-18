package rocks.metaldetector.discogs.client.transformer;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import rocks.metaldetector.discogs.api.DiscogsArtistSearchResult;
import rocks.metaldetector.discogs.api.DiscogsArtistSearchResultContainer;
import rocks.metaldetector.discogs.api.DiscogsPagination;
import rocks.metaldetector.discogs.client.DiscogsDtoFactory.DiscogsArtistSearchResultContainerFactory;
import rocks.metaldetector.discogs.facade.dto.DiscogsArtistSearchResultDto;
import rocks.metaldetector.discogs.facade.dto.DiscogsArtistSearchResultEntryDto;
import rocks.metaldetector.support.Pagination;

class DiscogsArtistSearchResultContainerTransformerTest implements WithAssertions {

  private final DiscogsArtistSearchResultContainerTransformer underTest = new DiscogsArtistSearchResultContainerTransformer();

  @Test
  @DisplayName("Should transform DiscogsPagination to Pagination")
  void should_transform_pagination() {
    // given
    DiscogsArtistSearchResultContainer container = DiscogsArtistSearchResultContainerFactory.createDefault();

    // when
    DiscogsArtistSearchResultDto result = underTest.transform(container);

    // then
    DiscogsPagination discogsPagination = container.getPagination();

    assertThat(result.getPagination()).isEqualTo(
            Pagination.builder()
                      .currentPage(discogsPagination.getCurrentPage())
                      .itemsPerPage(discogsPagination.getItemsPerPage())
                      .totalPages(discogsPagination.getPagesTotal())
                      .build()
    );
  }

  @Test
  @DisplayName("Should transform DiscogsArtistSearchResult to DiscogsArtistSearchResultEntryDto")
  void should_transform() {
    // given
    DiscogsArtistSearchResultContainer container = DiscogsArtistSearchResultContainerFactory.createDefault();

    // when
    DiscogsArtistSearchResultDto result = underTest.transform(container);

    // then
    assertThat(result.getSearchResults().size()).isEqualTo(container.getResults().size());
    for (int index = 0; index < result.getSearchResults().size(); index++) {
      DiscogsArtistSearchResult givenEntry = container.getResults().get(index);
      DiscogsArtistSearchResultEntryDto resultEntry = result.getSearchResults().get(index);
      assertThat(resultEntry).isEqualTo(
              DiscogsArtistSearchResultEntryDto.builder()
                      .id(String.valueOf(givenEntry.getId()))
                      .name(givenEntry.getTitle())
                      .imageUrl(givenEntry.getThumb())
                      .uri(givenEntry.getUri())
                      .followed(false)
                      .build()
      );
    }
  }
}