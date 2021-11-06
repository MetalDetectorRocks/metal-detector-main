package rocks.metaldetector.web.controller.rest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import rocks.metaldetector.service.cleanup.CleanupService;
import rocks.metaldetector.testutil.BaseWebMvcTestWithSecurity;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static rocks.metaldetector.support.Endpoints.Rest.CLEANUP;

@WebMvcTest(controllers = CleanupRestController.class)
public class CleanupRestControllerIT extends BaseWebMvcTestWithSecurity {

  @MockBean
  private CleanupService cleanupService;

  @Nested
  @DisplayName("Administrator is allowed to send requests to all endpoints")
  class AdministratorRoleTest {

    @Test
    @DisplayName("Administrator is allowed to POST on endpoint " + CLEANUP + "'")
    @WithMockUser(roles = "ADMINISTRATOR")
    void admin_is_allowed_to_cleanup() throws Exception {
      mockMvc.perform(post(CLEANUP))
              .andExpect(status().isOk());
    }
  }

  @Nested
  @DisplayName("Users is not allowed to send requests to all endpoints")
  class UserRoleTest {

    @Test
    @DisplayName("User is not allowed to POST on endpoint " + CLEANUP + "'")
    @WithMockUser(roles = "USER")
    void user_is_not_allowed_to_cleanup() throws Exception {
      mockMvc.perform(post(CLEANUP))
              .andExpect(status().isForbidden());
    }
  }
}
