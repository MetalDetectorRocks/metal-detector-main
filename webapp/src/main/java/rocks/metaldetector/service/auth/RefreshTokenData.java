package rocks.metaldetector.service.auth;

import org.springframework.http.ResponseCookie;
import rocks.metaldetector.web.api.auth.LoginResponse;

import java.util.List;

public record RefreshTokenData(
    String username,
    List<String> roles,
    String accessToken,
    ResponseCookie refreshToken
) {

  public LoginResponse asLoginResponse() {
    return LoginResponse.builder()
        .username(username)
        .roles(roles)
        .accessToken(accessToken)
        .build();
  }
}
