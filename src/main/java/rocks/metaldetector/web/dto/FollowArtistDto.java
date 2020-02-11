package rocks.metaldetector.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FollowArtistDto {

  private String publicUserId;
  private String artistName;
  private long artistDiscogsId;

}
