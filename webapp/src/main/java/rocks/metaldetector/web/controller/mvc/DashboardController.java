package rocks.metaldetector.web.controller.mvc;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import rocks.metaldetector.config.constants.ViewNames;
import rocks.metaldetector.security.AuthenticationFacade;
import rocks.metaldetector.support.Endpoints;

import java.util.Map;

@Controller
@RequestMapping(Endpoints.Frontend.DASHBOARD)
@RequiredArgsConstructor
public class DashboardController {

  private final AuthenticationFacade authenticationFacade;

  @GetMapping
  public ModelAndView showDashboard() {
    return new ModelAndView(ViewNames.Frontend.DASHBOARD, Map.of("username", authenticationFacade.getCurrentUser().getUsername()));
  }
}
