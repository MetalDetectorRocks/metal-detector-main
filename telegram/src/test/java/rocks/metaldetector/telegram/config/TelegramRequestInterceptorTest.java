package rocks.metaldetector.telegram.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;

import java.io.IOException;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class TelegramRequestInterceptorTest {

  private final TelegramRequestInterceptor underTest = new TelegramRequestInterceptor();

  @Test
  @DisplayName("should call 'execute' on ClientHttpRequestExecution")
  void execute_is_called() throws IOException {
    // given
    var httpRequestMock = mock(HttpRequest.class);
    var httpHeadersMock = mock(HttpHeaders.class);
    var clientHttpRequestExecutionMock = mock(ClientHttpRequestExecution.class);
    var bodyAsByteArray = new byte[]{};
    doReturn(httpHeadersMock).when(httpRequestMock).getHeaders();

    // when
    underTest.intercept(httpRequestMock, bodyAsByteArray, clientHttpRequestExecutionMock);

    // then
    verify(clientHttpRequestExecutionMock).execute(httpRequestMock, bodyAsByteArray);
  }
}
