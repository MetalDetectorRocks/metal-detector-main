package rocks.metaldetector.discogs.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

@Data
@JsonPropertyOrder({
        "active",
        "id",
        "name",
        "resource_url"
})
public class DiscogsMember {

  @JsonProperty("id")
  private long id;

  @JsonProperty("name")
  private String name;

  @JsonProperty("active")
  private boolean active;

  @JsonProperty("resource_url")
  private String resourceUrl;

}
