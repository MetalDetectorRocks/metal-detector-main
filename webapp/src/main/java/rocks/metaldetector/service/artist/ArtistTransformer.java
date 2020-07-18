package rocks.metaldetector.service.artist;

import org.springframework.stereotype.Component;
import rocks.metaldetector.persistence.domain.artist.ArtistEntity;

@Component
public class ArtistTransformer {

  public ArtistDto transform(ArtistEntity artistEntity) {
    return ArtistDto.builder()
        .externalId(artistEntity.getExternalId())
        .artistName(artistEntity.getArtistName())
        .thumb(artistEntity.getThumb())
        .source(artistEntity.getSource().toDisplayString())
        .build();
  }
}
