package rocks.metaldetector.web.transformer;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import rocks.metaldetector.service.artist.ArtistDto;
import rocks.metaldetector.service.artist.ArtistsService;

import java.util.List;

import static org.mockito.Mockito.doReturn;
import static rocks.metaldetector.testutil.DtoFactory.ReleaseDtoFactory;

@ExtendWith(MockitoExtension.class)
class DetectorReleasesResponseTransformerTest implements WithAssertions {

  @Mock
  private ArtistsService artistsService;

  @InjectMocks
  private DetectorReleasesResponseTransformer underTest;

  @Test
  @DisplayName("Should transform list of ReleaseDto to a list of DetectorReleasesResponse")
  void should_transform() {
    // given
    var releases = List.of(
            ReleaseDtoFactory.withArtistName("Metallica"),
            ReleaseDtoFactory.withArtistName("Slayer")
    );

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
  @DisplayName("Should mark all followed artists by the current user with artist service")
  void test() {
    // given
    var followedArtist = ArtistDto.builder().artistName("Metallica").build();
    var releases = List.of(
            ReleaseDtoFactory.withArtistName("Metallica"),
            ReleaseDtoFactory.withArtistName("Slayer")
    );
    doReturn(List.of(followedArtist)).when(artistsService).findFollowedArtistsForCurrentUser();

    // when
    var results = underTest.transformListOf(releases);

    // then
    assertThat(results.get(0).isFollowed()).isTrue();
    assertThat(results.get(1).isFollowed()).isFalse();
  }
}
