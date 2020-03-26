package rocks.metaldetector.discogs.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonPropertyOrder({
        "thumb",
        "title",
        "uri",
        "resource_url",
        "id"
})
public class DiscogsArtistSearchResult {

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

}
