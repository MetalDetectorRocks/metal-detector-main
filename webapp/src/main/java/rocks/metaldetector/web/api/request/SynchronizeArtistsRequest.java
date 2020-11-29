package rocks.metaldetector.web.api.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import rocks.metaldetector.support.infrastructure.ArtifactForFramework;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

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
    //noinspection unchecked: Cast is safe here
    this.artistIds = (List<String>) data.get("artistIds");
  }
}
