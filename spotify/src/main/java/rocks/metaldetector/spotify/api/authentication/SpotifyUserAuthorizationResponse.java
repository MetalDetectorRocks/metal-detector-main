package rocks.metaldetector.spotify.api.authentication;

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
    "access_token",
    "token_type",
    "scope",
    "expires_in",
    "refresh_token"
})
public class SpotifyUserAuthorizationResponse {

  @JsonProperty("access_token")
  private String accessToken;

  @JsonProperty("token_type")
  private String tokenType;

  @JsonProperty("scope")
  private String scope;

  @JsonProperty("expires_in")
  private int expiresIn;

  @JsonProperty("refresh_token")
  private String refreshToken;
}
