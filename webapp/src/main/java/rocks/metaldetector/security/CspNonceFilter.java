package rocks.metaldetector.security;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@AllArgsConstructor
public class CspNonceFilter implements Filter {

  static final String CSP_HEADER_NAME = "Content-Security-Policy";
  static final String CSP_POLICY = "object-src 'none'; script-src 'nonce-%s' 'strict-dynamic'; style-src https://fonts.googleapis.com 'nonce-%s'; base-uri 'none'; report-uri /rest/v1/csp-violation-report; report-to /rest/v1/csp-violation-report;";
  static final String ATTRIBUTE_NAME = "random";

  private final NonceSupplier nonceSupplier;

  @Override
  public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
    String nonce = nonceSupplier.get();
    servletRequest.setAttribute(ATTRIBUTE_NAME, nonce);
    ((HttpServletResponse) servletResponse).setHeader(CSP_HEADER_NAME, CSP_POLICY.formatted(nonce, nonce));
    filterChain.doFilter(servletRequest, servletResponse);
  }
}
