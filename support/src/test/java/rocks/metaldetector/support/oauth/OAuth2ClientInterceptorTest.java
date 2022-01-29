package rocks.metaldetector.support.oauth;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.mock.http.client.MockClientHttpRequest;

import java.io.IOException;
import java.util.List;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@ExtendWith(MockitoExtension.class)
class OAuth2ClientInterceptorTest implements WithAssertions {

  @Mock
  private OAuth2AccessTokenClient accessTokenClient;

  @InjectMocks
  private OAuth2ClientInterceptor underTest;

  @AfterEach
  void tearDown() {
    reset(accessTokenClient);
  }

  @Test
  @DisplayName("accessTokenClient is called")
  void test_token_supplier_called() throws IOException {
    // when
    underTest.intercept(new MockClientHttpRequest(), "body".getBytes(), mock(ClientHttpRequestExecution.class));

    // then
    verify(accessTokenClient).getAccessToken();
  }

  @Test
  @DisplayName("bearer Auth is set as request header")
  void test_auth_header_set() throws IOException {
    // given
    var requestMock = new MockClientHttpRequest();
    var token = "token";
    doReturn(token).when(accessTokenClient).getAccessToken();

    // when
    underTest.intercept(requestMock, "body".getBytes(), mock(ClientHttpRequestExecution.class));

    // then
    assertThat(requestMock.getHeaders()).containsEntry(AUTHORIZATION, List.of("Bearer " + token));
  }

  @Test
  @DisplayName("clientRequestExecution is called")
  void test_request_execution_called() throws IOException {
    // given
    var clientExecutionMock = mock(ClientHttpRequestExecution.class);
    var requestMock = new MockClientHttpRequest();
    var bodyMock = "body".getBytes();

    // when
    underTest.intercept(requestMock, bodyMock, clientExecutionMock);

    // then
    verify(clientExecutionMock).execute(requestMock, bodyMock);
  }
}
