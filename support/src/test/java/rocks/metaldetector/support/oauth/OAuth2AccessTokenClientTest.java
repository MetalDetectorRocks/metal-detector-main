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
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2AccessToken;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.springframework.security.core.authority.AuthorityUtils.createAuthorityList;
import static org.springframework.security.oauth2.core.AuthorizationGrantType.CLIENT_CREDENTIALS;

@ExtendWith(MockitoExtension.class)
class OAuth2AccessTokenClientTest implements WithAssertions {

  private static final String REGISTRATION_ID = "testRegistrationId";
  private static final AuthorizationGrantType GRANT_TYPE = CLIENT_CREDENTIALS;
  private static final AnonymousAuthenticationToken PRINCIPAL = new AnonymousAuthenticationToken("key", "anonymous", createAuthorityList("ROLE_ANONYMOUS"));
  private static final OAuth2AuthorizedClient AUTHORIZED_CLIENT = mock(OAuth2AuthorizedClient.class);
  private static final OAuth2AccessToken ACCESS_TOKEN = mock(OAuth2AccessToken.class);
  private static final String ACCESS_TOKEN_VALUE = "tokenValue";
  private static final Instant VALID_EXPIRATION_DATE = LocalDateTime.now().plusDays(1).toInstant(ZoneOffset.ofHoursMinutes(1, 0));
  private static final Instant EXPIRED_EXPIRATION_DATE = LocalDateTime.now().minusDays(1).toInstant(ZoneOffset.ofHoursMinutes(1, 0));

  @Mock
  private OAuth2AuthorizedClientManager authorizedClientManager;

  @Mock
  private OAuth2AuthorizedClientService authorizedClientService;

  @Mock
  private OAuth2AuthorizeRequestProvider authorizeRequestProvider;

  @Mock
  private OAuth2AuthenticationProvider authenticationProvider;

  private OAuth2AccessTokenClient underTest;

  @BeforeEach
  void setup() {
    underTest = new OAuth2AccessTokenClient(authorizedClientManager, authorizedClientService, authorizeRequestProvider, authenticationProvider);
    underTest.setRegistrationId(REGISTRATION_ID);
    underTest.setAuthorizationGrantType(GRANT_TYPE);

    doReturn(PRINCIPAL).when(authenticationProvider).provideForGrant(any());
  }

  @AfterEach
  void tearDown() {
    reset(authorizedClientManager, authorizedClientService, authorizeRequestProvider, authenticationProvider);
  }

  @Nested
  @TestInstance(TestInstance.Lifecycle.PER_CLASS)
  @DisplayName("(Re-)authorization not required tests")
  class AuthPresentTests {

    @BeforeEach
    void setup() {
      doReturn(AUTHORIZED_CLIENT).when(authorizedClientService).loadAuthorizedClient(any(), any());
      doReturn(ACCESS_TOKEN).when(AUTHORIZED_CLIENT).getAccessToken();
      doReturn(VALID_EXPIRATION_DATE).when(ACCESS_TOKEN).getExpiresAt();
      doReturn(ACCESS_TOKEN_VALUE).when(ACCESS_TOKEN).getTokenValue();
    }

    @Test
    @DisplayName("authenticationProvider is called to get current authentication")
    void test_authentication_provider_called() {
      // when
      underTest.getAccessToken();

      // then
      verify(authenticationProvider).provideForGrant(GRANT_TYPE);
    }

    @Test
    @DisplayName("authorizedClientService is called to get client")
    void test_authorized_client_service_called() {
      // when
      underTest.getAccessToken();

      // then
      verify(authorizedClientService).loadAuthorizedClient(REGISTRATION_ID, PRINCIPAL.getName());
    }

    @Test
    @DisplayName("if (re-)authentication is not required, no more services are called")
    void test_no_more_services_called() {
      // when
      underTest.getAccessToken();

      // then
      verify(authorizedClientService).loadAuthorizedClient(any(), any());
      verifyNoMoreInteractions(authorizedClientService);
      verifyNoInteractions(authorizeRequestProvider);
      verifyNoInteractions(authorizedClientManager);
    }

    @Test
    @DisplayName("if (re-)authentication is not required, the present token value is returned")
    void test_present_token_returned() {
      // when
      var result = underTest.getAccessToken();

      // then
      assertThat(result).isEqualTo(ACCESS_TOKEN_VALUE);
    }
  }

  @Nested
  @TestInstance(TestInstance.Lifecycle.PER_CLASS)
  @DisplayName("(Re-)authorization required tests")
  class AuthNotPresentTests {

    @BeforeEach
    void setup() {
      doReturn(AUTHORIZED_CLIENT).when(authorizedClientManager).authorize(any());
      doReturn(ACCESS_TOKEN).when(AUTHORIZED_CLIENT).getAccessToken();
      doReturn(ACCESS_TOKEN_VALUE).when(ACCESS_TOKEN).getTokenValue();
    }

