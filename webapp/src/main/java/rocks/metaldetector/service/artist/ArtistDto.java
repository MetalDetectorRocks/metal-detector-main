package rocks.metaldetector.service.artist;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import rocks.metaldetector.support.ImageBySizeFetcher;
import rocks.metaldetector.support.ImageSize;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@NoArgsConstructor
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

}
