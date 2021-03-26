package rocks.metaldetector.web.controller.mvc;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import rocks.metaldetector.config.constants.ViewNames;
import rocks.metaldetector.security.CurrentUserSupplier;
import rocks.metaldetector.support.Endpoints;

import java.util.Map;

@Controller
@RequestMapping(Endpoints.Frontend.HOME)
@RequiredArgsConstructor
public class HomeController {

  private final CurrentUserSupplier currentUserSupplier;

  @GetMapping
  public ModelAndView showHome() {
    return new ModelAndView(ViewNames.Frontend.HOME, Map.of("username", currentUserSupplier.get().getUsername()));
  }
}
