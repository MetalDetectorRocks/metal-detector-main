package rocks.metaldetector.security;

import org.apache.commons.io.IOUtils;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;

class XSSRequestWrapperTest implements WithAssertions {

  private final MockHttpServletRequest request = new MockHttpServletRequest();

  @Test
  @DisplayName("The request's content can be overwritten and reader returns new data")
  void test_request_content_can_be_overwritten_reader() throws IOException {
    // given
    request.setContent("oldContent".getBytes());
    request.setCharacterEncoding(Charset.defaultCharset().name());
    var newContent = "newContent";
    var wrapper = new XSSRequestWrapper(request);

    // when
    wrapper.resetInputStream(newContent.getBytes());
    var result = IOUtils.toString(wrapper.getReader());

    // then
    assertThat(result).isEqualTo(newContent);
  }

  @Test
  @DisplayName("The request's content can be overwritten and input stream returns new data")
  void test_request_content_can_be_overwritten_input_stream() throws IOException {
    // given
    request.setContent("oldContent".getBytes());
    request.setCharacterEncoding(Charset.defaultCharset().name());
    var newContent = "newContent";
    var wrapper = new XSSRequestWrapper(request);

    // when
    wrapper.resetInputStream(newContent.getBytes());
    var result = IOUtils.toString(wrapper.getInputStream(), Charset.defaultCharset());

    // then
    assertThat(result).isEqualTo(newContent);
  }

  @Test
  @DisplayName("XSSUtils are called to sanitize parameter")
  void test_xss_utils_called_for_param() {
    // given
    var paramName = "testParam";
    var paramValue = "badInput";
    request.setParameter(paramName, paramValue);

    try(MockedStatic<XSSUtils> mock = Mockito.mockStatic(XSSUtils.class)) {
      // when
      new XSSRequestWrapper(request).getParameter(paramName);

      // then
      mock.verify(() -> XSSUtils.stripXSS(paramValue));
    }
  }

  @Test
  @DisplayName("The sanitized parameter value is returned")
  void test_sanitized_parameter_returned() {
    // given
    var paramName = "testParam";
    var sanitizedValue = "sanitizedValue";
    request.setParameter(paramName, "badInput");

    try(MockedStatic<XSSUtils> mock = Mockito.mockStatic(XSSUtils.class)) {
      mock.when(() -> XSSUtils.stripXSS(any())).thenReturn(sanitizedValue);

      // when
      var result = new XSSRequestWrapper(request).getParameter(paramName);

      // then
      assertThat(result).isEqualTo(sanitizedValue);
    }
  }

  @Test
  @DisplayName("XSSUtils are called for every parameter to sanitize values")
  void test_xss_utils_called_for_every_param() {
    // given
    var paramName = "testParam";
    var paramValue1 = "badInput1";
    var paramValue2 = "badInput2";
    request.setParameter(paramName, paramValue1, paramValue2);

    try(MockedStatic<XSSUtils> mock = Mockito.mockStatic(XSSUtils.class)) {
      // when
      new XSSRequestWrapper(request).getParameterValues(paramName);

      // then
      mock.verify(() -> XSSUtils.stripXSS(paramValue1));
      mock.verify(() -> XSSUtils.stripXSS(paramValue2));
    }
  }

  @Test
  @DisplayName("The sanitized parameter values are returned")
  void test_sanitized_parameters_returned() {
    // given
    var paramName = "testParam";
    var sanitizedValue1 = "sanitizedValue1";
    var sanitizedValue2 = "sanitizedValue2";
    request.setParameter(paramName, "badInput1", "badInput2");

    try(MockedStatic<XSSUtils> mock = Mockito.mockStatic(XSSUtils.class)) {
      mock.when(() -> XSSUtils.stripXSS(any())).thenReturn(sanitizedValue1, sanitizedValue2);

      // when
      var result = new XSSRequestWrapper(request).getParameterValues(paramName);

      // then
      assertThat(result.length).isEqualTo(2);
      assertThat(result[0]).isEqualTo(sanitizedValue1);
      assertThat(result[1]).isEqualTo(sanitizedValue2);
    }
  }

  @Test
  @DisplayName("XSSUtils are called to sanitize the header")
  void test_xss_utils_called_for_header() {
    // given
    var headerName = "testParam";
    var headerValue = "badInput";
    request.addHeader(headerName, headerValue);

    try(MockedStatic<XSSUtils> mock = Mockito.mockStatic(XSSUtils.class)) {
      // when
      new XSSRequestWrapper(request).getHeader(headerName);

      // then
      mock.verify(() -> XSSUtils.stripXSS(headerValue));
    }
  }

  @Test
  @DisplayName("The sanitized header value is returned")
  void test_sanitized_header_returned() {
    // given
    var headerName = "testParam";
    var sanitizedValue = "sanitizedValue";
    request.addHeader(headerName, "badInput");

    try(MockedStatic<XSSUtils> mock = Mockito.mockStatic(XSSUtils.class)) {
      mock.when(() -> XSSUtils.stripXSS(any())).thenReturn(sanitizedValue);

      // when
      var result = new XSSRequestWrapper(request).getHeader(headerName);

      // then
      assertThat(result).isEqualTo(sanitizedValue);
    }
  }

  @Test
  @DisplayName("XSSUtils are called to sanitize the headers")
  void test_xss_utils_called_for_headers() {
    // given
    var headerName = "testParam";
    var headerValue1 = "badInput1";
    var headerValue2 = "badInput2";
    request.addHeader(headerName, headerValue1);
    request.addHeader(headerName, headerValue2);

    try(MockedStatic<XSSUtils> mock = Mockito.mockStatic(XSSUtils.class)) {
      // when
      new XSSRequestWrapper(request).getHeaders(headerName);

      // then
      mock.verify(() -> XSSUtils.stripXSS(headerValue1));
      mock.verify(() -> XSSUtils.stripXSS(headerValue2));
    }
  }

  @Test
  @DisplayName("The sanitized header values are returned")
  void test_sanitized_headers_returned() {
    // given
    var headerName = "testParam";
    var sanitizedValue1 = "sanitizedValue1";
    var sanitizedValue2 = "sanitizedValue2";
    var headerValue1 = "badInput1";
    var headerValue2 = "badInput2";
    request.addHeader(headerName, headerValue1);
    request.addHeader(headerName, headerValue2);

    try(MockedStatic<XSSUtils> mock = Mockito.mockStatic(XSSUtils.class)) {
      mock.when(() -> XSSUtils.stripXSS(headerValue1)).thenReturn(sanitizedValue1);
      mock.when(() -> XSSUtils.stripXSS(headerValue2)).thenReturn(sanitizedValue2);

      // when
      var result = new XSSRequestWrapper(request).getHeaders(headerName);

      // then
      assertThat(Collections.list(result)).containsExactly(sanitizedValue1, sanitizedValue2);
    }
  }
}
