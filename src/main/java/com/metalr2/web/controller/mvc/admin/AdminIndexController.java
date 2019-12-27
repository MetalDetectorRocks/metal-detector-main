package com.metalr2.web.controller.mvc.admin;

import com.metalr2.config.constants.Endpoints;
import com.metalr2.config.constants.ViewNames;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class AdminIndexController {

  @GetMapping({Endpoints.AdminArea.INDEX})
  public ModelAndView showUsersList() {
    return new ModelAndView(ViewNames.AdminArea.INDEX);
  }

}
