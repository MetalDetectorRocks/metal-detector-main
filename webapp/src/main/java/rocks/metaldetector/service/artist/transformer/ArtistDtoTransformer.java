package rocks.metaldetector.service.artist.transformer;

import org.springframework.stereotype.Component;
import rocks.metaldetector.persistence.domain.artist.ArtistEntity;
import rocks.metaldetector.persistence.domain.artist.FollowActionEntity;
import rocks.metaldetector.persistence.domain.artist.MultipleSizeImages;
import rocks.metaldetector.persistence.domain.artist.TopArtist;
import rocks.metaldetector.service.artist.ArtistDto;
import rocks.metaldetector.spotify.facade.dto.SpotifyArtistDto;
import rocks.metaldetector.support.ImageSize;

import java.util.HashMap;
import java.util.Map;

import static rocks.metaldetector.persistence.domain.artist.ArtistSource.SPOTIFY;
import static rocks.metaldetector.support.ImageSize.L;
import static rocks.metaldetector.support.ImageSize.M;
import static rocks.metaldetector.support.ImageSize.S;
import static rocks.metaldetector.support.ImageSize.XS;

@Component
public class ArtistDtoTransformer {

  public ArtistDto transformSpotifyArtistDto(SpotifyArtistDto spotifyArtistDto) {
    return ArtistDto.builder()
        .externalId(spotifyArtistDto.getId())
        .artistName(spotifyArtistDto.getName())
        .images(spotifyArtistDto.getImages())
        .source(SPOTIFY.getDisplayName())
        .follower(spotifyArtistDto.getFollower())
        .build();
  }

  public ArtistDto transformArtistEntity(ArtistEntity artistEntity) {
    return ArtistDto.builder()
            .externalId(artistEntity.getExternalId())
            .artistName(artistEntity.getArtistName())
            .images(transformImages(artistEntity))
            .source(artistEntity.getSource().getDisplayName())
            .build();
  }

  public ArtistDto transformTopArtist(TopArtist topArtist) {
    return ArtistDto.builder()
            .externalId(topArtist.getExternalId())
            .artistName(topArtist.getArtistName())
            .source(topArtist.getSource().getDisplayName())
            .follower(topArtist.getFollower())
            .images(transformImages(topArtist))
            .build();
  }

  private Map<ImageSize, String> transformImages(MultipleSizeImages artistImages) {
    Map<ImageSize, String> images = new HashMap<>();
    images.put(XS, artistImages.getImageXs());
    images.put(S, artistImages.getImageS());
    images.put(M, artistImages.getImageM());
    images.put(L, artistImages.getImageL());

    return images;
  }

  public ArtistDto transformFollowActionEntity(FollowActionEntity followAction) {
    ArtistDto artistDto = transformArtistEntity(followAction.getArtist());
    artistDto.setFollowedSince(followAction.getCreatedDateTime());
    return artistDto;
  }
}
