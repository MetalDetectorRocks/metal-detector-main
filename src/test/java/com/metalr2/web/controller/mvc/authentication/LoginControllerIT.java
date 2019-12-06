package com.metalr2.web.controller.mvc.authentication;

import com.metalr2.config.constants.Endpoints;
import com.metalr2.config.constants.ViewNames;
import com.metalr2.model.user.UserFactory;
import com.metalr2.security.SecurityConfig;
import com.metalr2.service.user.UserService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

import java.util.stream.Stream;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LoginController.class)
@Import(SecurityConfig.class)
@Tag("integration-test")
class LoginControllerIT {

  private static final String PARAM_USERNAME = "username";
  private static final String PARAM_PASSWORD = "password";
  private static final String USERNAME       = "JohnD";
  private static final String PASSWORD       = "plain-password";

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private UserService userService;

  @Autowired
  private BCryptPasswordEncoder passwordEncoder;

  @BeforeEach
  void setup() {
    when(userService.loadUserByUsername(any())).thenAnswer(invocationOnMock -> {
      String usernameArg = invocationOnMock.getArgument(0);
      if (usernameArg.equalsIgnoreCase(USERNAME)) {
        return UserFactory.createUser(USERNAME, "user@example.com", passwordEncoder.encode(PASSWORD));
      }
      else {
        throw new UsernameNotFoundException("username not found");
      }
    });
  }

  @AfterEach
  void tearDown() {
    reset(userService);
  }

  @Test
  @DisplayName("Requesting '" + Endpoints.Guest.LOGIN + "' should return the view to login")
  void given_login_uri_should_return_login_view() throws Exception {
    mockMvc.perform(get(Endpoints.Guest.LOGIN))
            .andExpect(status().isOk())
            .andExpect(view().name(ViewNames.Guest.LOGIN))
            .andExpect(model().size(0))
            .andExpect(content().contentType("text/html;charset=UTF-8"))
            .andExpect(content().string(containsString("Login")));
  }

  @Test
  @DisplayName("Login with valid credentials should be ok")
  void login_with_valid_credentials_should_be_ok() throws Exception {
    mockMvc.perform(post(Endpoints.Guest.LOGIN)
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .param(PARAM_USERNAME, USERNAME)
                .param(PARAM_PASSWORD, PASSWORD))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl(Endpoints.Frontend.HOME));

    verify(userService, times(1)).loadUserByUsername(USERNAME);
  }

  @ParameterizedTest(name = "[{index}]: Username <{0}> and Password <{1}>")
  @MethodSource("credentialProvider")
  @DisplayName("Login with bad credentials should fail")
  void login_with_bad_credentials_should_fail(String username, String plainPassword) throws Exception {
    mockMvc.perform(post(Endpoints.Guest.LOGIN)
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .param(PARAM_USERNAME, username)
                .param(PARAM_PASSWORD, plainPassword))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl(Endpoints.Guest.LOGIN + "?badCredentials"));

    String expectedUsernameArgument = (username == null || username.isBlank()) ? "" : username;
    verify(userService, times(1)).loadUserByUsername(expectedUsernameArgument);
  }

  private static Stream<Arguments> credentialProvider() {
    return Stream.of(
            Arguments.of("", ""),
            Arguments.of("  ", "   "),
            Arguments.of(null, null),
            Arguments.of("invalid@example.com", PASSWORD),
            Arguments.of(USERNAME, ""),
            Arguments.of(USERNAME, null),
            Arguments.of(USERNAME, "invalid-password")
    );
  }

  @TestConfiguration
  static class TestConfig {

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
      return new BCryptPasswordEncoder();
    }

  }

}
