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

  @Mock
  private CurrentOAuthUserIdSupplier currentOAuthUserIdSupplier;

  private OAuth2AccessTokenAuthorizationCodeClient underTest;

  @BeforeEach
  private void setup() {
    underTest = new OAuth2AccessTokenAuthorizationCodeClient(oAuth2AuthorizedClientService, currentOAuthUserIdSupplier);
  }

  @AfterEach
  private void tearDown() {
    reset(oAuth2AuthorizedClientService, currentOAuthUserIdSupplier);
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

      // when
      String result = underTest.getAccessToken();

      assertThat(result).isEqualTo(token);
    }

    @ParameterizedTest
    @DisplayName("Exception is thrown for bad clients and tokens")
    @MethodSource("badClientInputProvider")
    void test_exception_thrown_for_bad_clients_or_tokens(OAuth2AuthorizedClient client, OAuth2AccessToken accessToken, String token) {
      // given
      var registrationId = "registrationId";
      underTest.setRegistrationId(registrationId);
      doReturn(client).when(oAuth2AuthorizedClientService).loadAuthorizedClient(any(), any());
      if (client != null) {
        doReturn(accessToken).when(client).getAccessToken();
        if (accessToken != null) {
          doReturn(token).when(accessToken).getTokenValue();
        }
      }

      // when
      var throwable = catchThrowable(() -> underTest.getAccessToken());

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
