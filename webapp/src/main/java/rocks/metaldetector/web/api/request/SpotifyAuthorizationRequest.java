package rocks.metaldetector.web.api.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SpotifyAuthorizationRequest {

  @NotEmpty
  private String state;

  @NotEmpty
  private String code;

  @JsonProperty("data")
  private void unpackNested(Map<String, String> data) {
    this.state = data.get("state");
    this.code = data.get("code");
  }
}
