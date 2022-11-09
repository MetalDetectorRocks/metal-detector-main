package rocks.metaldetector.web.controller.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import rocks.metaldetector.service.notification.config.NotificationConfigService;
import rocks.metaldetector.testutil.DtoFactory.UpdateNotificationConfigRequestFactory;
import rocks.metaldetector.testutil.WithIntegrationTestConfig;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static rocks.metaldetector.support.Endpoints.Rest.NOTIFICATION_CONFIG;

@SpringBootTest
@AutoConfigureMockMvc
public class NotificationConfigRestControllerIntegrationTest implements WithIntegrationTestConfig {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  @SuppressWarnings("unused")
  private NotificationConfigService notificationConfigService;

  @Nested
  class AdministratorRoleTest {

    @Test
    @DisplayName("Administrator is allowed to GET on endpoint " + NOTIFICATION_CONFIG + "'")
    @WithMockUser(roles = {"ADMINISTRATOR"})
    void administrator_is_allowed_to_fetch_notification_config() throws Exception {
      mockMvc.perform(get(NOTIFICATION_CONFIG))
          .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Administrator is allowed to PUT on endpoint " + NOTIFICATION_CONFIG + "'")
    @WithMockUser(roles = {"ADMINISTRATOR"})
    void administrator_is_allowed_to_update_notification_config() throws Exception {
      mockMvc.perform(put(NOTIFICATION_CONFIG)
              .with(csrf())
              .content(objectMapper.writeValueAsString(UpdateNotificationConfigRequestFactory.createDefault()))
              .contentType(APPLICATION_JSON)
          ).andExpect(status().isOk());
    }
  }

  @Nested
  class UserRoleTest {

    @Test
    @DisplayName("User is allowed to GET on endpoint " + NOTIFICATION_CONFIG + "'")
    @WithMockUser
    void user_is_allowed_to_fetch_notification_config() throws Exception {
      mockMvc.perform(get(NOTIFICATION_CONFIG))
          .andExpect(status().isOk());
    }

    @Test
    @DisplayName("User is allowed to PUT on endpoint " + NOTIFICATION_CONFIG + "'")
    @WithMockUser
    void user_is_allowed_to_update_notification_config() throws Exception {
      mockMvc.perform(put(NOTIFICATION_CONFIG)
          .with(csrf())
          .content(objectMapper.writeValueAsString(UpdateNotificationConfigRequestFactory.createDefault()))
          .contentType(APPLICATION_JSON)
      ).andExpect(status().isOk());
    }
  }

  @Nested
  class AnonymousUserTest {

    @Test
    @DisplayName("Anonymous user is not allowed to GET on endpoint " + NOTIFICATION_CONFIG + "'")
    @WithAnonymousUser
    void anonymous_user_is_not_allowed_to_fetch_notification_config() throws Exception {
      mockMvc.perform(get(NOTIFICATION_CONFIG))
          .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Anonymous user is not allowed to PUT on endpoint " + NOTIFICATION_CONFIG + "'")
    @WithAnonymousUser
    void anonymous_user_is_not_allowed_to_update_notification_config() throws Exception {
      mockMvc.perform(put(NOTIFICATION_CONFIG)
          .with(csrf())
          .content(objectMapper.writeValueAsString(UpdateNotificationConfigRequestFactory.createDefault()))
          .contentType(APPLICATION_JSON)
      ).andExpect(status().isUnauthorized());
    }
  }
}
