package rocks.metaldetector.web.controller.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import rocks.metaldetector.service.telegram.TelegramService;
import rocks.metaldetector.service.telegram.TelegramUpdateFactory;
import rocks.metaldetector.testutil.BaseSpringBootTest;
import rocks.metaldetector.web.api.request.TelegramUpdate;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static rocks.metaldetector.support.Endpoints.Rest.NOTIFICATION_TELEGRAM;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {"telegram.bot-id=abc"})
class TelegramRestControllerIntegrationTest extends BaseSpringBootTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  @SuppressWarnings("unused")
  private TelegramService telegramService;

  private final TelegramUpdate update = TelegramUpdateFactory.createDefault();
  private final String botId = "abc";

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  @DisplayName("Administrator is allowed to POST on endpoint " + NOTIFICATION_TELEGRAM + "'")
  @WithMockUser(roles = {"ADMINISTRATOR"})
  void admin_is_allowed_to_call() throws Exception {
    mockMvc.perform(post(NOTIFICATION_TELEGRAM + "/" + botId)
                        .with(csrf())
                        .content(objectMapper.writeValueAsString(update))
                        .contentType(APPLICATION_JSON))
        .andExpect(status().isOk());
  }

  @Test
  @DisplayName("User is allowed to POST on endpoint " + NOTIFICATION_TELEGRAM + "'")
  @WithMockUser
  void user_is_allowed_to_call() throws Exception {
    mockMvc.perform(post(NOTIFICATION_TELEGRAM + "/" + botId)
                        .with(csrf())
                        .content(objectMapper.writeValueAsString(update))
                        .contentType(APPLICATION_JSON))
        .andExpect(status().isOk());
  }

  @Test
  @DisplayName("Anonymous is allowed to POST on endpoint " + NOTIFICATION_TELEGRAM + "'")
  @WithAnonymousUser
  void anonymous_is_allowed_to_call() throws Exception {
    mockMvc.perform(post(NOTIFICATION_TELEGRAM + "/" + botId)
                        .with(csrf())
                        .content(objectMapper.writeValueAsString(update))
                        .contentType(APPLICATION_JSON))
        .andExpect(status().isOk());
  }
}
