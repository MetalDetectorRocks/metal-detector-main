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
import rocks.metaldetector.testutil.BaseSpringBootTest;
import rocks.metaldetector.testutil.DtoFactory.RegisterUserRequestFactory;
import rocks.metaldetector.testutil.DtoFactory.UpdateUserRequestFactory;
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
class UserRestControllerIntegrationTest extends BaseSpringBootTest {

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
  @DisplayName("Administrators should have access to all endpoints")
  class AdministratorRoleTest {

    @Test
    @DisplayName("Administrators can GET on endpoint " + USERS)
    @WithMockUser(roles = {"ADMINISTRATOR"})
    void admin_can_get_all_users() throws Exception {
      mockMvc.perform(get(USERS))
          .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Administrators can GET on endpoint " + USERS + "/{id}")
    @WithMockUser(roles = {"ADMINISTRATOR"})
    void admin_can_get_specified_user() throws Exception {
      mockMvc.perform(get(USERS + "/{id}", "123"))
          .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Administrators can POST on endpoint " + USERS)
    @WithMockUser(roles = {"ADMINISTRATOR"})
    void admin_can_create_user_via_post() throws Exception {
      mockMvc.perform(post(USERS)
                          .with(csrf())
                          .content(objectMapper.writeValueAsString(RegisterUserRequestFactory.createDefault()))
                          .contentType(APPLICATION_JSON))
          .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Administrators can PUT on endpoint " + USERS)
    @WithMockUser(roles = {"ADMINISTRATOR"})
    void admin_can_update_user_via_put() throws Exception {
      mockMvc.perform(put(USERS)
                          .with(csrf())
                          .content(objectMapper.writeValueAsString(UpdateUserRequestFactory.createDefault()))
                          .contentType(APPLICATION_JSON))
          .andExpect(status().isOk());
    }
  }

  @Nested
  @DisplayName("Users should have no access to all endpoints")
  class UserRoleTest {

    @Test
    @DisplayName("Users cannot GET on endpoint " + USERS)
    @WithMockUser
    void users_cannot_get_all_users() throws Exception {
      mockMvc.perform(get(USERS))
          .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Users cannot GET on endpoint " + USERS + "/{id}")
    @WithMockUser
    void users_cannot_get_specified_user() throws Exception {
      mockMvc.perform(get(USERS + "/{id}", "123"))
          .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Users cannot POST on endpoint " + USERS)
    @WithMockUser
    void users_cannot_create_user_via_post() throws Exception {
      mockMvc.perform(post(USERS)
                          .with(csrf())
                          .content(objectMapper.writeValueAsString(RegisterUserRequestFactory.createDefault()))
                          .contentType(APPLICATION_JSON))
          .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Users cannot PUT on endpoint " + USERS)
    @WithMockUser
    void users_cannot_update_user_via_put() throws Exception {
      mockMvc.perform(put(USERS)
                          .with(csrf())
                          .content(objectMapper.writeValueAsString(UpdateUserRequestFactory.createDefault()))
                          .contentType(APPLICATION_JSON))
          .andExpect(status().isForbidden());
    }
  }

  @Nested
  @DisplayName("Anonymous user should have no access to all endpoints")
  class AnonymousUserTest {

    @Test
    @DisplayName("Anonymous user cannot GET on endpoint " + USERS)
    @WithAnonymousUser
    void anonymous_user_cannot_get_all_users() throws Exception {
      mockMvc.perform(get(USERS))
          .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Anonymous user cannot GET on endpoint " + USERS + "/{id}")
    @WithAnonymousUser
    void anonymous_user_can_get_specified_user() throws Exception {
      mockMvc.perform(get(USERS + "/{id}", "123"))
          .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Anonymous user cannot POST on endpoint " + USERS)
    @WithAnonymousUser
    void anonymous_user_can_create_user_via_post() throws Exception {
      mockMvc.perform(post(USERS)
              .with(csrf())
              .content(objectMapper.writeValueAsString(RegisterUserRequestFactory.createDefault()))
              .contentType(APPLICATION_JSON))
          .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Anonymous user cannot PUT on endpoint " + USERS)
    @WithAnonymousUser
    void anonymous_user_can_update_user_via_put() throws Exception {
      mockMvc.perform(put(USERS)
              .with(csrf())
              .content(objectMapper.writeValueAsString(UpdateUserRequestFactory.createDefault()))
              .contentType(APPLICATION_JSON))
          .andExpect(status().isUnauthorized());
    }
  }
}
