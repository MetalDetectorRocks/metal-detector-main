package rocks.metaldetector.support.infrastructure;

import com.github.valfirst.slf4jtest.TestLogger;
import com.github.valfirst.slf4jtest.TestLoggerFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.mock.http.client.MockClientHttpResponse;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.stream.Stream;

import static com.github.valfirst.slf4jtest.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.slf4j.event.Level.ERROR;
import static org.slf4j.event.Level.WARN;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE;

class CustomClientErrorHandlerTest {

  private final TestLogger logger = TestLoggerFactory.getTestLogger(CustomClientErrorHandler.class);

  private final CustomClientErrorHandler underTest = new CustomClientErrorHandler();

  @AfterEach
  void tearDown() {
    logger.clear();
  }

  @ParameterizedTest(name = "[{index}] => Response <{0}>")
  @MethodSource("inputProviderHasError")
  @DisplayName("Should return true for 4xx and 5xx errors")
  void has_error_should_return_true(ClientHttpResponse response) throws IOException {
    // when
    boolean hasError = underTest.hasError(response);

    // then
    assertTrue(hasError);
  }

  @ParameterizedTest(name = "[{index}] => Response <{0}>")
  @MethodSource("inputProviderNoError")
  @DisplayName("Should return false for 2xx and 3xx return codes")
  void has_error_should_return_false(ClientHttpResponse response) throws IOException {
    // when
    boolean hasError = underTest.hasError(response);

    // then
    assertFalse(hasError);
  }

  @Test
  @DisplayName("Should log 4xx errors as warnings")
  void handle_error_should_log_warn() throws IOException, URISyntaxException {
    // given
    var mockResponse = new MockClientHttpResponse(new byte[0], NOT_FOUND);
    var uri = new URI("https://www.metal-detector.rocks");
    var expectedLogMessage = "URL: " + uri + " | " +
                             "Method: " + GET.name() + " | " +
                             "Status code: " + mockResponse.getStatusCode().value() + " | " +
                             "Status text: " + mockResponse.getStatusText();

    // when
    underTest.handleError(uri, GET, new MockClientHttpResponse(new byte[0], NOT_FOUND));

    // then
    assertThat(logger).hasLogged(WARN, expectedLogMessage);
  }

  @Test
  @DisplayName("Should log 5xx errors as errors")
  void handle_error_should_log_error() throws IOException, URISyntaxException {
    // given
    var mockResponse = new MockClientHttpResponse(new byte[0], INTERNAL_SERVER_ERROR);
    var uri = new URI("https://www.metal-detector.rocks");
    var expectedLogMessage = "URL: " + uri + " | " +
                             "Method: " + GET.name() + " | " +
                             "Status code: " + mockResponse.getStatusCode().value() + " | " +
                             "Status text: " + mockResponse.getStatusText();

    // when
    underTest.handleError(uri, GET, new MockClientHttpResponse(new byte[0], INTERNAL_SERVER_ERROR));

    // then
    assertThat(logger).hasLogged(ERROR, expectedLogMessage);
  }

  private static Stream<Arguments> inputProviderHasError() {
    return Stream.of(
        Arguments.of(new MockClientHttpResponse(new byte[0], NOT_FOUND)),
        Arguments.of(new MockClientHttpResponse(new byte[0], BAD_REQUEST)),
        Arguments.of(new MockClientHttpResponse(new byte[0], INTERNAL_SERVER_ERROR)),
        Arguments.of(new MockClientHttpResponse(new byte[0], SERVICE_UNAVAILABLE))
    );
  }

  private static Stream<Arguments> inputProviderNoError() {
    return Stream.of(
        Arguments.of(new MockClientHttpResponse(new byte[0], HttpStatus.OK)),
        Arguments.of(new MockClientHttpResponse(new byte[0], HttpStatus.TEMPORARY_REDIRECT))
    );
  }
}
