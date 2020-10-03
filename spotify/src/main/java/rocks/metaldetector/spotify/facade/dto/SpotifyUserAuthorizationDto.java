package rocks.metaldetector.spotify.facade.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class SpotifyUserAuthorizationDto {

  private String accessToken;
  private String refreshToken;
  private String tokenType;
  private String scope;
  private int expiresIn;
}
