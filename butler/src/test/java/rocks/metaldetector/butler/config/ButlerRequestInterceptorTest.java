package rocks.metaldetector.butler.config;

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

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ButlerRequestInterceptorTest implements WithAssertions {

  @Mock
  private ButlerConfig butlerConfig;

  @Mock
  private HttpRequest httpRequestMock;

  @Mock
  private HttpHeaders httpHeadersMock;

  @Mock
  private ClientHttpRequestExecution clientHttpRequestExecutionMock;

  @InjectMocks
  private ButlerRequestInterceptor underTest;

  @Test
  @DisplayName("should set authorization header with token from ButlerConfig")
  void authorization_header_is_set() throws IOException {
    // given
    var token = "butler token";
    doReturn(httpHeadersMock).when(httpRequestMock).getHeaders();
    doReturn(token).when(butlerConfig).getAccessToken();

    // when
    underTest.intercept(httpRequestMock, new byte[]{}, clientHttpRequestExecutionMock);

    // then
    verify(httpHeadersMock).setBearerAuth(token);
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
