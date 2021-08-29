package rocks.metaldetector.support;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.mock.http.client.MockClientHttpRequest;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Collections;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@ExtendWith(MockitoExtension.class)
class PostHeaderInterceptorTest implements WithAssertions {

  private final PostHeaderInterceptor underTest = new PostHeaderInterceptor();

  @Test
  @DisplayName("accept header is set")
  void test_accept() throws IOException {
    // given
    var mockRequest = new MockClientHttpRequest();

    // when
    underTest.intercept(mockRequest, new byte[0], mock(ClientHttpRequestExecution.class));

    // then
    assertThat(mockRequest.getHeaders().getAccept()).isEqualTo(Collections.singletonList(APPLICATION_JSON));
  }

  @Test
  @DisplayName("acceptCharset header is set")
  void test_accept_charset() throws IOException {
    // given
    var mockRequest = new MockClientHttpRequest();

    // when
    underTest.intercept(mockRequest, new byte[0], mock(ClientHttpRequestExecution.class));

    // then
    assertThat(mockRequest.getHeaders().getAcceptCharset()).isEqualTo(Collections.singletonList(Charset.defaultCharset()));
  }

  @Test
  @DisplayName("contentType header is set")
  void test_content_type() throws IOException {
    // given
    var mockRequest = new MockClientHttpRequest();

    // when
    underTest.intercept(mockRequest, new byte[0], mock(ClientHttpRequestExecution.class));

    // then
    assertThat(mockRequest.getHeaders().getContentType()).isEqualTo(APPLICATION_JSON);
  }

  @Test
  @DisplayName("execution is called")
  void test_execution_called() throws IOException {
    // given
    var mockExecution = mock(ClientHttpRequestExecution.class);
    var mockRequest = new MockClientHttpRequest();
    var body = new byte[0];

    // when
    underTest.intercept(mockRequest, body, mockExecution);

    // then
    verify(mockExecution).execute(mockRequest, body);
  }
}
