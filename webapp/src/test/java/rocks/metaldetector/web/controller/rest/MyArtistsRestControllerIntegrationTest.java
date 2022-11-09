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
import rocks.metaldetector.testutil.BaseSpringBootTest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static rocks.metaldetector.support.Endpoints.Rest.MY_ARTISTS;

@SpringBootTest
@AutoConfigureMockMvc
public class MyArtistsRestControllerIntegrationTest extends BaseSpringBootTest {

  @Autowired
  private MockMvc mockMvc;

  @Nested
  class AdministratorRoleTest {

    @Test
    @DisplayName("Administrator is allowed to GET on endpoint " + MY_ARTISTS + "'")
    @WithMockUser(roles = {"ADMINISTRATOR"})
    void administrator_is_allowed_to_fetch_followed_artists() throws Exception {
      mockMvc.perform(get(MY_ARTISTS))
          .andExpect(status().isOk());
    }
  }

  @Nested
  class UserRoleTest {

    @Test
    @DisplayName("User is allowed to GET on endpoint " + MY_ARTISTS + "'")
    @WithMockUser
    void user_is_allowed_to_fetch_followed_artists() throws Exception {
      mockMvc.perform(get(MY_ARTISTS))
          .andExpect(status().isOk());
    }
  }

  @Nested
  class AnonymousUserTest {

    @Test
    @DisplayName("Anonymous user is not allowed to GET on endpoint " + MY_ARTISTS + "'")
    @WithAnonymousUser
    void anonymous_user_is_not_allowed_to_fetch_followed_artists() throws Exception {
      mockMvc.perform(get(MY_ARTISTS))
          .andExpect(status().isUnauthorized());
    }
  }
}
