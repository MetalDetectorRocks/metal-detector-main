package rocks.metaldetector.web.api.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SpotifyUserAuthorizationExistsResponse {

  private boolean exists;

  public boolean exists() {
    return exists;
  }
}
