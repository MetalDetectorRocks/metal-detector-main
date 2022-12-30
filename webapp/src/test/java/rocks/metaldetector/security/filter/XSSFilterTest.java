package rocks.metaldetector.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletResponse;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import rocks.metaldetector.security.XSSUtils;
import rocks.metaldetector.security.filter.XSSFilter;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class XSSFilterTest implements WithAssertions {

  private final XSSFilter underTest = new XSSFilter();

  private final MockHttpServletRequest request = new MockHttpServletRequest();
  private final ServletResponse response = new MockHttpServletResponse();
  private final FilterChain filterChain = new MockFilterChain();

  @Test
  @DisplayName("XSSUtils are called to sanitize the request body")
  void test_xss_utils_called() throws IOException, ServletException {
    // given
    var requestBody = "<h1>Nirvana</h1>";
    request.setContent(requestBody.getBytes());

    try(MockedStatic<XSSUtils> mock = Mockito.mockStatic(XSSUtils.class)) {
      mock.when(() -> XSSUtils.stripXSS(any())).thenReturn("strippedContent");

      // when
      underTest.doFilter(request, response, filterChain);

      // then
      mock.verify(() -> XSSUtils.stripXSS(requestBody));
    }
  }
}
