package rocks.metaldetector.butler.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ButlerImportCreatedResponse {

  @JsonProperty("importJobIds")
  private List<String> importJobIds;

}
