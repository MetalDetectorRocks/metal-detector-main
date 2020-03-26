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
        "height",
        "resource_url",
        "type",
        "uri",
        "uri150",
        "width"
})
public class DiscogsImage {

  @JsonProperty("height")
  private int height;

  @JsonProperty("width")
  private int width;

  @JsonProperty("type")
  private String type;

  @JsonProperty("resource_url")
  private String resourceUrl;

  @JsonProperty("uri")
  private String uri;

  @JsonProperty("uri150")
  private String uri150;

}
