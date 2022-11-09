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
import rocks.metaldetector.service.user.UserService;
import rocks.metaldetector.testutil.DtoFactory.RegisterUserRequestFactory;
import rocks.metaldetector.testutil.DtoFactory.UpdateUserRequestFactory;
import rocks.metaldetector.testutil.WithIntegrationTestConfig;
import rocks.metaldetector.web.transformer.UserDtoTransformer;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static rocks.metaldetector.support.Endpoints.Rest.USERS;

@SpringBootTest
@AutoConfigureMockMvc
public class UserAccountRestControllerIntegrationTest implements WithIntegrationTestConfig {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  @SuppressWarnings("unused")
  private UserService userService;

  @MockBean
  @SuppressWarnings("unused")
  private UserDtoTransformer userDtoTransformer;

  @Nested
  class AdministratorRoleTest {

    @Test
    @DisplayName("Administrator is allowed to GET on endpoint " + USERS + "' (all users)")
    @WithMockUser(roles = {"ADMINISTRATOR"})
    void administrator_is_allowed_to_fetch_all_users() throws Exception {
      mockMvc.perform(get(USERS))
          .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Administrator is allowed to GET on endpoint " + USERS + "' (certain user)")
    @WithMockUser(roles = {"ADMINISTRATOR"})
    void administrator_is_allowed_to_fetch_certain_user() throws Exception {
      mockMvc.perform(get(USERS + "/1"))
          .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Administrator is allowed to POST on endpoint " + USERS + "'")
    @WithMockUser(roles = {"ADMINISTRATOR"})
    void administrator_is_allowed_to_create_another_administrator() throws Exception {
      mockMvc.perform(post(USERS)
              .with(csrf())
              .content(objectMapper.writeValueAsString(RegisterUserRequestFactory.createDefault()))
              .contentType(APPLICATION_JSON)
          )
          .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Administrator is allowed to PUT on endpoint " + USERS + "'")
    @WithMockUser(roles = {"ADMINISTRATOR"})
    void administrator_is_allowed_to_update_certain_user() throws Exception {
      mockMvc.perform(put(USERS)
              .with(csrf())
              .content(objectMapper.writeValueAsString(UpdateUserRequestFactory.createDefault()))
              .contentType(APPLICATION_JSON)
          )
          .andExpect(status().isOk());
    }
  }

  @Nested
  class UserRoleTest {

    @Test
    @DisplayName("User is allowed to GET on endpoint " + USERS + "' (all users)")
    @WithMockUser
    void user_is_allowed_to_fetch_all_users() throws Exception {
      mockMvc.perform(get(USERS))
          .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("User is allowed to GET on endpoint " + USERS + "' (certain user)")
    @WithMockUser
    void user_is_allowed_to_fetch_certain_user() throws Exception {
      mockMvc.perform(get(USERS + "/1"))
          .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("User is allowed to POST on endpoint " + USERS + "'")
    @WithMockUser
    void user_is_allowed_to_create_another_administrator() throws Exception {
      mockMvc.perform(post(USERS)
              .with(csrf())
              .content(objectMapper.writeValueAsString(RegisterUserRequestFactory.createDefault()))
              .contentType(APPLICATION_JSON)
          )
          .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("User is allowed to PUT on endpoint " + USERS + "'")
    @WithMockUser
    void user_is_allowed_to_update_certain_user() throws Exception {
      mockMvc.perform(put(USERS)
              .with(csrf())
              .content(objectMapper.writeValueAsString(UpdateUserRequestFactory.createDefault()))
              .contentType(APPLICATION_JSON)
          )
          .andExpect(status().isForbidden());
    }
  }

  @Nested
  class AnonymousUserTest {

    @Test
    @DisplayName("Anonymous user is not allowed to GET on endpoint " + USERS + "' (all users)")
    @WithAnonymousUser
    void anonymous_user_is_not_allowed_to_fetch_all_users() throws Exception {
      mockMvc.perform(get(USERS))
          .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Anonymous user is not allowed to GET on endpoint " + USERS + "' (certain user)")
    @WithAnonymousUser
    void anonymous_user_is_not_allowed_to_fetch_certain_user() throws Exception {
      mockMvc.perform(get(USERS + "/1"))
          .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Anonymous user is not allowed to POST on endpoint " + USERS + "'")
    @WithAnonymousUser
    void anonymous_user_is_not_allowed_to_create_another_administrator() throws Exception {
      mockMvc.perform(post(USERS)
              .with(csrf())
              .content(objectMapper.writeValueAsString(RegisterUserRequestFactory.createDefault()))
              .contentType(APPLICATION_JSON)
          )
          .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Anonymous user is not allowed to PUT on endpoint " + USERS + "'")
    @WithAnonymousUser
    void anonymous_user_is_not_allowed_to_update_certain_user() throws Exception {
      mockMvc.perform(put(USERS)
              .with(csrf())
              .content(objectMapper.writeValueAsString(UpdateUserRequestFactory.createDefault()))
              .contentType(APPLICATION_JSON)
          )
          .andExpect(status().isUnauthorized());
    }
  }
}
