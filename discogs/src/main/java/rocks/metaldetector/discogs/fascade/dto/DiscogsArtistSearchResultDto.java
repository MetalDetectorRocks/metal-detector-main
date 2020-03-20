package rocks.metaldetector.discogs.fascade.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DiscogsArtistSearchResultDto {

  private static final String DISCOGS_URL = "http://discogs.com";

  private long id;
  private String title;
  private String thumb;
  private String uri;
  private String resourceUrl;

  public String getUri() {
    return DISCOGS_URL + uri;
  }

}
