package rocks.metaldetector.discogs.client.transformer;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import rocks.metaldetector.discogs.api.DiscogsArtistSearchResult;
import rocks.metaldetector.discogs.api.DiscogsArtistSearchResultContainer;
import rocks.metaldetector.discogs.api.DiscogsPagination;
import rocks.metaldetector.discogs.client.DiscogsDtoFactory.DiscogsArtistSearchResultContainerFactory;
import rocks.metaldetector.discogs.facade.dto.DiscogsArtistSearchResultDto;
import rocks.metaldetector.discogs.facade.dto.DiscogsArtistSearchResultEntryDto;
import rocks.metaldetector.support.Pagination;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DiscogsArtistSearchResultContainerTransformerTest implements WithAssertions {

  @Mock
  private DiscogsArtistNameTransformer artistNameTransformer;

  @InjectMocks
  private DiscogsArtistSearchResultContainerTransformer underTest;

  @AfterEach
  void tearDown() {
    reset(artistNameTransformer);
  }

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
                      .imageUrl(givenEntry.getThumb())
                      .uri(givenEntry.getUri())
                      .build()
      );
    }
  }

  @Test
  @DisplayName("Should use DiscogsArtistNameTransformer to transform artist name")
  void should_transform_artist_name() {
    // given
    var givenName = "Karg (3)";
    var expectedName = "Karg";
    DiscogsArtistSearchResultContainer container = DiscogsArtistSearchResultContainerFactory.withArtistNames(List.of(givenName));
    doReturn(expectedName).when(artistNameTransformer).transformArtistName(anyString());

    // when
    DiscogsArtistSearchResultDto result = underTest.transform(container);

    // then
    verify(artistNameTransformer).transformArtistName(givenName);
    assertThat(result.getSearchResults().get(0).getName()).isEqualTo(expectedName);
  }
}