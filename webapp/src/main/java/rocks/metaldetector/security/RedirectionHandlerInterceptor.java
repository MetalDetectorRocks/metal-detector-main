package rocks.metaldetector.security;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.springframework.http.HttpStatus.TEMPORARY_REDIRECT;
import static rocks.metaldetector.support.Endpoints.Frontend.SLASH_HOME;

@Component
@AllArgsConstructor
public class RedirectionHandlerInterceptor implements HandlerInterceptor {

  private final AuthenticationFacade authenticationFacade;

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
    if (authenticationFacade.getCurrentUser() != null) {
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
    String encodedRedirectURL = response.encodeRedirectURL(request.getContextPath() + SLASH_HOME);
    response.setStatus(TEMPORARY_REDIRECT.value());
    response.setHeader("Location", encodedRedirectURL);
  }
}
