package com.metalr2.web.controller.mvc.authentication;

import com.metalr2.config.constants.Endpoints;
import com.metalr2.config.constants.ViewNames;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping(Endpoints.Guest.LOGIN)
public class LoginController {

  @GetMapping
  public ModelAndView showLoginForm() {
    return new ModelAndView(ViewNames.Guest.LOGIN);
  }
}
