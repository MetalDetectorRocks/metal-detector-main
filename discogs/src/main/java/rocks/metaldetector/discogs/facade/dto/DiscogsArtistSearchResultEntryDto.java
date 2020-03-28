package rocks.metaldetector.discogs.facade.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@Builder
public class DiscogsArtistSearchResultEntryDto {

  private static final String DISCOGS_URL = "http://discogs.com";

  private long id;
  private String name;
  private String imageUrl;
  private String uri;
  private boolean isFollowed;

  public String getUri() {
    return uri != null ? DISCOGS_URL + uri : null;
  }
}
