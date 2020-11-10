package rocks.metaldetector.security;

import lombok.AllArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import org.springframework.stereotype.Component;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.IOException;

@Component
@AllArgsConstructor
public class SearchQuerySanitizingFilter implements Filter {

  static final String PARAMETER_NAME = "query";

  @Override
  public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
    filterChain.doFilter(new QuerySanitizingRequestWrapper((HttpServletRequest) servletRequest), servletResponse);
  }

  private static class QuerySanitizingRequestWrapper extends HttpServletRequestWrapper {

    private QuerySanitizingRequestWrapper(HttpServletRequest request) {
      super(request);
    }

    @Override
    public String[] getParameterValues(String name) {
      if (name.equals(PARAMETER_NAME)) {
        return new String[] {Jsoup.clean(super.getParameter(name), Whitelist.none())};
      }
      return super.getParameterValues(name);
    }
  }
}
