package rocks.metaldetector.spotify.facade.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class SpotifyAlbumDto {

  private String id;
  private String name;
  private List<SpotifyArtistDto> artists;
  private String imageUrl;
  private String uri;
  private List<String> genres;
  private int popularity;
}
