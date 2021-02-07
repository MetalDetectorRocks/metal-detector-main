package rocks.metaldetector.service.artist;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import rocks.metaldetector.support.ImageSize;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArtistDto {

  private String externalId;
  private String artistName;
  private Map<ImageSize, String> images;
  private String source;
  private LocalDateTime followedSince;
  private int follower;

}
