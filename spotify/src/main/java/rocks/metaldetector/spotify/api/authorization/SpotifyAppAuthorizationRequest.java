package rocks.metaldetector.spotify.api.authorization;

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
    "grant_type"
})
public class SpotifyAppAuthorizationRequest {

  @JsonProperty("grant_type")
  private String grantType;

}
