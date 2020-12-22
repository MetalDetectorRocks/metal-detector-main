package rocks.metaldetector.web.api.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArtistSearchResponseEntryDto {

  private String id;
  private String name;
  private String imageUrl;
  private String uri;
  private String source;
  private boolean followed;
  private List<String> genres;
  private int popularity;
  private int metalDetectorFollower;
  private int spotifyFollower;

}
