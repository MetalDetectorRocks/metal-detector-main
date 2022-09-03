package rocks.metaldetector.support;

import com.github.valfirst.slf4jtest.TestLogger;
import com.github.valfirst.slf4jtest.TestLoggerFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.mock.http.client.MockClientHttpRequest;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import static com.github.valfirst.slf4jtest.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static uk.org.lidalia.slf4jext.Level.INFO;

class DefaultRequestLoggingInterceptorTest {

  private final TestLogger logger = TestLoggerFactory.getTestLogger(DefaultRequestLoggingInterceptor.class);

  private final DefaultRequestLoggingInterceptor underTest = new DefaultRequestLoggingInterceptor();

  @AfterEach
  void tearDown() {
    logger.clear();
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
    mockRequest.setURI(new URI("https://www.metal-detector.rocks"));

    // when
    underTest.intercept(mockRequest, new byte[0], mock(ClientHttpRequestExecution.class));

    // then
    assertThat(logger).hasLogged(INFO, "URI: {}", mockRequest.getURI());
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
    assertThat(logger).hasLogged(INFO, "Headers: {}", mockRequest.getHeaders().toString());
  }
}
