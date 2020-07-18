package rocks.metaldetector.service.artist;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArtistDto {

  private String externalId;
  private String artistName;
  private String thumb;
  private String source;

}
