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
import rocks.metaldetector.service.user.UserDto;
import rocks.metaldetector.service.user.UserService;
import rocks.metaldetector.testutil.BaseSpringBootTest;
import rocks.metaldetector.testutil.DtoFactory.UpdateEmailRequestFactory;
import rocks.metaldetector.testutil.DtoFactory.UpdatePasswordRequestFactory;
import rocks.metaldetector.web.transformer.UserDtoTransformer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static rocks.metaldetector.support.Endpoints.Rest.CURRENT_USER;
import static rocks.metaldetector.support.Endpoints.Rest.CURRENT_USER_EMAIL;
import static rocks.metaldetector.support.Endpoints.Rest.CURRENT_USER_PASSWORD;

@SpringBootTest
@AutoConfigureMockMvc
public class UserAccountRestControllerIntegrationTest extends BaseSpringBootTest {

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
    @DisplayName("Administrator is allowed to GET on endpoint " + CURRENT_USER + "")
    @WithMockUser(roles = {"ADMINISTRATOR"})
    void administrator_is_allowed_to_fetch_current_user_information() throws Exception {
      mockMvc.perform(get(CURRENT_USER))
          .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Administrator is allowed to PATCH on endpoint " + CURRENT_USER_EMAIL + "'")
    @WithMockUser(roles = {"ADMINISTRATOR"})
    void administrator_is_allowed_to_update_current_email() throws Exception {
      doReturn(mock(UserDto.class)).when(userService).updateCurrentEmail(any());

      mockMvc.perform(patch(CURRENT_USER_EMAIL)
              .with(csrf())
              .content(objectMapper.writeValueAsString(UpdateEmailRequestFactory.createDefault()))
              .contentType(APPLICATION_JSON)
          )
          .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Administrator is allowed to PATCH on endpoint " + CURRENT_USER_PASSWORD + "'")
    @WithMockUser(roles = {"ADMINISTRATOR"})
    void administrator_is_allowed_to_update_current_password() throws Exception {
      mockMvc.perform(patch(CURRENT_USER_PASSWORD)
              .with(csrf())
              .content(objectMapper.writeValueAsString(UpdatePasswordRequestFactory.createDefault()))
              .contentType(APPLICATION_JSON)
          )
          .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Administrator is allowed to DELETE on endpoint " + CURRENT_USER + "'")
    @WithMockUser(roles = {"ADMINISTRATOR"})
    void administrator_is_allowed_to_remove_own_account() throws Exception {
      mockMvc.perform(delete(CURRENT_USER)
              .with(csrf())
          )
          .andExpect(status().isOk());
    }
  }

  @Nested
  class UserRoleTest {

    @Test
    @DisplayName("User is allowed to GET on endpoint " + CURRENT_USER + "")
    @WithMockUser
    void user_is_allowed_to_fetch_current_user_information() throws Exception {
      mockMvc.perform(get(CURRENT_USER))
          .andExpect(status().isOk());
    }

    @Test
    @DisplayName("User is allowed to PATCH on endpoint " + CURRENT_USER_EMAIL + "'")
    @WithMockUser
    void user_is_allowed_to_update_current_email() throws Exception {
      doReturn(mock(UserDto.class)).when(userService).updateCurrentEmail(any());

      mockMvc.perform(patch(CURRENT_USER_EMAIL)
              .with(csrf())
              .content(objectMapper.writeValueAsString(UpdateEmailRequestFactory.createDefault()))
              .contentType(APPLICATION_JSON)
          )
          .andExpect(status().isOk());
    }

    @Test
    @DisplayName("User is allowed to PATCH on endpoint " + CURRENT_USER_PASSWORD + "'")
    @WithMockUser
    void user_is_allowed_to_update_current_password() throws Exception {
      mockMvc.perform(patch(CURRENT_USER_PASSWORD)
              .with(csrf())
              .content(objectMapper.writeValueAsString(UpdatePasswordRequestFactory.createDefault()))
              .contentType(APPLICATION_JSON)
          )
          .andExpect(status().isOk());
    }

    @Test
    @DisplayName("User is allowed to DELETE on endpoint " + CURRENT_USER + "'")
    @WithMockUser
    void user_is_allowed_to_remove_own_account() throws Exception {
      mockMvc.perform(delete(CURRENT_USER)
              .with(csrf())
          )
          .andExpect(status().isOk());
    }
  }

  @Nested
  class AnonymousUserTest {

    @Test
    @DisplayName("Anonymous user is not allowed to GET on endpoint " + CURRENT_USER + "")
    @WithAnonymousUser
    void anonymous_user_is_not_allowed_to_fetch_current_user_information() throws Exception {
      mockMvc.perform(get(CURRENT_USER))
          .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Anonymous user is not allowed to PATCH on endpoint " + CURRENT_USER_EMAIL + "'")
    @WithAnonymousUser
    void anonymous_user_is_not_allowed_to_update_current_email() throws Exception {
      doReturn(mock(UserDto.class)).when(userService).updateCurrentEmail(any());

      mockMvc.perform(patch(CURRENT_USER_EMAIL)
              .with(csrf())
              .content(objectMapper.writeValueAsString(UpdateEmailRequestFactory.createDefault()))
              .contentType(APPLICATION_JSON)
          )
          .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Anonymous user is not allowed to PATCH on endpoint " + CURRENT_USER_PASSWORD + "'")
    @WithAnonymousUser
    void anonymous_user_is_not_allowed_to_update_current_password() throws Exception {
      mockMvc.perform(patch(CURRENT_USER_PASSWORD)
              .with(csrf())
              .content(objectMapper.writeValueAsString(UpdatePasswordRequestFactory.createDefault()))
              .contentType(APPLICATION_JSON)
          )
          .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Anonymous user is not allowed to DELETE on endpoint " + CURRENT_USER + "'")
    @WithAnonymousUser
    void anonymous_user_is_not_allowed_to_remove_own_account() throws Exception {
      mockMvc.perform(delete(CURRENT_USER)
              .with(csrf())
          )
          .andExpect(status().isUnauthorized());
    }
  }
}
