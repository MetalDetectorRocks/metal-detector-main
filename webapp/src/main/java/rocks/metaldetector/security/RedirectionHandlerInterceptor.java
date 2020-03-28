package rocks.metaldetector.security;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import rocks.metaldetector.config.constants.Endpoints;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
@AllArgsConstructor
public class RedirectionHandlerInterceptor implements HandlerInterceptor {

  private final CurrentUserSupplier currentUserSupplier;

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
    if (currentUserSupplier.get() != null) {
      response.setContentType("text/plain");
      sendRedirect(request, response);
      return false;
    }
    return true;
  }

  @Override
  public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                         ModelAndView modelAndView) {
  }

  @Override
  public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                              Object handler, Exception exception) {
  }

  private void sendRedirect(HttpServletRequest request, HttpServletResponse response) {
    String encodedRedirectURL = response.encodeRedirectURL(request.getContextPath() + Endpoints.Frontend.STATUS);
    response.setStatus(HttpStatus.TEMPORARY_REDIRECT.value());
    response.setHeader("Location", encodedRedirectURL);
  }
}
