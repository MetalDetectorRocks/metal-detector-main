package rocks.metaldetector.security.handler;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.util.List;
import java.util.stream.Stream;

import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.TEMPORARY_REDIRECT;
import static rocks.metaldetector.support.Endpoints.AdminArea.USERS;
import static rocks.metaldetector.support.Endpoints.Authentication.ALL_AUTH_PAGES;
import static rocks.metaldetector.support.Endpoints.Frontend.STATUS;

@ExtendWith(MockitoExtension.class)
class CustomAccessDeniedHandlerTest implements WithAssertions {

  @ParameterizedTest
  @MethodSource("createRequestUris")
  @DisplayName("Test several access denied scenarios")
  void handle_access_denied_for_authenticated_user(List<String> requestUris, HttpStatus httpStatus, String location, String errorMessage) throws Exception {
    Authentication authenticationMock = Mockito.mock(UsernamePasswordAuthenticationToken.class);
    AccessDeniedHandler accessDeniedHandler = new CustomAccessDeniedHandler(() -> authenticationMock);

    for (String requestUri : requestUris) {
      MockHttpServletRequest request   = new MockHttpServletRequest();
      MockHttpServletResponse response = new MockHttpServletResponse();
      request.setRequestURI(requestUri);

      accessDeniedHandler.handle(request, response, new AccessDeniedException("Access denied!"));

      assertThat(response.getStatus()).isEqualTo(httpStatus.value());
      assertThat(response.getHeader(HttpHeaders.LOCATION)).isEqualTo(location);
      assertThat(response.getErrorMessage()).isEqualTo(errorMessage);
    }
  }

  private static Stream<Arguments> createRequestUris() {
    return Stream.of(
            Arguments.of(ALL_AUTH_PAGES, TEMPORARY_REDIRECT, STATUS, null),
            Arguments.of(List.of(USERS), FORBIDDEN, null, "Not authorized")
    );
  }
}
