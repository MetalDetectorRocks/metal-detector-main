package rocks.metaldetector.discogs.facade.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DiscogsArtistSearchResultEntryDto {

  private static final String DISCOGS_URL = "http://discogs.com";

  private long id;
  private String name;
  private String imageUrl;
  // ToDo DanielW: Unterschied zwischen uri und resourceUrl?
  private String uri;
  private String resourceUrl;
  private boolean isFollowed;

  public String getUri() {
    return DISCOGS_URL + uri;
  }

}
