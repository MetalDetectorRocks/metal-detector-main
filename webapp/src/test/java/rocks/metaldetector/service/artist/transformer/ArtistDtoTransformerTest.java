package rocks.metaldetector.service.artist.transformer;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import rocks.metaldetector.persistence.domain.artist.ArtistEntity;
import rocks.metaldetector.persistence.domain.artist.FollowActionEntity;
import rocks.metaldetector.persistence.domain.artist.TopArtist;
import rocks.metaldetector.service.artist.ArtistDto;
import rocks.metaldetector.service.artist.ArtistEntityFactory;
import rocks.metaldetector.testutil.DtoFactory;

import java.time.LocalDateTime;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static rocks.metaldetector.persistence.domain.artist.ArtistSource.SPOTIFY;
import static rocks.metaldetector.support.ImageSize.L;
import static rocks.metaldetector.support.ImageSize.M;
import static rocks.metaldetector.support.ImageSize.S;
import static rocks.metaldetector.support.ImageSize.XS;

class ArtistDtoTransformerTest implements WithAssertions {

  private final ArtistDtoTransformer underTest = new ArtistDtoTransformer();

  @Test
  @DisplayName("SpotifyArtistDto is transformed to ArtistDto")
  void test_spotify_dto_is_transformed() {
    // given
    var spotifyArtistDto = DtoFactory.SpotifyArtistDtoFactory.withArtistName("artist");

    // when
    var result = underTest.transformSpotifyArtistDto(spotifyArtistDto);

    // then
    assertThat(result.getExternalId()).isEqualTo(spotifyArtistDto.getId());
    assertThat(result.getArtistName()).isEqualTo(spotifyArtistDto.getName());
    assertThat(result.getFollower()).isEqualTo(spotifyArtistDto.getFollower());
    assertThat(result.getSource()).isEqualTo(SPOTIFY.getDisplayName());
    assertThat(result.getImages()).isEqualTo(spotifyArtistDto.getImages());
    assertThat(result.getFollowedSince()).isNull();
  }

  @Test
  @DisplayName("Should transform ArtistEntity to ArtistDto")
  void should_transform_entity_to_dto() {
    // given
    ArtistEntity artistEntity = ArtistEntityFactory.withExternalId("1");

    // when
    ArtistDto result = underTest.transformArtistEntity(artistEntity);

    // then
    assertThat(result.getExternalId()).isEqualTo(artistEntity.getExternalId());
    assertThat(result.getArtistName()).isEqualTo(artistEntity.getArtistName());
    assertThat(result.getImages().get(XS)).isEqualTo(artistEntity.getImageXs());
    assertThat(result.getImages().get(S)).isEqualTo(artistEntity.getImageS());
    assertThat(result.getImages().get(M)).isEqualTo(artistEntity.getImageM());
    assertThat(result.getImages().get(L)).isEqualTo(artistEntity.getImageL());
    assertThat(result.getSource()).isEqualTo(artistEntity.getSource().getDisplayName());
  }

  @Test
  @DisplayName("Should transform TopArtist to ArtistDto")
  void should_transform_top_artist_to_dto() {
    // given
    TopArtist topArtist = mock(TopArtist.class);
    doReturn("artist").when(topArtist).getArtistName();
    doReturn(SPOTIFY).when(topArtist).getSource();
    doReturn("http://example.com/image-xs.jpg").when(topArtist).getImageXs();
    doReturn("http://example.com/image-s.jpg").when(topArtist).getImageS();
    doReturn("http://example.com/image-m.jpg").when(topArtist).getImageM();
    doReturn("http://example.com/image-l.jpg").when(topArtist).getImageL();
    doReturn("externalId").when(topArtist).getExternalId();

    // when
    ArtistDto result = underTest.transformTopArtist(topArtist);

    // then
    assertThat(result.getArtistName()).isEqualTo(topArtist.getArtistName());
    assertThat(result.getImages().get(XS)).isEqualTo(topArtist.getImageXs());
    assertThat(result.getImages().get(S)).isEqualTo(topArtist.getImageS());
    assertThat(result.getImages().get(M)).isEqualTo(topArtist.getImageM());
    assertThat(result.getImages().get(L)).isEqualTo(topArtist.getImageL());
    assertThat(result.getExternalId()).isEqualTo(topArtist.getExternalId());
    assertThat(result.getSource()).isEqualTo(topArtist.getSource().getDisplayName());
  }

  @Test
  @DisplayName("Should transform FollowActionEntity to ArtistDto")
  void should_transform_follow_action_entity_to_artist_dto() {
    // given
    FollowActionEntity followAction = mock(FollowActionEntity.class);
    ArtistEntity artistEntity = ArtistEntityFactory.withExternalId("1");
    doReturn(artistEntity).when(followAction).getArtist();
    doReturn(LocalDateTime.of(2020, 1, 1, 0, 0, 0)).when(followAction).getCreatedDateTime();

    // when
    ArtistDto result = underTest.transformFollowActionEntity(followAction);

    // then
    assertThat(result.getExternalId()).isEqualTo(artistEntity.getExternalId());
    assertThat(result.getArtistName()).isEqualTo(artistEntity.getArtistName());
    assertThat(result.getSource()).isEqualTo(artistEntity.getSource().getDisplayName());
    assertThat(result.getFollowedSince()).isEqualTo(LocalDateTime.of(2020, 1, 1, 0, 0, 0));
  }
}
