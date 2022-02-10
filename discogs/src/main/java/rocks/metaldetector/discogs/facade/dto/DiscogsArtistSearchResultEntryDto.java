package rocks.metaldetector.discogs.facade.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class DiscogsArtistSearchResultEntryDto {

  private static final String DISCOGS_URL = "https://discogs.com";

  private String id;
  private String name;
  private String imageUrl;
  private String uri;

  public String getUri() {
    return uri != null ? DISCOGS_URL + uri : null;
  }
}
