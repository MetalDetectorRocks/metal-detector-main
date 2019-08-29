package com.metalr2.security.handler;

import com.metalr2.config.constants.Endpoints;
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

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class CustomAccessDeniedHandlerTest {

  @ParameterizedTest
  @MethodSource("createRequestUris")
  void handleAccessDeniedForAuthenticatedUser(List<String> requestUris, HttpStatus httpStatus, String location, String errorMessage) throws Exception {
    Authentication authenticationMock = Mockito.mock(UsernamePasswordAuthenticationToken.class);
    AccessDeniedHandler accessDeniedHandler = new CustomAccessDeniedHandler(() -> authenticationMock);

    for (String requestUri : requestUris) {
      MockHttpServletRequest request   = new MockHttpServletRequest();
      MockHttpServletResponse response = new MockHttpServletResponse();
      request.setRequestURI(requestUri);

      accessDeniedHandler.handle(request, response, new AccessDeniedException("Access denied!"));

      assertEquals(httpStatus.value(), response.getStatus());
      assertEquals(location, response.getHeader(HttpHeaders.LOCATION));
      assertEquals(errorMessage, response.getErrorMessage());
    }
  }

  private static Stream<Arguments> createRequestUris() {
    return Stream.of(
            Arguments.of(Endpoints.Guest.ALL_GUEST_INDEX_PAGES, HttpStatus.TEMPORARY_REDIRECT, Endpoints.Frontend.HOME, null),
            Arguments.of(Endpoints.Guest.ALL_AUTH_PAGES, HttpStatus.TEMPORARY_REDIRECT, Endpoints.Frontend.STATUS, null),
            Arguments.of(List.of(Endpoints.AdminArea.USERS_LIST), HttpStatus.FORBIDDEN, null, "Not authorized")
            );
  }

}
