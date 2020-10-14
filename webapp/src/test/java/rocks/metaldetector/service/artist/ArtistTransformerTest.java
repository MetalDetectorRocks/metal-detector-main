package rocks.metaldetector.service.artist;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import rocks.metaldetector.persistence.domain.artist.ArtistEntity;
import rocks.metaldetector.persistence.domain.artist.FollowActionEntity;
import rocks.metaldetector.persistence.domain.artist.TopArtist;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

class ArtistTransformerTest implements WithAssertions {

  private final ArtistTransformer underTest = new ArtistTransformer();

  @Test
  @DisplayName("Should transform ArtistEntity to ArtistDto")
  void should_transform_entity_to_dto() {
    // given
    ArtistEntity artistEntity = ArtistEntityFactory.withExternalId("1");

    // when
    ArtistDto result = underTest.transform(artistEntity);

    // then
    assertThat(result.getExternalId()).isEqualTo(artistEntity.getExternalId());
    assertThat(result.getArtistName()).isEqualTo(artistEntity.getArtistName());
    assertThat(result.getThumb()).isEqualTo(artistEntity.getThumb());
    assertThat(result.getSource()).isEqualTo(artistEntity.getSource().getDisplayName());
  }

  @Test
  @DisplayName("Should transform TopArtist to ArtistDto")
  void should_transform_top_artist_to_dto() {
    // given
    TopArtist topArtist = mock(TopArtist.class);
    doReturn("artist").when(topArtist).getArtistName();
    doReturn("thumb").when(topArtist).getThumb();
    doReturn("externalId").when(topArtist).getExternalId();

    // when
    ArtistDto result = underTest.transform(topArtist);

    // then
    assertThat(result.getArtistName()).isEqualTo(topArtist.getArtistName());
    assertThat(result.getThumb()).isEqualTo(topArtist.getThumb());
    assertThat(result.getExternalId()).isEqualTo(topArtist.getExternalId());
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
    ArtistDto result = underTest.transform(followAction);

    // then
    assertThat(result.getExternalId()).isEqualTo(artistEntity.getExternalId());
    assertThat(result.getArtistName()).isEqualTo(artistEntity.getArtistName());
    assertThat(result.getThumb()).isEqualTo(artistEntity.getThumb());
    assertThat(result.getSource()).isEqualTo(artistEntity.getSource().getDisplayName());
    assertThat(result.getFollowedSince()).isEqualTo(LocalDate.of(2020, 1, 1));
  }
}
