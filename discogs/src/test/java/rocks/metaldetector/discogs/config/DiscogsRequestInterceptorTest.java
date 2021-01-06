package rocks.metaldetector.discogs.config;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;

import java.io.IOException;
import java.util.List;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpHeaders.USER_AGENT;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static rocks.metaldetector.discogs.config.DiscogsRequestInterceptor.TOKEN_PREFIX;

@ExtendWith(MockitoExtension.class)
class DiscogsRequestInterceptorTest implements WithAssertions {

  @Mock
  private DiscogsConfig discogsConfig;

  @Mock
  private HttpRequest httpRequestMock;

  @Mock
  private HttpHeaders httpHeadersMock;

  @Mock
  private ClientHttpRequestExecution clientHttpRequestExecutionMock;

  @InjectMocks
  private DiscogsRequestInterceptor underTest;

  @Test
  @DisplayName("should set accept header always to 'application/json'")
  void accept_header_is_set() throws IOException {
    // given
    doReturn(httpHeadersMock).when(httpRequestMock).getHeaders();

    // when
    underTest.intercept(httpRequestMock, new byte[]{}, clientHttpRequestExecutionMock);

    // then
    verify(httpHeadersMock).setAccept(List.of(APPLICATION_JSON));
  }

  @Test
  @DisplayName("should set user agent with value from DiscogsConfig")
  void user_agent_header_is_set() throws IOException {
    // given
    var userAgent = "User Agent";
    doReturn(httpHeadersMock).when(httpRequestMock).getHeaders();
    doReturn(userAgent).when(discogsConfig).getUserAgent();

    // when
    underTest.intercept(httpRequestMock, new byte[]{}, clientHttpRequestExecutionMock);

    // then
    verify(httpHeadersMock).set(USER_AGENT, userAgent);
  }

  @Test
  @DisplayName("should set authorization header with token from DiscogsConfig")
  void authorization_header_is_set() throws IOException {
    // given
    var token = "discogs token";
    doReturn(httpHeadersMock).when(httpRequestMock).getHeaders();
    doReturn(token).when(discogsConfig).getAccessToken();

    // when
    underTest.intercept(httpRequestMock, new byte[]{}, clientHttpRequestExecutionMock);

    // then
    verify(httpHeadersMock).set(AUTHORIZATION, TOKEN_PREFIX + token);
  }

  @Test
  @DisplayName("should call 'execute' on ClientHttpRequestExecution")
  void execute_is_called() throws IOException {
    // given
    var bodyAsByteArray = new byte[]{};
    doReturn(httpHeadersMock).when(httpRequestMock).getHeaders();

    // when
    underTest.intercept(httpRequestMock, bodyAsByteArray, clientHttpRequestExecutionMock);

    // then
    verify(clientHttpRequestExecutionMock).execute(httpRequestMock, bodyAsByteArray);
  }
}
