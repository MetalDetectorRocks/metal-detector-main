package rocks.metaldetector.service.artist;

import org.springframework.stereotype.Component;
import rocks.metaldetector.persistence.domain.artist.ArtistEntity;
import rocks.metaldetector.persistence.domain.artist.FollowActionEntity;
import rocks.metaldetector.persistence.domain.artist.TopArtist;

@Component
public class ArtistTransformer {

  public ArtistDto transform(ArtistEntity artistEntity) {
    return ArtistDto.builder()
        .externalId(artistEntity.getExternalId())
        .artistName(artistEntity.getArtistName())
        .thumb(artistEntity.getThumb())
        .source(artistEntity.getSource().getDisplayName())
        .build();
  }

  public ArtistDto transform(TopArtist topArtist) {
    return ArtistDto.builder()
        .artistName(topArtist.getArtistName())
        .thumb(topArtist.getThumb())
        .build();
  }

  public ArtistDto transform(FollowActionEntity followAction) {
    ArtistDto artistDto = transform(followAction.getArtist());
    artistDto.setFollowedSince(followAction.getCreatedDateTime().toLocalDate());
    return artistDto;
  }
}
