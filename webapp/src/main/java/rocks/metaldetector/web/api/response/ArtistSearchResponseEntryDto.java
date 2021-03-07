package rocks.metaldetector.web.api.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import rocks.metaldetector.support.ImageBySizeFetcher;
import rocks.metaldetector.support.ImageSize;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArtistSearchResponseEntryDto implements ImageBySizeFetcher {

  private String id;
  private String name;
  private String uri;
  private String source;
  private boolean followed;
  private List<String> genres;
  private int popularity;
  private int metalDetectorFollower;
  private int spotifyFollower;

  @JsonIgnore
  private Map<ImageSize, String> images;

}
