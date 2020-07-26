package rocks.metaldetector.spotify.facade.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class SpotifyArtistSearchResultEntryDto {

  private String id;
  private String name;
  private String imageUrl;
  private String uri;
  private List<String> genres;
  private int popularity;

}
