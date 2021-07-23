package rocks.metaldetector.support.oauth;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.core.OAuth2AccessToken;

import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class OAuth2AccessTokenAuthorizationCodeClientTest implements WithAssertions {

  @Mock
  private OAuth2AuthorizedClientService oAuth2AuthorizedClientService;

  private OAuth2AccessTokenAuthorizationCodeClient underTest;

  @BeforeEach
  private void setup() {
    underTest = new OAuth2AccessTokenAuthorizationCodeClient(oAuth2AuthorizedClientService);
  }

  @AfterEach
  private void tearDown() {
    reset(oAuth2AuthorizedClientService);
  }

  @DisplayName("Tests for SecurityContext handling")
  @TestInstance(TestInstance.Lifecycle.PER_CLASS)
  @Nested
  class SecurityContextTest {

    @BeforeEach
    void setup() {
      var authorizedClientMock = mock(OAuth2AuthorizedClient.class);
      var accessTokenMock = mock(OAuth2AccessToken.class);
      doReturn(authorizedClientMock).when(oAuth2AuthorizedClientService).loadAuthorizedClient(any(), any());
      doReturn(accessTokenMock).when(authorizedClientMock).getAccessToken();
      doReturn("token").when(accessTokenMock).getTokenValue();
    }

    @Test
    @DisplayName("securityContextHolder is called for securityContext")
    void test_security_context_holder_called() {
      // given
      var securityContextMock = mock(SecurityContext.class);
      doReturn(mock(Authentication.class)).when(securityContextMock).getAuthentication();

      try (MockedStatic<SecurityContextHolder> securityContextHolderMock = Mockito.mockStatic(SecurityContextHolder.class)) {
        // given
        securityContextHolderMock.when(SecurityContextHolder::getContext).thenReturn(securityContextMock);

        // when
        underTest.getAccessToken();

        // then
        securityContextHolderMock.verify(SecurityContextHolder::getContext);
      }
    }

    @Test
    @DisplayName("securityContext is called for current authentication")
    void test_security_context_called() {
      // given
      var securityContextMock = mock(SecurityContext.class);
      doReturn(mock(Authentication.class)).when(securityContextMock).getAuthentication();

      try (MockedStatic<SecurityContextHolder> securityContextHolderMock = Mockito.mockStatic(SecurityContextHolder.class)) {
        // given
        securityContextHolderMock.when(SecurityContextHolder::getContext).thenReturn(securityContextMock);

        // when
        underTest.getAccessToken();
      }

      // then
      verify(securityContextMock).getAuthentication();
    }

    @Test
    @DisplayName("authentication is called for username")
    void test_authentication_called() {
      // given
      var securityContextMock = mock(SecurityContext.class);
      var authenticationMock = mock(Authentication.class);
      doReturn(authenticationMock).when(securityContextMock).getAuthentication();

      try (MockedStatic<SecurityContextHolder> securityContextHolderMock = Mockito.mockStatic(SecurityContextHolder.class)) {
        // given
        securityContextHolderMock.when(SecurityContextHolder::getContext).thenReturn(securityContextMock);

        // when
        underTest.getAccessToken();
      }

      // then
      verify(authenticationMock).getName();
    }

    @Test
    @DisplayName("oAuth2AuthorizedClientService is called with registrationId")
    void test_oauth_service_called_with_registration_id() {
      // given
      var securityContextMock = mock(SecurityContext.class);
      var authenticationMock = mock(Authentication.class);
      var registrationId = "registrationId";
      underTest.setRegistrationId(registrationId);
      doReturn(authenticationMock).when(securityContextMock).getAuthentication();

      try (MockedStatic<SecurityContextHolder> securityContextHolderMock = Mockito.mockStatic(SecurityContextHolder.class)) {
        // given
        securityContextHolderMock.when(SecurityContextHolder::getContext).thenReturn(securityContextMock);

        // when
        underTest.getAccessToken();
      }

      // then
      verify(oAuth2AuthorizedClientService).loadAuthorizedClient(eq(registrationId), any());
    }

    @Test
    @DisplayName("oAuth2AuthorizedClientService is called with username")
    void test_oauth_service_called_with_username() {
      // given
      var securityContextMock = mock(SecurityContext.class);
      var authenticationMock = mock(Authentication.class);
      var username = "username";
      doReturn(authenticationMock).when(securityContextMock).getAuthentication();
      doReturn(username).when(authenticationMock).getName();

      try (MockedStatic<SecurityContextHolder> securityContextHolderMock = Mockito.mockStatic(SecurityContextHolder.class)) {
        // given
        securityContextHolderMock.when(SecurityContextHolder::getContext).thenReturn(securityContextMock);

        // when
        underTest.getAccessToken();
      }

      // then
      verify(oAuth2AuthorizedClientService).loadAuthorizedClient(any(), eq(username));
    }
  }

  @DisplayName("Tests for clients and tokens")
  @TestInstance(TestInstance.Lifecycle.PER_CLASS)
  @Nested
  class OAuthClientServiceTest {

    @Test
    @DisplayName("token is returned if present")
    void test_token_returned() {
      // given
      var authorizedClientMock = mock(OAuth2AuthorizedClient.class);
      var accessTokenMock = mock(OAuth2AccessToken.class);
      var token = "token";
      var securityContextMock = mock(SecurityContext.class);
      String result;
      doReturn(authorizedClientMock).when(oAuth2AuthorizedClientService).loadAuthorizedClient(any(), any());
      doReturn(accessTokenMock).when(authorizedClientMock).getAccessToken();
      doReturn(token).when(accessTokenMock).getTokenValue();
      doReturn(mock(Authentication.class)).when(securityContextMock).getAuthentication();

      try (MockedStatic<SecurityContextHolder> securityContextHolderMock = Mockito.mockStatic(SecurityContextHolder.class)) {
        // given
        securityContextHolderMock.when(SecurityContextHolder::getContext).thenReturn(securityContextMock);

        // when
        result = underTest.getAccessToken();
      }

      assertThat(result).isEqualTo(token);
    }

    @ParameterizedTest
    @DisplayName("Exception is thrown for bad clients and tokens")
    @MethodSource("badClientInputProvider")
    void test_exception_thrown_for_bad_clients_or_tokens(OAuth2AuthorizedClient client, OAuth2AccessToken accessToken, String token) {
      // given
      var securityContextMock = mock(SecurityContext.class);
      var registrationId = "registrationId";
      underTest.setRegistrationId(registrationId);
      doReturn(mock(Authentication.class)).when(securityContextMock).getAuthentication();
      doReturn(client).when(oAuth2AuthorizedClientService).loadAuthorizedClient(any(), any());
      if (client != null) {
        doReturn(accessToken).when(client).getAccessToken();
        if (accessToken != null) {
          doReturn(token).when(accessToken).getTokenValue();
        }
      }
      Throwable throwable;

      try (MockedStatic<SecurityContextHolder> securityContextHolderMock = Mockito.mockStatic(SecurityContextHolder.class)) {
        // given
        securityContextHolderMock.when(SecurityContextHolder::getContext).thenReturn(securityContextMock);

        // when
        throwable = catchThrowable(() -> underTest.getAccessToken());
      }

      // then
      assertThat(throwable).isInstanceOf(IllegalArgumentException.class);
      assertThat(throwable).hasMessageContaining(registrationId);
    }

    private Stream<Arguments> badClientInputProvider() {
      return Stream.of(
          Arguments.of(null, null, null),
          Arguments.of(mock(OAuth2AuthorizedClient.class), null, null),
          Arguments.of(mock(OAuth2AuthorizedClient.class), mock(OAuth2AccessToken.class), null),
          Arguments.of(mock(OAuth2AuthorizedClient.class), mock(OAuth2AccessToken.class), "")
      );
    }
  }
}
