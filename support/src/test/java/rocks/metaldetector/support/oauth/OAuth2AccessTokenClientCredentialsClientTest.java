package rocks.metaldetector.support.oauth;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.core.OAuth2AccessToken;

import java.time.Instant;

import static java.time.temporal.ChronoUnit.MINUTES;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static rocks.metaldetector.support.oauth.OAuth2AccessTokenClientCredentialsClient.PRINCIPAL;

@ExtendWith(MockitoExtension.class)
class OAuth2AccessTokenClientCredentialsClientTest implements WithAssertions {

  private final String REGISTRATION_ID = "testRegistrationId";

  @Mock
  private OAuth2AuthorizedClientManager manager;

  @Mock
  private OAuth2AuthorizedClientService service;

  @InjectMocks
  private OAuth2AccessTokenClientCredentialsClient underTest;

  @BeforeEach
  void setup() {
    underTest.setRegistrationId(REGISTRATION_ID);
  }

  @AfterEach
  void tearDown() {
    reset(manager, service);
  }

  @Test
  @DisplayName("service is called to load authorized client")
  void test_service_is_called_to_load_client() {
    // given
    var clientMock = mock(OAuth2AuthorizedClient.class);
    var accessToken = mock(OAuth2AccessToken.class);
    doReturn(clientMock).when(manager).authorize(any());
    doReturn(accessToken).when(clientMock).getAccessToken();
    doReturn("tokenValue").when(accessToken).getTokenValue();

    // when
    underTest.getAccessToken();

    // then
    verify(service).loadAuthorizedClient(REGISTRATION_ID, PRINCIPAL.getName());
  }

  @Test
  @DisplayName("manager is not called if client with valid token is present")
  void test_valid_token_present() {
    // given
    var client = mock(OAuth2AuthorizedClient.class);
    var token = mock(OAuth2AccessToken.class);
    var expiresAt = Instant.now().plus(1, MINUTES);
    doReturn(client).when(service).loadAuthorizedClient(any(), any());
    doReturn(token).when(client).getAccessToken();
    doReturn(expiresAt).when(token).getExpiresAt();

    // when
    underTest.getAccessToken();

    // then
    verifyNoInteractions(manager);
  }

  @Test
  @DisplayName("token value is returned if present")
  void test_present_token_returned() {
    // given
    var client = mock(OAuth2AuthorizedClient.class);
    var token = mock(OAuth2AccessToken.class);
    var expiresAt = Instant.now().plus(1, MINUTES);
    var tokenValue = "tokenValue";
    doReturn(client).when(service).loadAuthorizedClient(any(), any());
    doReturn(token).when(client).getAccessToken();
    doReturn(expiresAt).when(token).getExpiresAt();
    doReturn(tokenValue).when(token).getTokenValue();

    // when
    var result = underTest.getAccessToken();

    // then
    assertThat(result).isEqualTo(tokenValue);
  }

  @Test
  @DisplayName("manager is called with expected request if authorization is required")
  void test_manager_called() {
    // given
    ArgumentCaptor<OAuth2AuthorizeRequest> argumentCaptor = ArgumentCaptor.forClass(OAuth2AuthorizeRequest.class);
    var clientMock = mock(OAuth2AuthorizedClient.class);
    var accessToken = mock(OAuth2AccessToken.class);
    var tokenValue = "tokenValue";
    doReturn(clientMock).when(manager).authorize(any());
    doReturn(accessToken).when(clientMock).getAccessToken();
    doReturn(tokenValue).when(accessToken).getTokenValue();

    // when
    underTest.getAccessToken();

    // then
    verify(manager).authorize(argumentCaptor.capture());
    var request = argumentCaptor.getValue();
    assertThat(request.getPrincipal()).isEqualTo(PRINCIPAL);
    assertThat(request.getClientRegistrationId()).isEqualTo("testRegistrationId");
  }

  @Test
  @DisplayName("new accessToken value is returned if no valid token exists")
  void test_new_access_token_value_returned() {
    // given
    var clientMock = mock(OAuth2AuthorizedClient.class);
    var accessToken = mock(OAuth2AccessToken.class);
    var tokenValue = "tokenValue";
    doReturn(clientMock).when(manager).authorize(any());
    doReturn(accessToken).when(clientMock).getAccessToken();
    doReturn(tokenValue).when(accessToken).getTokenValue();

    // when
    var result = underTest.getAccessToken();

    // then
    assertThat(result).isEqualTo(tokenValue);
  }

  @Test
  @DisplayName("exception is thrown if token is not present after calling manager")
  void test_exception_on_token_not_present() {
    // given
    doReturn(null).when(manager).authorize(any());

    // when
    var throwable = catchThrowable(() -> underTest.getAccessToken());

    // then
    assertThat(throwable).isInstanceOf(IllegalArgumentException.class);
    assertThat(throwable).hasMessageContaining("testRegistrationId");
  }
}
