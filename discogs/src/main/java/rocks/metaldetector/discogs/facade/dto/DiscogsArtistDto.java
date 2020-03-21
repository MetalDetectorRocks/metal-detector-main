package rocks.metaldetector.discogs.facade.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DiscogsArtistDto {

  private long id;
  private String name;
  private String imageUrl;

}
