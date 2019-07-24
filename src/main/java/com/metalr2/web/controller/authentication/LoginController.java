package com.metalr2.web.controller.authentication;

import com.metalr2.config.constants.Endpoints;
import com.metalr2.config.constants.ViewNames;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(Endpoints.Guest.LOGIN)
public class LoginController {

  @GetMapping
  public String showLoginForm() {
    return ViewNames.Guest.LOGIN;
  }

}
