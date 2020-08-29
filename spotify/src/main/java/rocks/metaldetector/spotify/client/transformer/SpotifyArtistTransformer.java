package rocks.metaldetector.spotify.client.transformer;

import org.springframework.stereotype.Service;
import rocks.metaldetector.spotify.api.search.SpotifyArtist;
import rocks.metaldetector.spotify.api.search.SpotifyImage;
import rocks.metaldetector.spotify.facade.dto.SpotifyArtistDto;

import java.util.List;

@Service
public class SpotifyArtistTransformer {

  public SpotifyArtistDto transform(SpotifyArtist spotifyArtist) {
    return SpotifyArtistDto.builder()
            .id(spotifyArtist.getId())
            .name(spotifyArtist.getName())
            .imageUrl(getImageUrl(spotifyArtist.getImages()))
            .uri(spotifyArtist.getUri())
            .genres(spotifyArtist.getGenres())
            .popularity(spotifyArtist.getPopularity())
            .follower(spotifyArtist.getFollowers().getTotal())
            .build();
  }

  private String getImageUrl(List<SpotifyImage> images) {
    if (images != null && !images.isEmpty()) {
      return images.get(0).getUrl();
    }

    return "";
  }
}
