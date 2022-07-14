package rocks.metaldetector.web.transformer;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import rocks.metaldetector.support.SlicingService;
import rocks.metaldetector.testutil.DtoFactory.ArtistDtoFactory;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MyArtistsResponseTransformerTest implements WithAssertions {

  @Mock
  private SlicingService slicingService;

  @InjectMocks
  private MyArtistsResponseTransformer underTest;

  @AfterEach
  void tearDown() {
    reset(slicingService);
  }

  @Test
  @DisplayName("SlicingService is called with artist list")
  void test_slicing_service_called_with_artists() {
    // given
    var artists = List.of(ArtistDtoFactory.createDefault());

    //when
    underTest.transform(artists, 1, 1);

    // then
    verify(slicingService).slice(eq(artists), anyInt(), anyInt());
  }

  @Test
  @DisplayName("SlicingService is called with given page")
  void test_slicing_service_called_with_given_page() {
    // given
    var artists = List.of(ArtistDtoFactory.createDefault());
    var page = 1;

    //when
    underTest.transform(artists, page, 1);

    // then
    verify(slicingService).slice(anyList(), eq(page), anyInt());
  }

  @Test
  @DisplayName("SlicingService is called with given size")
  void test_slicing_service_called_with_given_size() {
    // given
    var size = 1;

    //when
    underTest.transform(Collections.emptyList(), 1, size);

    // then
    verify(slicingService).slice(anyList(), anyInt(), eq(size));
  }

  @Test
  @DisplayName("SlicingService is called with last page, if requested page is greater than the number of pages")
  void test_slicing_service_called_with_last_page() {
    // given
    var artist = ArtistDtoFactory.createDefault();
    var artists = List.of(artist, artist);
    var page = 3;

    //when
    underTest.transform(artists, page, 1);

    // then
    verify(slicingService).slice(anyList(), eq(2), anyInt());
  }

  @Test
  @DisplayName("Sliced list is returned")
  void test_sliced_list_returned() {
    // given
    var artists = List.of(ArtistDtoFactory.withName("a"), ArtistDtoFactory.withName("b"));
    var slicedArtists = List.of(ArtistDtoFactory.withName("a"));
    doReturn(slicedArtists).when(slicingService).slice(anyList(), anyInt(), anyInt());

    //when
    var result = underTest.transform(artists, 1, 1);

    // then
    assertThat(result.getMyArtists()).isEqualTo(slicedArtists);
  }

  @Test
  @DisplayName("Total pages in pagination are calculated correctly")
  void test_pagination_total_pages() {
    // given
    var artists = List.of(ArtistDtoFactory.withName("a"), ArtistDtoFactory.withName("b"), ArtistDtoFactory.withName("c"));

    //when
    var result = underTest.transform(artists, 1, 1);

    // then
    assertThat(result.getPagination().getTotalPages()).isEqualTo(3);
  }

  @Test
  @DisplayName("Page in pagination is returned")
  void test_pagination_page() {
    // given
    var artists = List.of(ArtistDtoFactory.withName("a"));
    var page = 1;

    //when
    var result = underTest.transform(artists, page, 1);

    // then
    assertThat(result.getPagination().getCurrentPage()).isEqualTo(page);
  }

  @Test
  @DisplayName("Size in pagination is returned")
  void test_pagination_size() {
    // given
    var artists = List.of(ArtistDtoFactory.withName("a"));
    var size = 1;

    //when
    var result = underTest.transform(artists, 1, size);

    // then
    assertThat(result.getPagination().getItemsPerPage()).isEqualTo(size);
  }

  @Test
  @DisplayName("Last page is returned as page in pagination, if requested page is greater than the number of pages")
  void test_pagination_last_page() {
    // given
    var artists = List.of(ArtistDtoFactory.withName("a"));
    var page = 10;

    //when
    var result = underTest.transform(artists, page, 1);

    // then
    assertThat(result.getPagination().getCurrentPage()).isEqualTo(1);
  }
}
