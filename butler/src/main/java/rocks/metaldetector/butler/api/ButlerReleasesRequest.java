package rocks.metaldetector.butler.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonPropertyOrder({
    "page",
    "size",
    "dateFrom",
    "dateTo",
    "query",
    "artists"
})
public class ButlerReleasesRequest {

  @JsonProperty("page")
  private int page;

  @JsonProperty("size")
  private int size;

  @JsonProperty("dateFrom")
  private LocalDate dateFrom;

  @JsonProperty("dateTo")
  private LocalDate dateTo;

  @JsonProperty("query")
  private String query;

  @JsonProperty("artists")
  private Iterable<String> artists;

}
