package rocks.metaldetector.service.artist;

import org.springframework.stereotype.Component;
import rocks.metaldetector.spotify.facade.dto.SpotifyArtistDto;

import static rocks.metaldetector.persistence.domain.artist.ArtistSource.SPOTIFY;

@Component
public class ArtistDtoTransformer {

  public ArtistDto transformSpotifyArtistDto(SpotifyArtistDto spotifyArtistDto) {
    return ArtistDto.builder()
        .externalId(spotifyArtistDto.getId())
        .artistName(spotifyArtistDto.getName())
        .thumb(spotifyArtistDto.getImageUrl())
        .source(SPOTIFY.getDisplayName())
        .follower(spotifyArtistDto.getFollower())
        .build();
  }
}
