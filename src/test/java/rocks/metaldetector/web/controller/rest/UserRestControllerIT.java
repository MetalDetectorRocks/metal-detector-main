package rocks.metaldetector.web.controller.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import rocks.metaldetector.testutil.BaseWebMvcTestWithSecurity;
import rocks.metaldetector.web.DtoFactory.RegisterUserRequestFactory;
import rocks.metaldetector.web.DtoFactory.UpdateUserRequestFactory;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static rocks.metaldetector.config.constants.Endpoints.Rest.USERS;

@WebMvcTest(controllers = UserRestController.class)
class UserRestControllerIT extends BaseWebMvcTestWithSecurity {

  @Autowired
  MockMvc mockMvc;

  @SpyBean
  ObjectMapper objectMapper;

  @MockBean
  ModelMapper mapper;

  @Nested
  @DisplayName("Administrators should have access to all endpoints")
  class AdministratorRoleTest {

    @Test
    @DisplayName("Administrators can GET on endpoint " + USERS)
    @WithMockUser(roles = "ADMINISTRATOR")
    void admin_can_get_all_users() throws Exception {
      mockMvc.perform(get(USERS))
             .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Administrators can GET on endpoint " + USERS + "/{id}")
    @WithMockUser(roles = "ADMINISTRATOR")
    void admin_can_get_specified_user() throws Exception {
      mockMvc.perform(get(USERS + "/{id}", "123"))
             .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Administrators can POST on endpoint " + USERS)
    @WithMockUser(roles = "ADMINISTRATOR")
    void admin_can_create_user_via_post() throws Exception {
      mockMvc.perform(post(USERS)
             .content(objectMapper.writeValueAsString(RegisterUserRequestFactory.createDefault()))
             .contentType(APPLICATION_JSON))
             .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Administrators can PUT on endpoint " + USERS)
    @WithMockUser(roles = "ADMINISTRATOR")
    void admin_can_update_user_via_put() throws Exception {
      mockMvc.perform(put(USERS)
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
    @WithMockUser(roles = "USER")
    void users_cannot_get_all_users() throws Exception {
      mockMvc.perform(get(USERS))
             .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Users cannot GET on endpoint " + USERS + "/{id}")
    @WithMockUser(roles = "USER")
    void users_can_get_specified_user() throws Exception {
      mockMvc.perform(get(USERS + "/{id}", "123"))
             .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Users cannot POST on endpoint " + USERS)
    @WithMockUser(roles = "USER")
    void users_can_create_user_via_post() throws Exception {
      mockMvc.perform(post(USERS)
             .content(objectMapper.writeValueAsString(RegisterUserRequestFactory.createDefault()))
             .contentType(APPLICATION_JSON))
             .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Users cannot PUT on endpoint " + USERS)
    @WithMockUser(roles = "USER")
    void users_can_update_user_via_put() throws Exception {
      mockMvc.perform(put(USERS)
             .content(objectMapper.writeValueAsString(UpdateUserRequestFactory.createDefault()))
             .contentType(APPLICATION_JSON))
             .andExpect(status().isForbidden());
    }
  }
}
