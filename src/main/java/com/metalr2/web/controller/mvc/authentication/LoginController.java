package com.metalr2.web.controller.mvc.authentication;

import com.metalr2.config.constants.Endpoints;
import com.metalr2.config.constants.ViewNames;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping(Endpoints.Guest.LOGIN)
public class LoginController {

  @GetMapping
  public ModelAndView showLoginForm(Authentication authentication) {
    if (authentication != null && authentication.isAuthenticated()) {
      return new ModelAndView("redirect:" + Endpoints.Frontend.HOME);
    }
    return new ModelAndView(ViewNames.Guest.LOGIN);
  }
}
