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
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.OAuth2AccessToken;

import java.time.Instant;
import java.util.stream.Stream;

import static java.time.temporal.ChronoUnit.MINUTES;
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

  @Mock
  private OAuth2AuthorizedClientManager oAuth2AuthorizedClientManager;

  @Mock
  private CurrentOAuthUserIdSupplier currentOAuthUserIdSupplier;

  private OAuth2AccessTokenAuthorizationCodeClient underTest;

  @BeforeEach
  private void setup() {
    underTest = new OAuth2AccessTokenAuthorizationCodeClient(oAuth2AuthorizedClientService, currentOAuthUserIdSupplier, oAuth2AuthorizedClientManager);
  }

  @AfterEach
  private void tearDown() {
    reset(oAuth2AuthorizedClientService, currentOAuthUserIdSupplier, oAuth2AuthorizedClientManager);
  }

  @DisplayName("Tests for service handling")
  @TestInstance(TestInstance.Lifecycle.PER_CLASS)
  @Nested
  class ServicesTest {

    @BeforeEach
    void setup() {
      var authorizedClientMock = mock(OAuth2AuthorizedClient.class);
      var accessTokenMock = mock(OAuth2AccessToken.class);
      doReturn(authorizedClientMock).when(oAuth2AuthorizedClientService).loadAuthorizedClient(any(), any());
      doReturn(accessTokenMock).when(authorizedClientMock).getAccessToken();
      doReturn("token").when(accessTokenMock).getTokenValue();
      doReturn(Instant.now().plus(1, MINUTES)).when(accessTokenMock).getExpiresAt();
    }

    @Test
    @DisplayName("currentOAuthUserIdSupplier is called")
    void test_current_oauth_id_supplier_called() {
      // when
      underTest.getAccessToken();

      // then
      verify(currentOAuthUserIdSupplier).get();
    }

    @Test
    @DisplayName("oAuth2AuthorizedClientService is called with registrationId")
    void test_oauth_service_called_with_registration_id() {
      // given
      var registrationId = "registrationId";
      underTest.setRegistrationId(registrationId);

      // when
      underTest.getAccessToken();

      // then
      verify(oAuth2AuthorizedClientService).loadAuthorizedClient(eq(registrationId), any());
    }

    @Test
    @DisplayName("oAuth2AuthorizedClientService is called with userId")
    void test_oauth_service_called_with_user_id() {
      // given
      var userId = "userId";
      doReturn(userId).when(currentOAuthUserIdSupplier).get();

      // when
      underTest.getAccessToken();

      // then
      verify(oAuth2AuthorizedClientService).loadAuthorizedClient(any(), eq(userId));
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
      doReturn(authorizedClientMock).when(oAuth2AuthorizedClientService).loadAuthorizedClient(any(), any());
      doReturn(accessTokenMock).when(authorizedClientMock).getAccessToken();
      doReturn(token).when(accessTokenMock).getTokenValue();
      doReturn(Instant.now().plus(1, MINUTES)).when(accessTokenMock).getExpiresAt();

      // when
      String result = underTest.getAccessToken();

      assertThat(result).isEqualTo(token);
    }

    @ParameterizedTest
    @DisplayName("Exception is thrown for bad clients and tokens")
    @MethodSource("badClientInputProvider")
    void test_exception_thrown_for_bad_clients_or_tokens(OAuth2AuthorizedClient client, Instant instant) {
      // given
      var registrationId = "registrationId";
      underTest.setRegistrationId(registrationId);
      doReturn(client).when(oAuth2AuthorizedClientService).loadAuthorizedClient(any(), any());
      if (client != null) {
        var mockToken = mock(OAuth2AccessToken.class);
        doReturn(mockToken).when(client).getAccessToken();
        doReturn(instant).when(mockToken).getExpiresAt();
      }

      // when
      var throwable = catchThrowable(() -> underTest.getAccessToken());

      // then
      assertThat(throwable).isInstanceOf(IllegalArgumentException.class);
      assertThat(throwable).hasMessageContaining(registrationId);
    }

    private Stream<Arguments> badClientInputProvider() {
      return Stream.of(
          Arguments.of(null, null),
          Arguments.of(mock(OAuth2AuthorizedClient.class), null)
      );
    }
  }

  @DisplayName("Tests for refreshing the authorization")
  @TestInstance(TestInstance.Lifecycle.PER_CLASS)
  @Nested
  class RefreshTest {

    private final OAuth2AuthorizedClient AUTHORIZED_CLIENT_MOCK = mock(OAuth2AuthorizedClient.class);

    @BeforeEach
    void setup() {
      var accessTokenMock = mock(OAuth2AccessToken.class);
      doReturn(AUTHORIZED_CLIENT_MOCK).when(oAuth2AuthorizedClientService).loadAuthorizedClient(any(), any());
      doReturn(accessTokenMock).when(AUTHORIZED_CLIENT_MOCK).getAccessToken();
      doReturn(Instant.now().minus(1, MINUTES)).when(accessTokenMock).getExpiresAt();
      doReturn(mock(ClientRegistration.class)).when(AUTHORIZED_CLIENT_MOCK).getClientRegistration();
    }

    @Test
    @DisplayName("securityContextHolder is called for securityContext")
    void test_security_context_holder_called() {
      // given
      var securityContextMock = mock(SecurityContext.class);
      doReturn(mock(Authentication.class)).when(securityContextMock).getAuthentication();
      var reauthorizedClientMock = mock(OAuth2AuthorizedClient.class);
      var reauthorizedAccessTokenMock = mock(OAuth2AccessToken.class);
      doReturn(reauthorizedClientMock).when(oAuth2AuthorizedClientManager).authorize(any());
      doReturn(reauthorizedAccessTokenMock).when(reauthorizedClientMock).getAccessToken();

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
      var reauthorizedClientMock = mock(OAuth2AuthorizedClient.class);
      var reauthorizedAccessTokenMock = mock(OAuth2AccessToken.class);
      doReturn(reauthorizedClientMock).when(oAuth2AuthorizedClientManager).authorize(any());
      doReturn(reauthorizedAccessTokenMock).when(reauthorizedClientMock).getAccessToken();

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
    @DisplayName("authorizedClientManager is called to reauthenticate client")
    void test_manager_called() {
      // given
      ArgumentCaptor<OAuth2AuthorizeRequest> argumentCaptor = ArgumentCaptor.forClass(OAuth2AuthorizeRequest.class);
      var securityContextMock = mock(SecurityContext.class);
      var authenticationMock = mock(Authentication.class);
      doReturn(authenticationMock).when(securityContextMock).getAuthentication();
      var reauthorizedClientMock = mock(OAuth2AuthorizedClient.class);
      var reauthorizedAccessTokenMock = mock(OAuth2AccessToken.class);
      doReturn(reauthorizedClientMock).when(oAuth2AuthorizedClientManager).authorize(any());
      doReturn(reauthorizedAccessTokenMock).when(reauthorizedClientMock).getAccessToken();

      try (MockedStatic<SecurityContextHolder> securityContextHolderMock = Mockito.mockStatic(SecurityContextHolder.class)) {
        // given
        securityContextHolderMock.when(SecurityContextHolder::getContext).thenReturn(securityContextMock);

        // when
        underTest.getAccessToken();
      }

      // then
      verify(oAuth2AuthorizedClientManager).authorize(argumentCaptor.capture());
      OAuth2AuthorizeRequest capturedRequest = argumentCaptor.getValue();

      assertThat(capturedRequest.getAuthorizedClient()).isEqualTo(AUTHORIZED_CLIENT_MOCK);
      assertThat(capturedRequest.getPrincipal()).isEqualTo(authenticationMock);
    }

    @Test
    @DisplayName("refreshed token is returned if present")
    void test_refreshed_token_returned() {
      // given
      var securityContextMock = mock(SecurityContext.class);
      var expectedToken = "reauthorizedToken";
      String result;
      doReturn(mock(Authentication.class)).when(securityContextMock).getAuthentication();
      var reauthorizedClientMock = mock(OAuth2AuthorizedClient.class);
      var reauthorizedAccessTokenMock = mock(OAuth2AccessToken.class);
      doReturn(reauthorizedClientMock).when(oAuth2AuthorizedClientManager).authorize(any());
      doReturn(reauthorizedAccessTokenMock).when(reauthorizedClientMock).getAccessToken();
      doReturn(expectedToken).when(reauthorizedAccessTokenMock).getTokenValue();

      try (MockedStatic<SecurityContextHolder> securityContextHolderMock = Mockito.mockStatic(SecurityContextHolder.class)) {
        // given
        securityContextHolderMock.when(SecurityContextHolder::getContext).thenReturn(securityContextMock);

        // when
        result = underTest.getAccessToken();
      }

      // then
      assertThat(result).isEqualTo(expectedToken);
    }

    @Test
    @DisplayName("Exception is thrown for bad client")
    void test_exception_thrown_for_bad_clients_or_tokens() {
      // given
      var registrationId = "registrationId";
      underTest.setRegistrationId(registrationId);
      doReturn(null).when(oAuth2AuthorizedClientManager).authorize(any());
      var securityContextMock = mock(SecurityContext.class);
      doReturn(mock(Authentication.class)).when(securityContextMock).getAuthentication();
      Throwable throwable;

      try (MockedStatic<SecurityContextHolder> securityContextHolderMock = Mockito.mockStatic(SecurityContextHolder.class)) {
        // given
        securityContextHolderMock.when(SecurityContextHolder::getContext).thenReturn(securityContextMock);

        // when
        throwable = catchThrowable(() ->underTest.getAccessToken());
      }

      // then
      assertThat(throwable).isInstanceOf(IllegalArgumentException.class);
      assertThat(throwable).hasMessageContaining(registrationId);
    }
  }
}
