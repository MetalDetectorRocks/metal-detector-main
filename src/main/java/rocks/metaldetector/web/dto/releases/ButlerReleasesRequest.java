package rocks.metaldetector.web.dto.releases;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@JsonPropertyOrder({
    "dateFrom",
    "dateTo",
    "artists"
})
public class ButlerReleasesRequest {

  @JsonProperty("dateFrom")
  private LocalDate dateFrom;

  @JsonProperty("dateTo")
  private LocalDate dateTo;

  @JsonProperty("artists")
  private Iterable<String> artists;

}