    @Test
    @DisplayName("authorizeRequestProvider is called to build request")
    void test_authorize_request_provider_called() {
      // given
      doReturn(AUTHORIZED_CLIENT).when(authorizedClientService).loadAuthorizedClient(any(), any());

      // when
      underTest.getAccessToken();

      // then
      verify(authorizeRequestProvider).provideForGrant(GRANT_TYPE, AUTHORIZED_CLIENT, REGISTRATION_ID);
    }

    @Test
    @DisplayName("authorizedClientManager is called to (re-)authorize the client")
    void test_authorized_client_manager_called() {
      // given
      var request = mock(OAuth2AuthorizeRequest.class);
      doReturn(request).when(authorizeRequestProvider).provideForGrant(any(), any(), any());

      // when
      underTest.getAccessToken();

      // then
      verify(authorizedClientManager).authorize(request);
    }

    @Test
    @DisplayName("authorizedClientService is called to save new client")
    void test_authorized_client_service_called() {
      // when
      underTest.getAccessToken();

      // then
      verify(authorizedClientService).saveAuthorizedClient(AUTHORIZED_CLIENT, PRINCIPAL);
    }

    @Test
    @DisplayName("new access token's value is returned")
    void test_new_token_value_returned() {
      // when
      var result = underTest.getAccessToken();

      // then
      assertThat(result).isEqualTo(ACCESS_TOKEN_VALUE);
    }

    @ParameterizedTest
    @DisplayName("authorization is done for all client states")
    @MethodSource(value = "authorizedClientProvider")
    void test_authorization_for_clients(OAuth2AuthorizedClient authorizedClient) {
      // given
      doReturn(authorizedClient).when(authorizedClientService).loadAuthorizedClient(any(), any());

      // when
      underTest.getAccessToken();

      // then
      verify(authorizeRequestProvider).provideForGrant(any(), any(), any());
    }

    @ParameterizedTest
    @DisplayName("exception is thrown if new client is invalid")
    @MethodSource(value = "invalidClientProvider")
    void test_exception_on_invalid_client(OAuth2AuthorizedClient authorizedClient) {
      // given
      doReturn(authorizedClient).when(authorizedClientManager).authorize(any());

      // when
      var throwable = catchThrowable(() -> underTest.getAccessToken());

      // then
      assertThat(throwable).isInstanceOf(IllegalStateException.class);
      assertThat(throwable).hasMessageContaining(PRINCIPAL.getName());
      assertThat(throwable).hasMessageContaining(REGISTRATION_ID);
    }

    private Stream<Arguments> authorizedClientProvider() {
      var accessTokenNullClient = mock(OAuth2AuthorizedClient.class);
      doReturn(null).when(accessTokenNullClient).getAccessToken();

      var expiresAtNullClient = mock(OAuth2AuthorizedClient.class);
      var expiresAtNullAccessToken = mock(OAuth2AccessToken.class);
      doReturn(expiresAtNullAccessToken).when(expiresAtNullClient).getAccessToken();
      doReturn(null).when(expiresAtNullAccessToken).getExpiresAt();

      var accessTokenExpiredClient = mock(OAuth2AuthorizedClient.class);
      var expiredAccessToken = mock(OAuth2AccessToken.class);
      doReturn(expiredAccessToken).when(accessTokenExpiredClient).getAccessToken();
      doReturn(EXPIRED_EXPIRATION_DATE).when(expiredAccessToken).getExpiresAt();

      return Stream.of(
          Arguments.of((Object) null),
          Arguments.of(accessTokenNullClient),
          Arguments.of(expiresAtNullClient),
          Arguments.of(accessTokenExpiredClient)
      );
    }

    private Stream<Arguments> invalidClientProvider() {
      var accessTokenNullClient = mock(OAuth2AuthorizedClient.class);
      doReturn(null).when(accessTokenNullClient).getAccessToken();

      var tokenValueNullClient = mock(OAuth2AuthorizedClient.class);
      var tokenValueNullAccessToken = mock(OAuth2AccessToken.class);
      doReturn(tokenValueNullAccessToken).when(tokenValueNullClient).getAccessToken();
      doReturn(null).when(tokenValueNullAccessToken).getTokenValue();

      var tokenValueBlankClient = mock(OAuth2AuthorizedClient.class);
      var tokenValueBlankToken = mock(OAuth2AccessToken.class);
      doReturn(tokenValueBlankToken).when(tokenValueBlankClient).getAccessToken();
      doReturn("   ").when(tokenValueBlankToken).getTokenValue();

      return Stream.of(
          Arguments.of((Object) null),
          Arguments.of(accessTokenNullClient),
          Arguments.of(tokenValueNullClient),
          Arguments.of(tokenValueBlankClient)
      );
    }
  }
}
