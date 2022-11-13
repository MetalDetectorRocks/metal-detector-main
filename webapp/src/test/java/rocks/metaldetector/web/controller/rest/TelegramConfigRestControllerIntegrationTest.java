package rocks.metaldetector.web.controller.rest;

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
import rocks.metaldetector.service.notification.config.TelegramConfigService;
import rocks.metaldetector.testutil.BaseSpringBootTest;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static rocks.metaldetector.support.Endpoints.Rest.TELEGRAM_CONFIG;

@SpringBootTest
@AutoConfigureMockMvc
public class TelegramConfigRestControllerIntegrationTest extends BaseSpringBootTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  @SuppressWarnings("unused")
  private TelegramConfigService telegramConfigService;

  @Nested
  class AdministratorRoleTest {

    @Test
    @DisplayName("Administrator is allowed to POST on endpoint " + TELEGRAM_CONFIG + "'")
    @WithMockUser(roles = {"ADMINISTRATOR"})
    void administrator_is_allowed_to_generate_registration_id() throws Exception {
      mockMvc.perform(post(TELEGRAM_CONFIG)
              .with(csrf())
          )
          .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Administrator is allowed to DELETE on endpoint " + TELEGRAM_CONFIG + "'")
    @WithMockUser(roles = {"ADMINISTRATOR"})
    void administrator_is_allowed_to_delete_telegram_config() throws Exception {
      mockMvc.perform(delete(TELEGRAM_CONFIG)
              .with(csrf())
          )
          .andExpect(status().isOk());
    }
  }

  @Nested
  class UserRoleTest {

    @Test
    @DisplayName("User is allowed to POST on endpoint " + TELEGRAM_CONFIG + "'")
    @WithMockUser
    void user_is_allowed_to_generate_registration_id() throws Exception {
      mockMvc.perform(post(TELEGRAM_CONFIG)
              .with(csrf())
          )
          .andExpect(status().isOk());
    }

    @Test
    @DisplayName("User is allowed to DELETE on endpoint " + TELEGRAM_CONFIG + "'")
    @WithMockUser
    void user_is_allowed_to_delete_telegram_config() throws Exception {
      mockMvc.perform(delete(TELEGRAM_CONFIG)
              .with(csrf())
          )
          .andExpect(status().isOk());
    }
  }

  @Nested
  class AnonymousUserTest {

    @Test
    @DisplayName("Anonymous user is not allowed to POST on endpoint " + TELEGRAM_CONFIG + "'")
    @WithAnonymousUser
    void anonymous_user_is_not_allowed_to_generate_registration_id() throws Exception {
      mockMvc.perform(post(TELEGRAM_CONFIG)
              .with(csrf())
          )
          .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Anonymous user is not allowed to DELETE on endpoint " + TELEGRAM_CONFIG + "'")
    @WithAnonymousUser
    void anonymous_user_is_not_allowed_to_delete_telegram_config() throws Exception {
      mockMvc.perform(delete(TELEGRAM_CONFIG)
              .with(csrf())
          )
          .andExpect(status().isUnauthorized());
    }
  }
}
