package rocks.metaldetector.discogs.facade.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import rocks.metaldetector.support.ImageSize;

import java.util.Map;

@Data
@AllArgsConstructor
@Builder
public class DiscogsArtistDto {

  private String id;
  private String url;
  private String uri;
  private String name;
  private Map<ImageSize, String> images;

}
