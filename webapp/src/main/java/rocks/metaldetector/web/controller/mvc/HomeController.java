package rocks.metaldetector.web.controller.mvc;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import rocks.metaldetector.config.constants.ViewNames;
import rocks.metaldetector.persistence.domain.user.AbstractUserEntity;
import rocks.metaldetector.security.CurrentUserSupplier;
import rocks.metaldetector.support.Endpoints;

import java.util.Map;

@Controller
@AllArgsConstructor
@RequestMapping(Endpoints.Frontend.HOME)
public class HomeController {

  private final CurrentUserSupplier currentUserSupplier;

  @GetMapping
  public ModelAndView showHome() {
    AbstractUserEntity currentUser = currentUserSupplier.get();
    Map<String, Object> model = Map.of("username", currentUser.getUsername());
    return new ModelAndView(ViewNames.Frontend.HOME, model);
  }
}
