package rocks.metaldetector.spotify.facade.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class SpotifyArtistDto {

  private String id;
  private String name;
  private String imageUrl;
  private String uri;
  private List<String> genres;
  private int popularity;
  private int follower;

}
