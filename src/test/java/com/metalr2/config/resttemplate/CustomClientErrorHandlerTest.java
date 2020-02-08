package com.metalr2.config.resttemplate;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.mock.http.client.MockClientHttpResponse;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.stream.Stream;

@ExtendWith(MockitoExtension.class)
class CustomClientErrorHandlerTest implements WithAssertions {

  private Logger logger = (Logger) LoggerFactory.getLogger(CustomClientErrorHandler.class);
  private ListAppender<ILoggingEvent> listAppender;
  private CustomClientErrorHandler errorHandler = new CustomClientErrorHandler();

  @BeforeEach
  void setUp() {
    listAppender = new ListAppender<>();
    listAppender.start();
    logger.addAppender(listAppender);
  }

  @AfterEach
  void tearDown() {
    logger.detachAppender(listAppender);
  }

  @ParameterizedTest(name = "[{index}] => Response <{0}>")
  @MethodSource("inputProviderHasError")
  @DisplayName("Should return true for 4xx and 5xx errors")
  void has_error_should_return_true(ClientHttpResponse response) throws IOException {
    // when
    boolean hasError = errorHandler.hasError(response);

    // then
    assertThat(hasError).isTrue();
  }

  @ParameterizedTest(name = "[{index}] => Response <{0}>")
  @MethodSource("inputProviderNoError")
  @DisplayName("Should return false for 2xx and 3xx return codes")
  void has_error_should_return_false(ClientHttpResponse response) throws IOException {
    // when
    boolean hasError = errorHandler.hasError(response);

    // then
    assertThat(hasError).isFalse();
  }

  @Test
  @DisplayName("Should log 4xx and 5xx errors")
  void handle_error_should_log() throws IOException, URISyntaxException {
    // when
    errorHandler.handleError(new URI("https://www.metal-detector.rocks"), HttpMethod.GET, new MockClientHttpResponse(new byte[0], HttpStatus.NOT_FOUND));

    // then
    List<ILoggingEvent> logsList = listAppender.list;

    assertThat(logsList).hasSize(1);
    assertThat(logsList.get(0).getLevel()).isEqualTo(Level.WARN);
  }

  private static Stream<Arguments> inputProviderHasError() {
    return Stream.of(
        Arguments.of(new MockClientHttpResponse(new byte[0], HttpStatus.NOT_FOUND)),
        Arguments.of(new MockClientHttpResponse(new byte[0], HttpStatus.BAD_REQUEST)),
        Arguments.of(new MockClientHttpResponse(new byte[0], HttpStatus.INTERNAL_SERVER_ERROR)),
        Arguments.of(new MockClientHttpResponse(new byte[0], HttpStatus.SERVICE_UNAVAILABLE))
    );
  }

  private static Stream<Arguments> inputProviderNoError() {
    return Stream.of(
        Arguments.of(new MockClientHttpResponse(new byte[0], HttpStatus.OK)),
        Arguments.of(new MockClientHttpResponse(new byte[0], HttpStatus.TEMPORARY_REDIRECT))
    );
  }
}
