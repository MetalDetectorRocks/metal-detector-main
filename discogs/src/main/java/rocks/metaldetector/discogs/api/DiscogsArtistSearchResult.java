package rocks.metaldetector.discogs.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

@Data
@JsonPropertyOrder({
        "thumb",
        "title",
        "uri",
        "resource_url",
        "id"
})
public class DiscogsArtistSearchResult {

  private static final String DISCOGS_URI = "http://discogs.com";

  @JsonProperty("id")
  private long id;

  @JsonProperty("title")
  private String title;

  @JsonProperty("thumb")
  private String thumb;

  @JsonProperty("uri")
  private String uri;

  @JsonProperty("resource_url")
  private String resourceUrl;

  public String getUri() {
    return DISCOGS_URI + uri;
  }
}
