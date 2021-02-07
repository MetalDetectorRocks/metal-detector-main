package rocks.metaldetector.service.artist.transformer;

import org.springframework.stereotype.Component;
import rocks.metaldetector.persistence.domain.artist.ArtistEntity;
import rocks.metaldetector.persistence.domain.artist.FollowActionEntity;
import rocks.metaldetector.persistence.domain.artist.TopArtist;
import rocks.metaldetector.service.artist.ArtistDto;
import rocks.metaldetector.spotify.facade.dto.SpotifyArtistDto;

import static rocks.metaldetector.persistence.domain.artist.ArtistSource.SPOTIFY;
import static rocks.metaldetector.support.ImageSize.M;

@Component
public class ArtistDtoTransformer {

  public ArtistDto transformSpotifyArtistDto(SpotifyArtistDto spotifyArtistDto) {
    return ArtistDto.builder()
        .externalId(spotifyArtistDto.getId())
        .artistName(spotifyArtistDto.getName())
        .thumb(spotifyArtistDto.getImages().get(M)) // ToDo DanielW: set image properly
        .source(SPOTIFY.getDisplayName())
        .follower(spotifyArtistDto.getFollower())
        .build();
  }

  public ArtistDto transformArtistEntity(ArtistEntity artistEntity) {
    return ArtistDto.builder()
            .externalId(artistEntity.getExternalId())
            .artistName(artistEntity.getArtistName())
            .thumb(artistEntity.getThumb())
            .source(artistEntity.getSource().getDisplayName())
            .build();
  }

  public ArtistDto transformTopArtist(TopArtist topArtist) {
    return ArtistDto.builder()
            .externalId(topArtist.getExternalId())
            .artistName(topArtist.getArtistName())
            .thumb(topArtist.getThumb())
            .build();
  }

  public ArtistDto transformFollowActionEntity(FollowActionEntity followAction) {
    ArtistDto artistDto = transformArtistEntity(followAction.getArtist());
    artistDto.setFollowedSince(followAction.getCreatedDateTime());
    return artistDto;
  }
}
