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
import rocks.metaldetector.testutil.WithIntegrationTestConfig;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static rocks.metaldetector.support.Endpoints.Rest.NOTIFICATION_ON_ANNOUNCEMENT_DATE;
import static rocks.metaldetector.support.Endpoints.Rest.NOTIFICATION_ON_FREQUENCY;
import static rocks.metaldetector.support.Endpoints.Rest.NOTIFICATION_ON_RELEASE_DATE;

@SpringBootTest
@AutoConfigureMockMvc
public class NotificationRestControllerIntegrationTest implements WithIntegrationTestConfig {

  @Autowired
  private MockMvc mockMvc;

  @Nested
  @DisplayName("Administrator is allowed to send requests to all notification endpoints")
  class AdministratorRoleTest {

    @Test
    @DisplayName("Administrator is allowed to POST on endpoint " + NOTIFICATION_ON_FREQUENCY + "'")
    @WithMockUser(roles = {"ADMINISTRATOR"})
    void admin_is_allowed_to_notify_on_frequency() throws Exception {
      mockMvc.perform(post(NOTIFICATION_ON_FREQUENCY)
                          .with(csrf()))
          .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Administrator is allowed to POST on endpoint " + NOTIFICATION_ON_RELEASE_DATE + "'")
    @WithMockUser(roles = {"ADMINISTRATOR"})
    void admin_is_allowed_to_notify_on_release_date() throws Exception {
      mockMvc.perform(post(NOTIFICATION_ON_RELEASE_DATE)
                          .with(csrf()))
          .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Administrator is allowed to POST on endpoint " + NOTIFICATION_ON_ANNOUNCEMENT_DATE + "'")
    @WithMockUser(roles = {"ADMINISTRATOR"})
    void admin_is_allowed_to_notify_on_announcement_date() throws Exception {
      mockMvc.perform(post(NOTIFICATION_ON_ANNOUNCEMENT_DATE)
                          .with(csrf()))
          .andExpect(status().isOk());
    }
  }

  @Nested
  @DisplayName("User is not allowed to send requests to notification endpoints")
  class UserRoleTest {

    @Test
    @DisplayName("User is not allowed to POST on endpoint " + NOTIFICATION_ON_RELEASE_DATE + "'")
    @WithMockUser
    void user_is_not_allowed_to_notify_on_frequency() throws Exception {
      mockMvc.perform(post(NOTIFICATION_ON_FREQUENCY)
                          .with(csrf()))
          .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("User is not allowed to POST on endpoint " + NOTIFICATION_ON_RELEASE_DATE + "'")
    @WithMockUser
    void user_is_not_allowed_to_notify_on_release_date() throws Exception {
      mockMvc.perform(post(NOTIFICATION_ON_RELEASE_DATE)
                          .with(csrf()))
          .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("User is not allowed to POST on endpoint " + NOTIFICATION_ON_ANNOUNCEMENT_DATE + "'")
    @WithMockUser
    void user_is_not_allowed_to_notify_on_announcement_date() throws Exception {
      mockMvc.perform(post(NOTIFICATION_ON_ANNOUNCEMENT_DATE)
                          .with(csrf()))
          .andExpect(status().isForbidden());
    }
  }

  @Nested
  @DisplayName("Anonymous user is not allowed to send requests to notification endpoints")
  class AnonymousUserTest {

    @Test
    @DisplayName("Anonymous user is not allowed to POST on endpoint " + NOTIFICATION_ON_RELEASE_DATE + "'")
    @WithAnonymousUser
    void anonymous_user_is_not_allowed_to_notify_on_frequency() throws Exception {
      mockMvc.perform(post(NOTIFICATION_ON_FREQUENCY)
              .with(csrf()))
          .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Anonymous user is not allowed to POST on endpoint " + NOTIFICATION_ON_RELEASE_DATE + "'")
    @WithAnonymousUser
    void anonymous_user_is_not_allowed_to_notify_on_release_date() throws Exception {
      mockMvc.perform(post(NOTIFICATION_ON_RELEASE_DATE)
              .with(csrf()))
          .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Anonymous user is not allowed to POST on endpoint " + NOTIFICATION_ON_ANNOUNCEMENT_DATE + "'")
    @WithAnonymousUser
    void anonymous_user_is_not_allowed_to_notify_on_announcement_date() throws Exception {
      mockMvc.perform(post(NOTIFICATION_ON_ANNOUNCEMENT_DATE)
              .with(csrf()))
          .andExpect(status().isUnauthorized());
    }
  }
}
