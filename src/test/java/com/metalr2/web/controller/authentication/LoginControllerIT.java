package com.metalr2.web.controller.authentication;

import com.metalr2.config.constants.Endpoints;
import com.metalr2.config.constants.ViewNames;
import com.metalr2.service.user.UserService;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LoginController.class)
@Import(WebSecurity.class)
@Tag("integration-test")
class LoginControllerIT {

  @Autowired
  private MockMvc mockMvc;

  // for WebSecurity
  @MockBean private UserService userService;
  @MockBean private BCryptPasswordEncoder passwordEncoder;

  @Test
  void given_login_uri_should_return_login_view() throws Exception {
    mockMvc.perform(get(Endpoints.Guest.LOGIN))
            .andExpect(status().isOk())
            .andExpect(view().name(ViewNames.Guest.LOGIN))
            .andExpect(model().size(0))
            .andExpect(content().string(containsString("Login")));
  }
}
