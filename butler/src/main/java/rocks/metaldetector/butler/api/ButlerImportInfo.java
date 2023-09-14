package rocks.metaldetector.butler.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@JsonPropertyOrder({
    "source",
    "successRate",
    "lastImport",
    "lastSuccessfulImport"
})
@NoArgsConstructor
public class ButlerImportInfo {

  @JsonProperty("source")
  private String source;

  @JsonProperty("successRate")
  private int successRate;

  @JsonProperty("lastImport")
  private LocalDateTime lastImport;

  @JsonProperty("lastSuccessfulImport")
  private LocalDateTime lastSuccessfulImport;
}
