package rocks.metaldetector.support;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.mock.http.client.MockClientHttpRequest;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@ExtendWith(MockitoExtension.class)
class DefaultRequestLoggingInterceptorTest {

  @Mock
  private Logger logger;

  @InjectMocks
  private DefaultRequestLoggingInterceptor underTest;

  @AfterEach
  void tearDown() {
    reset(logger);
  }

  @Test
  @DisplayName("should call 'execute' on ClientHttpRequestExecution")
  void execute_is_called() throws IOException {
    // given
    var httpRequestMock = mock(HttpRequest.class);
    var httpHeadersMock = mock(HttpHeaders.class);
    var clientHttpRequestExecutionMock = mock(ClientHttpRequestExecution.class);
    var bodyAsByteArray = new byte[] {};
    doReturn(httpHeadersMock).when(httpRequestMock).getHeaders();

    // when
    underTest.intercept(httpRequestMock, bodyAsByteArray, clientHttpRequestExecutionMock);

    // then
    verify(clientHttpRequestExecutionMock).execute(httpRequestMock, bodyAsByteArray);
  }

  @Test
  @DisplayName("uri is logged")
  void test_uri_logging() throws IOException, URISyntaxException {
    // given
    var mockRequest = new MockClientHttpRequest();
    mockRequest.setURI(new URI("http://www.internet.com"));

    // when
    underTest.intercept(mockRequest, new byte[0], mock(ClientHttpRequestExecution.class));

    // then
    verify(logger).info("URI: {}", mockRequest.getURI());
  }

  @Test
  @DisplayName("headers are logged")
  void test_headers_logging() throws IOException {
    // given
    var mockRequest = new MockClientHttpRequest();
    mockRequest.getHeaders().setContentType(APPLICATION_JSON);

    // when
    underTest.intercept(mockRequest, new byte[0], mock(ClientHttpRequestExecution.class));

    // then
    verify(logger).info("Headers: {}", mockRequest.getHeaders().toString());
  }
}
