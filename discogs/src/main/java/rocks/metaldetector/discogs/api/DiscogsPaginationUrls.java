package rocks.metaldetector.discogs.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

@Data
@JsonPropertyOrder({
        "last",
        "next"
})
public class DiscogsPaginationUrls {

  @JsonProperty("last")
  private String last;

  @JsonProperty("next")
  private String next;

  DiscogsPaginationUrls() {
    this.last = "";
    this.next = "";
  }

}
