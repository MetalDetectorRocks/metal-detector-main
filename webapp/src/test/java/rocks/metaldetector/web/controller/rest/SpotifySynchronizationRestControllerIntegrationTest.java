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
import rocks.metaldetector.service.spotify.SpotifySynchronizationService;
import rocks.metaldetector.testutil.BaseSpringBootTest;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static rocks.metaldetector.support.Endpoints.Rest.SPOTIFY_ARTIST_SYNCHRONIZATION;

@SpringBootTest
@AutoConfigureMockMvc
public class SpotifySynchronizationRestControllerIntegrationTest extends BaseSpringBootTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  @SuppressWarnings("unused")
  private SpotifySynchronizationService spotifySynchronizationService;

  @Nested
  class AdministratorRoleTest {

    @Test
    @DisplayName("Administrator is allowed to POST on endpoint " + SPOTIFY_ARTIST_SYNCHRONIZATION + "'")
    @WithMockUser(roles = {"ADMINISTRATOR"})
    void administrator_is_allowed_to_synchronize_spotify_artists() throws Exception {
      mockMvc.perform(post(SPOTIFY_ARTIST_SYNCHRONIZATION)
              .with(csrf())
              .contentType(APPLICATION_JSON)
          )
          .andExpect(status().isOk());
    }
  }

  @Nested
  class UserRoleTest {

    @Test
    @DisplayName("User is allowed to POST on endpoint " + SPOTIFY_ARTIST_SYNCHRONIZATION + "'")
    @WithMockUser
    void user_is_allowed_to_synchronize_spotify_artists() throws Exception {
      mockMvc.perform(post(SPOTIFY_ARTIST_SYNCHRONIZATION)
              .with(csrf())
              .contentType(APPLICATION_JSON)
          )
          .andExpect(status().isOk());
    }
  }

  @Nested
  class AnonymousUserTest {

    @Test
    @DisplayName("Anonymous user is not allowed to POST on endpoint " + SPOTIFY_ARTIST_SYNCHRONIZATION + "'")
    @WithAnonymousUser
    void anonymous_user_is_not_allowed_to_synchronize_spotify_artists() throws Exception {
      mockMvc.perform(post(SPOTIFY_ARTIST_SYNCHRONIZATION)
              .with(csrf())
              .contentType(APPLICATION_JSON)
          )
          .andExpect(status().isUnauthorized());
    }
  }
}
