package rocks.metaldetector.service.artist;

import org.springframework.stereotype.Component;
import rocks.metaldetector.persistence.domain.artist.ArtistEntity;

@Component
public class ArtistDtoTransformer {

  public ArtistDto transform(ArtistEntity artistEntity) {
    return ArtistDto.builder()
        .discogsId(artistEntity.getArtistDiscogsId())
        .artistName(artistEntity.getArtistName())
        .thumb(artistEntity.getThumb())
        .build();
  }
}
