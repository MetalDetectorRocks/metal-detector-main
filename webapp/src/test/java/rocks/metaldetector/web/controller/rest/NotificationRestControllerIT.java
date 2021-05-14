package rocks.metaldetector.web.controller.rest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import rocks.metaldetector.service.notification.messaging.NotificationScheduler;
import rocks.metaldetector.testutil.BaseWebMvcTestWithSecurity;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static rocks.metaldetector.support.Endpoints.Rest.NOTIFICATION_ON_ANNOUNCEMENT_DATE;
import static rocks.metaldetector.support.Endpoints.Rest.NOTIFICATION_ON_FREQUENCY;
import static rocks.metaldetector.support.Endpoints.Rest.NOTIFICATION_ON_RELEASE_DATE;

@WebMvcTest(controllers = NotificationRestController.class)
public class NotificationRestControllerIT extends BaseWebMvcTestWithSecurity {

  @MockBean
  private NotificationScheduler notificationScheduler;

  @Nested
  @DisplayName("Administrator is allowed to send requests to all notification endpoints")
  class AdministratorRoleTest {

    @Test
    @DisplayName("Administrator is allowed to POST on endpoint " + NOTIFICATION_ON_FREQUENCY + "'")
    @WithMockUser(roles = "ADMINISTRATOR")
    void admin_is_allowed_to_notify_on_frequency() throws Exception {
      mockMvc.perform(post(NOTIFICATION_ON_FREQUENCY))
              .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Administrator is allowed to POST on endpoint " + NOTIFICATION_ON_RELEASE_DATE + "'")
    @WithMockUser(roles = "ADMINISTRATOR")
    void admin_is_allowed_to_notify_on_release_date() throws Exception {
      mockMvc.perform(post(NOTIFICATION_ON_RELEASE_DATE))
          .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Administrator is allowed to POST on endpoint " + NOTIFICATION_ON_ANNOUNCEMENT_DATE + "'")
    @WithMockUser(roles = "ADMINISTRATOR")
    void admin_is_allowed_to_notify_on_announcement_date() throws Exception {
      mockMvc.perform(post(NOTIFICATION_ON_ANNOUNCEMENT_DATE))
          .andExpect(status().isOk());
    }
  }

  @Nested
  @DisplayName("Users is not allowed to send requests to notification endpoints")
  class UserRoleTest {

    @Test
    @DisplayName("User is not allowed to POST on endpoint " + NOTIFICATION_ON_RELEASE_DATE + "'")
    @WithMockUser(roles = "USER")
    void user_is_not_allowed_to_notify_on_frequency() throws Exception {
      mockMvc.perform(post(NOTIFICATION_ON_FREQUENCY))
              .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("User is not allowed to POST on endpoint " + NOTIFICATION_ON_RELEASE_DATE + "'")
    @WithMockUser(roles = "USER")
    void user_is_not_allowed_to_notify_on_release_date() throws Exception {
      mockMvc.perform(post(NOTIFICATION_ON_RELEASE_DATE))
          .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("User is not allowed to POST on endpoint " + NOTIFICATION_ON_ANNOUNCEMENT_DATE + "'")
    @WithMockUser(roles = "USER")
    void user_is_not_allowed_to_notify_on_announcement_date() throws Exception {
      mockMvc.perform(post(NOTIFICATION_ON_ANNOUNCEMENT_DATE))
          .andExpect(status().isForbidden());
    }
  }
}
