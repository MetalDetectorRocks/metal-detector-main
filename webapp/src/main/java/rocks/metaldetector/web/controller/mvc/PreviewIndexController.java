package rocks.metaldetector.web.controller.mvc;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;
import rocks.metaldetector.security.AuthenticationFacade;

import java.util.Map;

import static rocks.metaldetector.config.constants.ViewNames.Authentication.PREVIEW_INDEX;
import static rocks.metaldetector.config.constants.ViewNames.Frontend.DASHBOARD;
import static rocks.metaldetector.support.Endpoints.Frontend.HOME;
import static rocks.metaldetector.support.Endpoints.Frontend.INDEX;

@Controller
@Profile("preview")
@RequiredArgsConstructor
public class PreviewIndexController {

  private final AuthenticationFacade authenticationFacade;

  @GetMapping({INDEX, HOME})
  public ModelAndView showPreviewIndex() {
    if (authenticationFacade.isAuthenticated()) {
      var username = authenticationFacade.getCurrentUser().getUsername();
      return new ModelAndView(DASHBOARD, Map.of("username", username));
    }

    return new ModelAndView(PREVIEW_INDEX);
  }
}
