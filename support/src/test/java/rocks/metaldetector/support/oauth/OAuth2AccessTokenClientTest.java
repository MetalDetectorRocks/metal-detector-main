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
import org.springframework.security.oauth2.core.OAuth2AccessToken;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static rocks.metaldetector.support.oauth.OAuth2AccessTokenClient.PRINCIPAL;

@ExtendWith(MockitoExtension.class)
class OAuth2AccessTokenClientTest implements WithAssertions {

  @Mock
  private OAuth2AuthorizedClientManager manager;

  @InjectMocks
  private OAuth2AccessTokenClient underTest;

  @BeforeEach
  void setup() {
    underTest.setRegistrationId("testRegistrationId");
  }

  @AfterEach
  void tearDown() {
    reset(manager);
  }

  @Test
  @DisplayName("manager is called with expected request")
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
  @DisplayName("accessToken value is returned")
  void test_access_token_value_returned() {
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
  @DisplayName("exception is thrown if token is not present")
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
