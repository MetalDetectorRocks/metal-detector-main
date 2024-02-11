package rocks.metaldetector.web.controller.rest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import rocks.metaldetector.testutil.BaseSpringBootTest;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static rocks.metaldetector.support.Endpoints.Rest.OAUTH_BASE;

@SpringBootTest
@AutoConfigureMockMvc
public class OAuth2AuthorizationRestControllerIntegrationTest extends BaseSpringBootTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  @SuppressWarnings("unused")
  private OAuth2AuthorizedClientService oAuth2AuthorizedClientService;

  @Nested
  class AdministratorRoleTest {

    @Test
    @DisplayName("Administrator is allowed to GET on endpoint " + OAUTH_BASE + "'")
    @WithMockUser(roles = {"ADMINISTRATOR"})
    void administrator_is_allowed_to_check_authorization() throws Exception {
      mockMvc.perform(get(OAUTH_BASE + "/{id}", "foo"))
          .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Administrator is allowed to DELETE on endpoint " + OAUTH_BASE + "'")
    @WithMockUser(roles = {"ADMINISTRATOR"})
    void administrator_is_allowed_to_delete_authorization() throws Exception {
      mockMvc.perform(delete(OAUTH_BASE + "/{id}", "foo")
              .with(csrf())
          )
          .andExpect(status().isOk());
    }
  }

  @Nested
  class UserRoleTest {

    @Test
    @DisplayName("User is allowed to GET on endpoint " + OAUTH_BASE + "'")
    @WithMockUser
    void user_is_allowed_to_check_authorization() throws Exception {
      mockMvc.perform(get(OAUTH_BASE + "/{id}", "foo"))
          .andExpect(status().isOk());
    }

    @Test
    @DisplayName("User is allowed to DELETE on endpoint " + OAUTH_BASE + "'")
    @WithMockUser
    void user_is_allowed_to_delete_authorization() throws Exception {
      mockMvc.perform(delete(OAUTH_BASE + "/{id}", "foo")
              .with(csrf())
          )
          .andExpect(status().isOk());
    }
  }

  @Nested
  class AnonymousUserTest {

    @Test
    @DisplayName("Anonymous user is not allowed to GET on endpoint " + OAUTH_BASE + "'")
    @WithAnonymousUser
    void anonymous_user_is_not_allowed_to_check_authorization() throws Exception {
      mockMvc.perform(get(OAUTH_BASE + "/{id}", "foo"))
          .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Anonymous user is not allowed to DELETE on endpoint " + OAUTH_BASE + "'")
    @WithAnonymousUser
    void anonymous_user_is_not_allowed_to_delete_authorization() throws Exception {
      mockMvc.perform(delete(OAUTH_BASE + "/{id}", "foo")
              .with(csrf())
          )
          .andExpect(status().isUnauthorized());
    }
  }
}
