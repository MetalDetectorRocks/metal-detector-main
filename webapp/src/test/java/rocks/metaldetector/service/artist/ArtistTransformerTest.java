package rocks.metaldetector.service.artist;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import rocks.metaldetector.persistence.domain.artist.ArtistEntity;
import rocks.metaldetector.persistence.domain.artist.TopArtist;

import static org.mockito.Mockito.doReturn;

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
    TopArtist topArtist = Mockito.mock(TopArtist.class);
    doReturn("artist").when(topArtist).getArtistName();
    doReturn("thumb").when(topArtist).getThumb();

    // when
    ArtistDto result = underTest.transform(topArtist);

    // then
    assertThat(result.getArtistName()).isEqualTo(topArtist.getArtistName());
    assertThat(result.getThumb()).isEqualTo(topArtist.getThumb());
  }
}
