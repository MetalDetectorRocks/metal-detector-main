package rocks.metaldetector.service.artist;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArtistDto {

  private String externalId;
  private String artistName;
  private String thumb;
  private String source;
  private LocalDateTime followedSince;
  private int follower;

}
