package rocks.metaldetector.web.controller.rest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import rocks.metaldetector.testutil.BaseSpringBootTest;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static rocks.metaldetector.support.Endpoints.Rest.REGISTRATION_CLEANUP;

@SpringBootTest
@AutoConfigureMockMvc
public class RegistrationCleanupRestControllerIntegrationTest extends BaseSpringBootTest {

  @Autowired
  private MockMvc mockMvc;

  @Nested
  @DisplayName("Administrator is allowed to send requests to all endpoints")
  class AdministratorRoleTest {

    @Test
    @DisplayName("Administrator is allowed to POST on endpoint " + REGISTRATION_CLEANUP + "'")
    @WithMockUser(roles = "ADMINISTRATOR")
    void admin_is_allowed_to_cleanup() throws Exception {
      mockMvc.perform(post(REGISTRATION_CLEANUP)
                          .with(csrf()))
          .andExpect(status().isOk());
    }
  }

  @Nested
  @DisplayName("User is not allowed to send requests to all endpoints")
  class UserRoleTest {

    @Test
    @DisplayName("User is not allowed to POST on endpoint " + REGISTRATION_CLEANUP + "'")
    @WithMockUser
    void user_is_not_allowed_to_cleanup() throws Exception {
      mockMvc.perform(post(REGISTRATION_CLEANUP)
                          .with(csrf()))
          .andExpect(status().isForbidden());
    }
  }

  @Nested
  @DisplayName("Anonymous user is not allowed to send requests to all endpoints")
  class AnonymousUserTest {

    @Test
    @DisplayName("Anonymous user is not allowed to POST on endpoint " + REGISTRATION_CLEANUP + "'")
    @WithAnonymousUser
    void anonymous_user_is_not_allowed_to_cleanup() throws Exception {
      mockMvc.perform(post(REGISTRATION_CLEANUP)
              .with(csrf()))
          .andExpect(status().isUnauthorized());
    }
  }
}
