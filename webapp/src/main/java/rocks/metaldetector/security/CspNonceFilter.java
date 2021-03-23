package rocks.metaldetector.security;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import rocks.metaldetector.support.Endpoints;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@AllArgsConstructor
public class CspNonceFilter extends OncePerRequestFilter {

  static final String CSP_HEADER_NAME = "Content-Security-Policy";
  static final String CSP_POLICY = "object-src 'none'; " +
                                   "script-src 'nonce-%s' 'strict-dynamic'; " +
                                   "style-src https://fonts.googleapis.com 'nonce-%s'; " +
                                   "base-uri 'none'; " +
                                   "form-action 'self'; " +
                                   "frame-ancestors 'none'; " +
                                   "report-uri " + Endpoints.Rest.CSP_VIOLATION_REPORT + "; " +
                                   "report-to " + Endpoints.Rest.CSP_VIOLATION_REPORT + ";";
  static final String ATTRIBUTE_NAME = "random";

  private final NonceSupplier nonceSupplier;

  @Override
  public void doFilterInternal(HttpServletRequest servletRequest, HttpServletResponse servletResponse, FilterChain filterChain) throws ServletException, IOException {
    String nonce = nonceSupplier.get();
    servletRequest.setAttribute(ATTRIBUTE_NAME, nonce);
    servletResponse.setHeader(CSP_HEADER_NAME, String.format(CSP_POLICY, nonce, nonce));
    filterChain.doFilter(servletRequest, servletResponse);
  }
}
