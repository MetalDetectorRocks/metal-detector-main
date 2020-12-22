package rocks.metaldetector.security;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import java.io.IOException;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static rocks.metaldetector.security.CspNonceFilter.ATTRIBUTE_NAME;
import static rocks.metaldetector.security.CspNonceFilter.CSP_HEADER_NAME;
import static rocks.metaldetector.security.CspNonceFilter.CSP_POLICY;

@ExtendWith(MockitoExtension.class)
class CspNonceFilterTest implements WithAssertions {

  private static final String NONCE = "nonce";

  @Mock
  private NonceSupplier nonceSupplier;

  @InjectMocks
  private CspNonceFilter underTest;

  MockHttpServletRequest request = new MockHttpServletRequest();
  MockHttpServletResponse response = new MockHttpServletResponse();
  FilterChain filterChain = mock(FilterChain.class);

  @BeforeEach
  void setup() {
    doReturn(NONCE).when(nonceSupplier).get();
  }

  @AfterEach
  void tearDown() {
    reset(nonceSupplier);
  }

  @Test
  @DisplayName("nonceSupplier is called")
  void test_nonce_supplier_called() throws IOException, ServletException {
    // when
    underTest.doFilter(request, response, filterChain);

    // then
    verify(nonceSupplier).get();
  }

  @Test
  @DisplayName("filterChain is called")
  void test_filter_chain_called() throws IOException, ServletException {
    // when
    underTest.doFilter(request, response, filterChain);

    // then
    verify(filterChain).doFilter(request, response);
  }

  @Test
  @DisplayName("nonce is set as request attribute")
  void test_nonce_set_in_request() throws IOException, ServletException {
    // when
    underTest.doFilter(request, response, filterChain);

    // then
    assertThat(request.getAttribute(ATTRIBUTE_NAME)).isNotNull().isEqualTo(NONCE);
  }

  @Test
  @DisplayName("csp header is set on response")
  void test_csp_header_set_in_response() throws IOException, ServletException {
    // when
    underTest.doFilter(request, response, filterChain);

    // then
    assertThat(response.getHeader(CSP_HEADER_NAME)).isNotNull().isEqualTo(String.format(CSP_POLICY, NONCE, NONCE));
  }
}