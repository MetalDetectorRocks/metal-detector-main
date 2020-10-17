package rocks.metaldetector.service.artist;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArtistDto {

  private String externalId;
  private String artistName;
  private String thumb;
  private String source;
  private LocalDate followedSince;
  private int follower;

}
