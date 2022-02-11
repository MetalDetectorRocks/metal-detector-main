package rocks.metaldetector.service.artist;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import rocks.metaldetector.support.ImageBySizeFetcher;
import rocks.metaldetector.support.ImageSize;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor
@Builder
public class ArtistDto implements ImageBySizeFetcher {

  private String externalId;
  private String artistName;
  private String source;
  private LocalDateTime followedSince;
  private int follower;

  @JsonIgnore
  private Map<ImageSize, String> images;

  public ArtistDto() {
    this.images = new HashMap<>();
  }
}
