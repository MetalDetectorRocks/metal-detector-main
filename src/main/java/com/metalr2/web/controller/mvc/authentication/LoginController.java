package com.metalr2.web.controller.mvc.authentication;

import com.metalr2.config.constants.Endpoints;
import com.metalr2.config.constants.ViewNames;
import com.metalr2.service.redirection.RedirectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.Optional;

@Controller
@RequestMapping(Endpoints.Guest.LOGIN)
public class LoginController {

  private final RedirectionService redirectionService;

  @Autowired
  public LoginController(RedirectionService redirectionService) {
    this.redirectionService = redirectionService;
  }

  @GetMapping
  public ModelAndView showLoginForm(Authentication authentication) {
    Optional<ModelAndView> redirectionOptional = redirectionService.getRedirectionIfNeeded(authentication);
    return redirectionOptional.orElseGet(() -> new ModelAndView(ViewNames.Guest.LOGIN));
  }
}
