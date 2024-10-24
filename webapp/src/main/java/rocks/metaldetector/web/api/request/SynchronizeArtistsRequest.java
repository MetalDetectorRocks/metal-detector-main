package rocks.metaldetector.web.api.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import rocks.metaldetector.support.infrastructure.ArtifactForFramework;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SynchronizeArtistsRequest {

  @NotNull
  private List<String> artistIds;

  @JsonProperty("data")
  @ArtifactForFramework
  private void unpackNested(Map<String, Object> data) {
    this.artistIds = ((List<?>) data.get("artistIds")).stream()
        .map(String::valueOf)
        .collect(Collectors.toList());
  }
}
