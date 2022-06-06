package rocks.metaldetector.web.controller.rest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import rocks.metaldetector.security.SecurityConfig;
import rocks.metaldetector.service.dashboard.DashboardService;
import rocks.metaldetector.testutil.BaseWebMvcTestWithSecurity;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static rocks.metaldetector.support.Endpoints.Rest.DASHBOARD;

@WebMvcTest(controllers = DashboardRestController.class)
@Import({SecurityConfig.class})
class DashboardRestControllerIntegrationTest extends BaseWebMvcTestWithSecurity {

  @MockBean
  @SuppressWarnings("unused")
  private DashboardService dashboardService;

  @Nested
  @DisplayName("Tests for user with ADMINISTRATOR role")
  class AdministratorRoleTest {

    @Test
    @DisplayName("Administrator is allowed to GET on endpoint " + DASHBOARD + "'")
    @WithMockUser(roles = "ADMINISTRATOR")
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
    @WithMockUser(roles = "USER")
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
