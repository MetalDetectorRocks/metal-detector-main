package rocks.metaldetector.config.logging;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.springframework.mock.web.MockHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.util.stream.Stream;

import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.PATCH;
import static rocks.metaldetector.support.Endpoints.Rest.CURRENT_USER_PASSWORD;

@ExtendWith(MockitoExtension.class)
class RestRequestLoggingFilterTest implements WithAssertions {

  @Mock
  private Logger logger;

  @InjectMocks
  private RestRequestLoggingFilter underTest;

  @AfterEach
  void tearDown() {
    reset(logger);
  }

  @Nested
  @TestInstance(TestInstance.Lifecycle.PER_CLASS)
  @DisplayName("Tests for shouldLog()")
  class ShouldLogTests {

    @ParameterizedTest
    @MethodSource("restCallProvider")
    @DisplayName("should log rest calls")
    void test_should_log_rest_calls(HttpServletRequest request) {
      // when
      var result = underTest.shouldLog(request);

      // then
      assertThat(result).isTrue();
    }

    @Test
    @DisplayName("should not log call to '" + CURRENT_USER_PASSWORD + "'")
    void test_should_not_log_password_change() {
      // given
      var request = new MockHttpServletRequest(PATCH.name(), CURRENT_USER_PASSWORD);

      // when
      var result = underTest.shouldLog(request);

      // then
      assertThat(result).isFalse();
    }

    @ParameterizedTest
    @MethodSource("otherCallProvider")
    @DisplayName("should not log other calls")
    void test_should_not_log_other_calls(HttpServletRequest request) {
      // when
      var result = underTest.shouldLog(request);

      // then
      assertThat(result).isFalse();
    }

    private Stream<Arguments> restCallProvider() {
      var mockHttpRequest1 = new MockHttpServletRequest(GET.name(), "/rest/v1/home");
      var mockHttpRequest2 = new MockHttpServletRequest(GET.name(), "/rest/v1/me");
      var mockHttpRequest3 = new MockHttpServletRequest(GET.name(), "/rest/v1/releases");
      return Stream.of(
          Arguments.of(mockHttpRequest1),
          Arguments.of(mockHttpRequest2),
          Arguments.of(mockHttpRequest3)
      );
    }

    private Stream<Arguments> otherCallProvider() {
      var mockHttpRequest1 = new MockHttpServletRequest(GET.name(), "/index");
      var mockHttpRequest2 = new MockHttpServletRequest(GET.name(), "/login");
      var mockHttpRequest3 = new MockHttpServletRequest(GET.name(), "/home");
      var mockHttpRequest4 = new MockHttpServletRequest(GET.name(), "/admin");
      return Stream.of(
          Arguments.of(mockHttpRequest1),
          Arguments.of(mockHttpRequest2),
          Arguments.of(mockHttpRequest3),
          Arguments.of(mockHttpRequest4)
      );
    }
  }

  @Nested
  @TestInstance(TestInstance.Lifecycle.PER_CLASS)
  @DisplayName("Tests for afterRequest()")
  class AfterRequestTests {

    @Test
    @DisplayName("message is logged as info")
    void test_log_info() {
      // given
      var message = "message";

      // when
      underTest.afterRequest(new MockHttpServletRequest(null, null), message);

      // then
      verify(logger).info(message);
    }
  }
}
