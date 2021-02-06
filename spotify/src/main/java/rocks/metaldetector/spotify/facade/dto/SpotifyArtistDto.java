package rocks.metaldetector.spotify.facade.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import rocks.metaldetector.support.ImageSize;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class SpotifyArtistDto {

  private String id;
  private String name;
  private String uri;
  private String url;
  private List<String> genres;
  private int popularity;
  private int follower;
  private Map<ImageSize, String> images;

}
