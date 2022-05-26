package rocks.metaldetector.web.controller.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import rocks.metaldetector.security.SecurityConfig;
import rocks.metaldetector.service.telegram.TelegramUpdateFactory;
import rocks.metaldetector.service.telegram.TelegramUpdateService;
import rocks.metaldetector.testutil.BaseWebMvcTestWithSecurity;
import rocks.metaldetector.web.api.request.TelegramUpdate;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static rocks.metaldetector.support.Endpoints.Rest.NOTIFICATION_TELEGRAM;

@WebMvcTest(controllers = TelegramRestController.class,
    properties = "telegram.bot-id=100")
@Import({SecurityConfig.class})
class TelegramRestControllerIntegrationTest extends BaseWebMvcTestWithSecurity {

  @MockBean
  @SuppressWarnings("unused")
  private TelegramUpdateService telegramUpdateService;

  private final TelegramUpdate update = TelegramUpdateFactory.createDefault();
  private final String botId = "100";

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  @DisplayName("Administrator is allowed to POST on endpoint " + NOTIFICATION_TELEGRAM + "'")
  @WithMockUser(roles = "ADMINISTRATOR")
  void admin_is_allowed_to_call() throws Exception {
    mockMvc.perform(post(NOTIFICATION_TELEGRAM + "/" + botId)
                        .content(objectMapper.writeValueAsString(update))
                        .contentType(APPLICATION_JSON))
        .andExpect(status().isOk());
  }

  @Test
  @DisplayName("User is allowed to POST on endpoint " + NOTIFICATION_TELEGRAM + "'")
  @WithMockUser(roles = "USER")
  void user_is_allowed_to_call() throws Exception {
    mockMvc.perform(post(NOTIFICATION_TELEGRAM + "/" + botId)
                        .content(objectMapper.writeValueAsString(update))
                        .contentType(APPLICATION_JSON))
        .andExpect(status().isOk());
  }

  @Test
  @DisplayName("Anonymous is allowed to POST on endpoint " + NOTIFICATION_TELEGRAM + "'")
  @WithAnonymousUser
  void anonymous_is_allowed_to_call() throws Exception {
    mockMvc.perform(post(NOTIFICATION_TELEGRAM + "/" + botId)
                        .content(objectMapper.writeValueAsString(update))
                        .contentType(APPLICATION_JSON))
        .andExpect(status().isOk());
  }
}
