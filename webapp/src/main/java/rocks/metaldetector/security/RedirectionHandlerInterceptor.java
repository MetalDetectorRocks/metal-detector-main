package rocks.metaldetector.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import static org.springframework.http.HttpStatus.TEMPORARY_REDIRECT;
import static rocks.metaldetector.support.Endpoints.Frontend.HOME;

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
    String encodedRedirectURL = response.encodeRedirectURL(request.getContextPath() + HOME);
    response.setStatus(TEMPORARY_REDIRECT.value());
    response.setHeader("Location", encodedRedirectURL);
  }
}
