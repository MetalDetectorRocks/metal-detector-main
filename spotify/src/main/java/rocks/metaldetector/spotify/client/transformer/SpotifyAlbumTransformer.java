package rocks.metaldetector.spotify.client.transformer;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import rocks.metaldetector.spotify.api.SpotifyArtist;
import rocks.metaldetector.spotify.api.SpotifyImage;
import rocks.metaldetector.spotify.api.imports.SpotifyAlbum;
import rocks.metaldetector.spotify.facade.dto.SpotifyAlbumDto;
import rocks.metaldetector.spotify.facade.dto.SpotifyArtistDto;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class SpotifyAlbumTransformer {

  private final SpotifyArtistTransformer artistTransformer;

  public SpotifyAlbumDto transform(SpotifyAlbum album) {
    return SpotifyAlbumDto.builder()
        .id(album.getId())
        .name(album.getName())
        .artists(transformArtists(album.getArtists()))
        .genres(album.getGenres())
        .imageUrl(getImageUrl(album.getImages()))
        .popularity(album.getPopularity())
        .uri(album.getUri())
        .build();
  }

  private List<SpotifyArtistDto> transformArtists(List<SpotifyArtist> artists) {
    return artists.stream()
        .map(artistTransformer::transform)
        .collect(Collectors.toList());
  }

  private String getImageUrl(List<SpotifyImage> images) {
    if (images != null && !images.isEmpty()) {
      return images.get(0).getUrl();
    }
    return "";
  }
}
