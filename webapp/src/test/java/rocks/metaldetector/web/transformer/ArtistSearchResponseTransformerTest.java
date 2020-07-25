package rocks.metaldetector.web.transformer;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import rocks.metaldetector.discogs.facade.dto.DiscogsArtistSearchResultDto;
import rocks.metaldetector.spotify.facade.dto.SpotifyArtistSearchResultDto;
import rocks.metaldetector.support.Pagination;
import rocks.metaldetector.web.api.response.ArtistSearchResponse;
import rocks.metaldetector.web.api.response.ArtistSearchResponseEntryDto;

import java.util.List;

import static rocks.metaldetector.testutil.DtoFactory.ArtistSearchResponseEntryDtoFactory;
import static rocks.metaldetector.testutil.DtoFactory.DiscogsArtistSearchResultDtoFactory;
import static rocks.metaldetector.testutil.DtoFactory.SpotifyArtistSearchResultDtoFactory;

@ExtendWith(MockitoExtension.class)
class ArtistSearchResponseTransformerTest implements WithAssertions {

  private ArtistSearchResponseTransformer underTest = new ArtistSearchResponseTransformer();

  @Test
  @DisplayName("Spotify pagination is transformed")
  void test_transform_spotify_pagination() {
    // given
    SpotifyArtistSearchResultDto searchResultDto = SpotifyArtistSearchResultDtoFactory.createDefault();
    Pagination expectedPagination = new Pagination(1, 1, 10);

    // when
    ArtistSearchResponse result = underTest.transformSpotify(searchResultDto);

    // then
    assertThat(result.getPagination().getCurrentPage()).isEqualTo(expectedPagination.getCurrentPage());
    assertThat(result.getPagination().getItemsPerPage()).isEqualTo(expectedPagination.getItemsPerPage());
    assertThat(result.getPagination().getTotalPages()).isEqualTo(expectedPagination.getTotalPages());  }

  @Test
  @DisplayName("Spotify search result is transformed")
  void test_transform_spotify_search_result() {
    // given
    SpotifyArtistSearchResultDto searchResultDto = SpotifyArtistSearchResultDtoFactory.createDefault();
    List<ArtistSearchResponseEntryDto> expectedSearchResults = List.of(ArtistSearchResponseEntryDtoFactory.spotifyWithArtistName("A"),
                                                                       ArtistSearchResponseEntryDtoFactory.spotifyWithArtistName("B"));

    // when
    ArtistSearchResponse result = underTest.transformSpotify(searchResultDto);

    // then
    assertThat(result.getSearchResults().size()).isEqualTo(expectedSearchResults.size());
    for (int i = 0; i < result.getSearchResults().size(); i++) {
      ArtistSearchResponseEntryDto resultEntry = result.getSearchResults().get(i);
      ArtistSearchResponseEntryDto expectedResultEntry = expectedSearchResults.get(i);

      assertThat(resultEntry.getId()).isEqualTo(expectedResultEntry.getId());
      assertThat(resultEntry.getImageUrl()).isEqualTo(expectedResultEntry.getImageUrl());
      assertThat(resultEntry.getName()).isEqualTo(expectedResultEntry.getName());
      assertThat(resultEntry.getPopularity()).isEqualTo(expectedResultEntry.getPopularity());
      assertThat(resultEntry.getGenres()).isEqualTo(expectedResultEntry.getGenres());
      assertThat(resultEntry.getUri()).isEqualTo(expectedResultEntry.getUri());
      assertThat(resultEntry.getSource()).isEqualTo(expectedResultEntry.getSource());
      assertThat(resultEntry.isFollowed()).isEqualTo(expectedResultEntry.isFollowed());
    }
  }

  @Test
  @DisplayName("Discogs pagination is transformed")
  void test_transform_discogs_pagination() {
    // given
    DiscogsArtistSearchResultDto searchResultDto = DiscogsArtistSearchResultDtoFactory.createDefault();
    Pagination expectedPagination = new Pagination(1, 1, 10);

    // when
    ArtistSearchResponse result = underTest.transformDiscogs(searchResultDto);

    // then
    assertThat(result.getPagination().getCurrentPage()).isEqualTo(expectedPagination.getCurrentPage());
    assertThat(result.getPagination().getItemsPerPage()).isEqualTo(expectedPagination.getItemsPerPage());
    assertThat(result.getPagination().getTotalPages()).isEqualTo(expectedPagination.getTotalPages());
  }

  @Test
  @DisplayName("Discogs search result is transformed")
  void test_transform_discogs_search_result() {
    // given
    DiscogsArtistSearchResultDto searchResultDto = DiscogsArtistSearchResultDtoFactory.createMultiple();
    List<ArtistSearchResponseEntryDto> expectedSearchResults = List.of(ArtistSearchResponseEntryDtoFactory.discogsWithArtistName("A"),
                                                                       ArtistSearchResponseEntryDtoFactory.discogsWithArtistName("B"));

    // when
    ArtistSearchResponse result = underTest.transformDiscogs(searchResultDto);

    // then
    assertThat(result.getSearchResults().size()).isEqualTo(expectedSearchResults.size());
    for (int i = 0; i < result.getSearchResults().size(); i++) {
      ArtistSearchResponseEntryDto resultEntry = result.getSearchResults().get(i);
      ArtistSearchResponseEntryDto expectedResultEntry = expectedSearchResults.get(i);

      assertThat(resultEntry.getId()).isEqualTo(expectedResultEntry.getId());
      assertThat(resultEntry.getImageUrl()).isEqualTo(expectedResultEntry.getImageUrl());
      assertThat(resultEntry.getName()).isEqualTo(expectedResultEntry.getName());
      assertThat(resultEntry.getPopularity()).isEqualTo(expectedResultEntry.getPopularity());
      assertThat(resultEntry.getGenres()).isEqualTo(expectedResultEntry.getGenres());
      assertThat(resultEntry.getUri()).isEqualTo(expectedResultEntry.getUri());
      assertThat(resultEntry.getSource()).isEqualTo(expectedResultEntry.getSource());
      assertThat(resultEntry.isFollowed()).isEqualTo(expectedResultEntry.isFollowed());
    }
  }
}