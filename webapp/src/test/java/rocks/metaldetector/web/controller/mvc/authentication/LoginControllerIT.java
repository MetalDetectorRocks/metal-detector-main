package rocks.metaldetector.web.controller.mvc.authentication;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import rocks.metaldetector.service.user.UserEntityFactory;
import rocks.metaldetector.support.Endpoints;
import rocks.metaldetector.testutil.BaseWebMvcTestWithSecurity;

import java.util.stream.Stream;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = LoginController.class)
class LoginControllerIT extends BaseWebMvcTestWithSecurity {

  private static final String PARAM_USERNAME = "username";
  private static final String PARAM_PASSWORD = "password";
  private static final String USERNAME = "JohnD";
  private static final String PASSWORD = "plain-password";

  @BeforeEach
  void setup() {
    when(userService.loadUserByUsername(any())).thenAnswer(invocationOnMock -> {
      String usernameArg = invocationOnMock.getArgument(0);
      if (usernameArg.equalsIgnoreCase(USERNAME)) {
        return UserEntityFactory.createUser(USERNAME, "user@example.com", passwordEncoder.encode(PASSWORD));
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
  @DisplayName("Requesting secured resource should redirect to login page")
  void requesting_secured_resource_should_redirect_to_login_page() throws Exception {
    // when
    var resultActions = mockMvc.perform(get(Endpoints.AdminArea.INDEX));

    //then
    resultActions.andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrlPattern("**/" + Endpoints.Guest.LOGIN));
  }

  @Test
  @DisplayName("Login with valid credentials should redirect to home page")
  void login_with_valid_credentials_should_redirect_to_home_page() throws Exception {
    // given
    MockHttpServletRequestBuilder requestBuilder = post(Endpoints.Guest.LOGIN)
        .with(SecurityMockMvcRequestPostProcessors.csrf())
        .param(PARAM_USERNAME, USERNAME)
        .param(PARAM_PASSWORD, PASSWORD)
        .session(new MockHttpSession());

    // when
    var resultActions = mockMvc.perform(requestBuilder);

    // then
    resultActions.andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(Endpoints.Frontend.DASHBOARD));
  }

  @Test
  @DisplayName("Login with valid credentials should call user service")
  void login_with_valid_credentials_should_call_user_service() throws Exception {
    // given
    MockHttpServletRequestBuilder requestBuilder = post(Endpoints.Guest.LOGIN)
        .with(SecurityMockMvcRequestPostProcessors.csrf())
        .param(PARAM_USERNAME, USERNAME)
        .param(PARAM_PASSWORD, PASSWORD)
        .session(new MockHttpSession());

    // when
    mockMvc.perform(requestBuilder);

    // then
    verify(userService).loadUserByUsername(USERNAME);
  }

  @ParameterizedTest(name = "[{index}]: Username <{0}> and Password <{1}>")
  @MethodSource("credentialProvider")
  @DisplayName("Login with bad credentials should redirect to login page")
  void login_with_bad_credentials_should_redirect_to_login_page(String username, String plainPassword) throws Exception {
    // given
    var request = post(Endpoints.Guest.LOGIN)
        .with(SecurityMockMvcRequestPostProcessors.csrf())
        .param(PARAM_USERNAME, username)
        .param(PARAM_PASSWORD, plainPassword);

    // when
    var resultActions = mockMvc.perform(request);

    // then
    resultActions.andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(Endpoints.Guest.LOGIN + "?badCredentials"));
  }

  @ParameterizedTest(name = "[{index}]: Username <{0}> and Password <{1}>")
  @MethodSource("credentialProvider")
  @DisplayName("Login with bad credentials should call user service")
  void login_with_bad_credentials_should_call_user_service(String username, String plainPassword) throws Exception {
    // given
    var request = post(Endpoints.Guest.LOGIN)
        .with(SecurityMockMvcRequestPostProcessors.csrf())
        .param(PARAM_USERNAME, username)
        .param(PARAM_PASSWORD, plainPassword);

    // when
    mockMvc.perform(request);

    // then
    String expectedUsernameArgument = (username == null || username.isBlank()) ? "" : username;
    verify(userService).loadUserByUsername(expectedUsernameArgument);
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
}
