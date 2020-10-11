package rocks.metaldetector.web.controller.rest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import rocks.metaldetector.service.notification.NotificationService;
import rocks.metaldetector.testutil.BaseWebMvcTestWithSecurity;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static rocks.metaldetector.support.Endpoints.Rest.NOTIFY;

@WebMvcTest(controllers = NotificationRestController.class)
public class NotificationRestControllerIT extends BaseWebMvcTestWithSecurity {

  @MockBean
  private NotificationService notificationService;

  @Nested
  @DisplayName("Administrator is allowed to send requests to all notification endpoints")
  class AdministratorRoleTest {

    @Test
    @DisplayName("Administrator is allowed to POST on endpoint " + NOTIFY + "/{publicUserId}'")
    @WithMockUser(roles = "ADMINISTRATOR")
    void admin_is_allowed_to_notify_specified_user() throws Exception {
      mockMvc.perform(post(NOTIFY)
              .param("publicUserId", "user-id")
              .contentType(APPLICATION_JSON))
              .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Administrator is allowed to POST on endpoint " + NOTIFY + "'")
    @WithMockUser(roles = "ADMINISTRATOR")
    void admin_is_allowed_to_notify_all_users() throws Exception {
      mockMvc.perform(post(NOTIFY))
              .andExpect(status().isOk());
    }
  }

  @Nested
  @DisplayName("Users is not allowed to send requests to notification endpoints")
  class UserRoleTest {

    @Test
    @DisplayName("User is not allowed to POST on endpoint " + NOTIFY + "/{publicUserId}'")
    @WithMockUser(roles = "USER")
    void user_is_not_allowed_to_notify_specified_user() throws Exception {
      mockMvc.perform(post(NOTIFY)
              .param("publicUserId", "user-id")
              .contentType(APPLICATION_JSON))
              .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("User is not allowed to POST on endpoint " + NOTIFY + "'")
    @WithMockUser(roles = "USER")
    void user_is_not_allowed_to_notify_all_users() throws Exception {
      mockMvc.perform(post(NOTIFY))
              .andExpect(status().isForbidden());
    }
  }
}
