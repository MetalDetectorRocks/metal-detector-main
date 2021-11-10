package rocks.metaldetector.web.controller.rest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import rocks.metaldetector.service.cleanup.RegistrationCleanupService;
import rocks.metaldetector.testutil.BaseWebMvcTestWithSecurity;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static rocks.metaldetector.support.Endpoints.Rest.REGISTRATION_CLEANUP;

@WebMvcTest(controllers = RegistrationCleanupRestController.class)
public class RegistrationCleanupRestControllerIT extends BaseWebMvcTestWithSecurity {

  @MockBean
  private RegistrationCleanupService registrationCleanupService;

  @Nested
  @DisplayName("Administrator is allowed to send requests to all endpoints")
  class AdministratorRoleTest {

    @Test
    @DisplayName("Administrator is allowed to POST on endpoint " + REGISTRATION_CLEANUP + "'")
    @WithMockUser(roles = "ADMINISTRATOR")
    void admin_is_allowed_to_cleanup() throws Exception {
      mockMvc.perform(post(REGISTRATION_CLEANUP))
              .andExpect(status().isOk());
    }
  }

  @Nested
  @DisplayName("User is not allowed to send requests to all endpoints")
  class UserRoleTest {

    @Test
    @DisplayName("User is not allowed to POST on endpoint " + REGISTRATION_CLEANUP + "'")
    @WithMockUser(roles = "USER")
    void user_is_not_allowed_to_cleanup() throws Exception {
      mockMvc.perform(post(REGISTRATION_CLEANUP))
              .andExpect(status().isForbidden());
    }
  }
}
