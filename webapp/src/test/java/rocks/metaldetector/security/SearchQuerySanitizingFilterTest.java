package rocks.metaldetector.security;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static rocks.metaldetector.security.SearchQuerySanitizingFilter.PARAMETER_NAME;

@ExtendWith(MockitoExtension.class)
class SearchQuerySanitizingFilterTest implements WithAssertions {

  private final SearchQuerySanitizingFilter underTest = new SearchQuerySanitizingFilter();

  @Test
  @DisplayName("Sanitized query is returned after filtering")
  void test_request_sanitized() throws IOException, ServletException {
    // given
    ArgumentCaptor<HttpServletRequest> argumentCaptor = ArgumentCaptor.forClass(HttpServletRequest.class);
    MockHttpServletRequest request = new MockHttpServletRequest();
    MockHttpServletResponse response = new MockHttpServletResponse();
    FilterChain filterChain = Mockito.mock(FilterChain.class);
    request.setParameter(PARAMETER_NAME, "<h1>Nirvana</h1>");

    // when
    underTest.doFilter(request, response, filterChain);

    // then
    verify(filterChain).doFilter(argumentCaptor.capture(), eq(response));
    HttpServletRequest filteredRequest = argumentCaptor.getValue();
    assertThat(filteredRequest.getParameterValues(PARAMETER_NAME)[0]).isEqualTo("Nirvana");
  }
}