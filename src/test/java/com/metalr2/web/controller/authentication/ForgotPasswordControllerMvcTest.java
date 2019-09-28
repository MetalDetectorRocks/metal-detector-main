package com.metalr2.web.controller.authentication;

import com.metalr2.config.constants.Endpoints;
import com.metalr2.config.constants.ViewNames;
import com.metalr2.security.WebSecurity;
import com.metalr2.service.user.UserService;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ForgotPasswordController.class)
@Import(WebSecurity.class)
@Tag("integration-test")
class ForgotPasswordControllerMvcTest {

  @Autowired
  private MockMvc mockMvc;

  // for WebSecurity
  @MockBean private UserService userService;
  @MockBean private BCryptPasswordEncoder passwordEncoder;

  @Test
  void given_forgot_password_uri_should_return_forgot_password_view() throws Exception {
    // ToDo 27.09.19 daniel: merge with ForgotPasswordControllerIT
    mockMvc.perform(get(Endpoints.Guest.FORGOT_PASSWORD))
            .andExpect(status().isOk())
            .andExpect(view().name(ViewNames.Guest.FORGOT_PASSWORD))
            .andExpect(model().attributeExists(ForgotPasswordController.FORM_DTO))
            .andExpect(content().string(containsString("Forgot Password")));
  }
}
