package rocks.metaldetector.web.controller.mvc;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;
import rocks.metaldetector.security.AuthenticationFacade;
import rocks.metaldetector.support.Endpoints;

import java.util.Map;

import static rocks.metaldetector.config.constants.ViewNames.Frontend.DASHBOARD;
import static rocks.metaldetector.config.constants.ViewNames.Frontend.INDEX;

@Controller
@RequiredArgsConstructor
public class IndexController {

  private final AuthenticationFacade authenticationFacade;

  @GetMapping({Endpoints.Frontend.INDEX, Endpoints.Frontend.HOME})
  public ModelAndView showIndex() {
    if (authenticationFacade.isAuthenticated()) {
      var username = authenticationFacade.getCurrentUser().getUsername();
      return new ModelAndView(DASHBOARD, Map.of("username", username));
    }

    return new ModelAndView(INDEX);
  }
}
