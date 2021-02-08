package rocks.metaldetector.spotify.client.transformer;

import org.apache.commons.text.WordUtils;
import org.springframework.stereotype.Service;
import rocks.metaldetector.spotify.api.SpotifyArtist;
import rocks.metaldetector.spotify.api.SpotifyImage;
import rocks.metaldetector.spotify.api.search.SpotifyFollowers;
import rocks.metaldetector.spotify.facade.dto.SpotifyArtistDto;
import rocks.metaldetector.support.ImageSize;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SpotifyArtistTransformer {

  public SpotifyArtistDto transform(SpotifyArtist spotifyArtist) {
    return SpotifyArtistDto.builder()
            .id(spotifyArtist.getId())
            .name(spotifyArtist.getName())
            .uri(spotifyArtist.getUri())
            .uri(spotifyArtist.getHref())
            .genres(transformGenres(spotifyArtist.getGenres()))
            .popularity(spotifyArtist.getPopularity())
            .follower(getFollower(spotifyArtist.getFollowers()))
            .images(transformImages(spotifyArtist.getImages()))
            .build();
  }

  private Map<ImageSize, String> transformImages(List<SpotifyImage> spotifyImages) {
    Map<ImageSize, String> images = new HashMap<>();
    if (spotifyImages != null) {
      List<SpotifyImage> sortedSpotifyImages = new ArrayList<>(spotifyImages);
      sortedSpotifyImages.sort(Comparator.comparingInt(SpotifyImage::getHeight).reversed());

      sortedSpotifyImages.forEach(spotifyImage -> {
        ImageSize size = ImageSize.ofHeight(spotifyImage.getHeight());
        if (!images.containsKey(size)) {
          images.put(size, spotifyImage.getUrl());
        }
      });
    }

    return images;
  }

  private List<String> transformGenres(List<String> genres) {
    return genres == null ? Collections.emptyList() : genres.stream().map(WordUtils::capitalizeFully).collect(Collectors.toList());
  }

  private int getFollower(SpotifyFollowers followers) {
    return followers == null ? 0 : followers.getTotal();
  }
}
