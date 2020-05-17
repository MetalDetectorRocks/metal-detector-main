package rocks.metaldetector.web.transformer;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import rocks.metaldetector.service.artist.FollowArtistService;
import rocks.metaldetector.testutil.DtoFactory;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static rocks.metaldetector.testutil.DtoFactory.ReleaseDtoFactory;

@ExtendWith(MockitoExtension.class)
class DetectorReleasesResponseTransformerTest implements WithAssertions {

  @Mock
  private FollowArtistService followArtistService;

  @InjectMocks
  private DetectorReleasesResponseTransformer underTest;

  @AfterEach
  void tearDown() {
    reset(followArtistService);
  }

  @Test
  @DisplayName("Should transform list of ReleaseDto to a list of DetectorReleasesResponse")
  void should_transform() {
    // given
    String artistName1 = "Metallica";
    String artistName2 = "Slayer";
    var releases = List.of(
            ReleaseDtoFactory.withArtistName(artistName1),
            ReleaseDtoFactory.withArtistName(artistName2)
    );
    when(followArtistService.getFollowedArtistsOfCurrentUser()).thenReturn(Collections.emptyList());

    // when
    var results = underTest.transformListOf(releases);

    // then
    assertThat(results.size()).isEqualTo(releases.size());

    for (int index = 0; index < releases.size(); index++) {
      var release = releases.get(index);
      var resultItem = results.get(index);
      assertThat(resultItem.getArtist()).isEqualTo(release.getArtist());
      assertThat(resultItem.getAlbumTitle()).isEqualTo(release.getAlbumTitle());
      assertThat(resultItem.getReleaseDate()).isEqualTo(release.getReleaseDate());
      assertThat(resultItem.getAdditionalArtists()).isEqualTo(release.getAdditionalArtists());
      assertThat(resultItem.getGenre()).isEqualTo(release.getGenre());
      assertThat(resultItem.getType()).isEqualTo(release.getType());
      assertThat(resultItem.getMetalArchivesArtistUrl()).isEqualTo(release.getMetalArchivesArtistUrl());
      assertThat(resultItem.getMetalArchivesAlbumUrl()).isEqualTo(release.getMetalArchivesAlbumUrl());
      assertThat(resultItem.getSource()).isEqualTo(release.getSource());
      assertThat(resultItem.getState()).isEqualTo(release.getState());
      assertThat(resultItem.isFollowed()).isFalse();
    }
  }

  @Test
  @DisplayName("Should mark all followed artists by the current user")
  void should_mark_as_followed() {
    // given
    var releases = List.of(
            ReleaseDtoFactory.withArtistName("1"),
            ReleaseDtoFactory.withArtistName("2")
    );
    when(followArtistService.getFollowedArtistsOfCurrentUser()).thenReturn(List.of(DtoFactory.ArtistDtoFactory.withName("1")));

    // when
    var results = underTest.transformListOf(releases);

    // then
    assertThat(results.get(0).isFollowed()).isTrue();
    assertThat(results.get(1).isFollowed()).isFalse();
  }

  @Test
  @DisplayName("Should call followArtistService to get followed artists")
  void should_call_service() {
    // given
    when(followArtistService.getFollowedArtistsOfCurrentUser()).thenReturn(Collections.emptyList());

    // when
    underTest.transformListOf(Collections.emptyList());

    // then
    verify(followArtistService, times(1)).getFollowedArtistsOfCurrentUser();
  }
}
