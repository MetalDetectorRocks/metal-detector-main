package rocks.metaldetector.service.auth;

import org.springframework.http.ResponseCookie;

public record TokenPair(
    String accessToken,
    ResponseCookie refreshToken
) {
}
