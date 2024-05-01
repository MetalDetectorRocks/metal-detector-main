package rocks.metaldetector.web.controller.rest.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.web.servlet.MockMvc;
import rocks.metaldetector.service.auth.ResetPasswordService;
import rocks.metaldetector.service.user.UserService;
import rocks.metaldetector.testutil.BaseSpringBootTest;
import rocks.metaldetector.web.api.auth.InitResetPasswordRequest;
import rocks.metaldetector.web.api.auth.RegisterUserRequest;
import rocks.metaldetector.web.api.auth.RegistrationVerificationRequest;
import rocks.metaldetector.web.api.auth.ResetPasswordRequest;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static rocks.metaldetector.support.Endpoints.Rest.REGISTER;
import static rocks.metaldetector.support.Endpoints.Rest.REGISTRATION_VERIFICATION;
import static rocks.metaldetector.support.Endpoints.Rest.REQUEST_PASSWORD_RESET;
import static rocks.metaldetector.support.Endpoints.Rest.RESET_PASSWORD;

@SpringBootTest
@AutoConfigureMockMvc
class AuthenticationRestControllerIntegrationTest extends BaseSpringBootTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  @SuppressWarnings("unused")
  private UserService userService;

  @MockBean
  @SuppressWarnings("unused")
  private ResetPasswordService resetPasswordService;

  @Nested
  class AnonymousUserTest {

    @Test
    @DisplayName("Anonymous user is allowed to POST on endpoint " + REGISTER + "'")
    @WithAnonymousUser
    void anonymous_user_is_allowed_to_post_on_endpoint_register() throws Exception {
      mockMvc.perform(post(REGISTER)
              .content(objectMapper.writeValueAsString(new RegisterUserRequest("Test", "test@example.com", "testtest")))
              .contentType(APPLICATION_JSON)
              .accept(APPLICATION_JSON)
          )
          .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Anonymous user is allowed to POST on endpoint " + REGISTRATION_VERIFICATION + "'")
    @WithAnonymousUser
    void anonymous_user_is_allowed_to_post_on_endpoint_registration_verification() throws Exception {
      mockMvc.perform(post(REGISTRATION_VERIFICATION)
              .content(objectMapper.writeValueAsString(new RegistrationVerificationRequest("eyFoo")))
              .contentType(APPLICATION_JSON)
              .accept(APPLICATION_JSON)
          )
          .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Anonymous user is allowed to POST on endpoint " + REQUEST_PASSWORD_RESET + "'")
    @WithAnonymousUser
    void anonymous_user_is_allowed_to_get_on_endpoint_request_password_reset() throws Exception {
      mockMvc.perform(post(REQUEST_PASSWORD_RESET)
              .content(objectMapper.writeValueAsString(new InitResetPasswordRequest("test@example.com")))
              .contentType(APPLICATION_JSON)
              .accept(APPLICATION_JSON)
          )
          .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Anonymous user is allowed to POST on endpoint " + RESET_PASSWORD + "'")
    @WithAnonymousUser
    void anonymous_user_is_allowed_to_get_on_endpoint_reset_password() throws Exception {
      mockMvc.perform(post(RESET_PASSWORD)
              .content(objectMapper.writeValueAsString(new ResetPasswordRequest("eyFoobar", "new-password")))
              .contentType(APPLICATION_JSON)
              .accept(APPLICATION_JSON)
          )
          .andExpect(status().isOk());
    }
  }
}