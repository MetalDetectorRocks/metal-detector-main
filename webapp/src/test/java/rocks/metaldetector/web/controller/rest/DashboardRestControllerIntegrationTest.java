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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static rocks.metaldetector.support.Endpoints.Rest.DASHBOARD;

@SpringBootTest
@AutoConfigureMockMvc
class DashboardRestControllerIntegrationTest extends BaseSpringBootTest {

  @Autowired
  private MockMvc mockMvc;

  @Nested
  @DisplayName("Tests for user with ADMINISTRATOR role")
  class AdministratorRoleTest {

    @Test
    @DisplayName("Administrator is allowed to GET on endpoint " + DASHBOARD + "'")
    @WithMockUser(roles = {"ADMINISTRATOR"})
    void admin_is_allowed_to_get_dashboard() throws Exception {
      mockMvc.perform(get(DASHBOARD))
          .andExpect(status().isOk());
    }
  }

  @Nested
  @DisplayName("Tests for user with USER role")
  class UserRoleTest {

    @Test
    @DisplayName("User is allowed to GET on endpoint " + DASHBOARD + "'")
    @WithMockUser
    void user_is_allowed_to_get_dashboard() throws Exception {
      mockMvc.perform(get(DASHBOARD))
          .andExpect(status().isOk());
    }
  }

  @Nested
  @DisplayName("Tests for anonymous user")
  class AnonymousUserTest {

    @Test
    @DisplayName("Anonymous user is not allowed to GET on endpoint " + DASHBOARD + "'")
    @WithAnonymousUser
    void anonymous_user_is_not_allowed_to_get_dashboard() throws Exception {
      mockMvc.perform(get(DASHBOARD))
          .andExpect(status().isUnauthorized());
    }
  }
}
