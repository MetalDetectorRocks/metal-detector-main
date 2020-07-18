package rocks.metaldetector.service.artist;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import rocks.metaldetector.persistence.domain.artist.ArtistEntity;

class ArtistTransformerTest implements WithAssertions {

  private ArtistTransformer underTest = new ArtistTransformer();

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
    assertThat(result.getSource()).isEqualTo(artistEntity.getSource().toDisplayString());
  }
}
