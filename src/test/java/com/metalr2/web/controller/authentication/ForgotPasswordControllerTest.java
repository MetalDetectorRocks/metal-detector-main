package com.metalr2.web.controller.authentication;

import com.metalr2.config.constants.Endpoints;
import com.metalr2.config.constants.ViewNames;
import com.metalr2.security.WebSecurity;
import com.metalr2.service.user.UserService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebMvcTest(ForgotPasswordController.class)
@Import(WebSecurity.class)
public class ForgotPasswordControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private UserService userService;

  @MockBean
  private BCryptPasswordEncoder passwordEncoder;

  @Before
  public void setUp() {
    // do nothing
  }

  @After
  public void tearDown() {
    // do nothing
  }

  @Test
  public void given_forgot_password_uri_should_return_forgot_password_view() throws Exception {
    mockMvc.perform(get(Endpoints.FORGOT_PASSWORD))
            .andExpect(status().isOk())
            .andExpect(view().name(ViewNames.FORGOT_PASSWORD))
            .andExpect(content().string(containsString("Forgot Password")));
  }
}
