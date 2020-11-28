package rocks.metaldetector.spotify.client.transformer;

import org.apache.commons.text.WordUtils;
import org.springframework.stereotype.Service;
import rocks.metaldetector.spotify.api.SpotifyArtist;
import rocks.metaldetector.spotify.api.SpotifyImage;
import rocks.metaldetector.spotify.api.search.SpotifyFollowers;
import rocks.metaldetector.spotify.facade.dto.SpotifyArtistDto;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SpotifyArtistTransformer {

  public SpotifyArtistDto transform(SpotifyArtist spotifyArtist) {
    return SpotifyArtistDto.builder()
            .id(spotifyArtist.getId())
            .name(spotifyArtist.getName())
            .imageUrl(getImageUrl(spotifyArtist.getImages()))
            .uri(spotifyArtist.getUri())
            .genres(transformGenres(spotifyArtist.getGenres()))
            .popularity(spotifyArtist.getPopularity())
            .follower(getFollower(spotifyArtist.getFollowers()))
            .build();
  }

  private String getImageUrl(List<SpotifyImage> images) {
    if (images != null && !images.isEmpty()) {
      return images.get(0).getUrl();
    }
    return "";
  }

  private List<String> transformGenres(List<String> genres) {
    return genres == null ? Collections.emptyList() : genres.stream().map(WordUtils::capitalizeFully).collect(Collectors.toList());
  }

  private int getFollower(SpotifyFollowers followers) {
    if (followers != null) {
      return followers.getTotal();
    }
    return 0;
  }
}
