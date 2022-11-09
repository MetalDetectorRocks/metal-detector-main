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
import rocks.metaldetector.service.artist.FollowArtistService;
import rocks.metaldetector.testutil.WithIntegrationTestConfig;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static rocks.metaldetector.persistence.domain.artist.ArtistSource.SPOTIFY;
import static rocks.metaldetector.support.Endpoints.Rest.FOLLOW_ARTIST;
import static rocks.metaldetector.support.Endpoints.Rest.SEARCH_ARTIST;
import static rocks.metaldetector.support.Endpoints.Rest.TOP_ARTISTS;
import static rocks.metaldetector.support.Endpoints.Rest.UNFOLLOW_ARTIST;

@SpringBootTest
@AutoConfigureMockMvc
class ArtistsRestControllerIntegrationTest implements WithIntegrationTestConfig {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  @SuppressWarnings("unused")
  private FollowArtistService followArtistService;

  @Nested
  class AdministratorRoleTest {

    @Test
    @DisplayName("Administrator user is allowed to GET on endpoint " + TOP_ARTISTS + "'")
    @WithMockUser(roles = {"ADMINISTRATOR"})
    void administrator_is_allowed_to_get_top_artists() throws Exception {
      mockMvc.perform(get(TOP_ARTISTS))
          .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Administrator is allowed to GET on endpoint " + SEARCH_ARTIST + "'")
    @WithMockUser(roles = {"ADMINISTRATOR"})
    void administrator_is_allowed_to_search_artists() throws Exception {
      mockMvc.perform(get(SEARCH_ARTIST))
          .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Administrator is allowed to POST on endpoint " + FOLLOW_ARTIST + "'")
    @WithMockUser(roles = {"ADMINISTRATOR"})
    void administrator_is_allowed_to_follow_artists() throws Exception {
      mockMvc.perform(post(FOLLOW_ARTIST + "/{source}/{id}", SPOTIFY, "123")
              .with(csrf())
              .contentType(APPLICATION_JSON))
          .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Administrator is allowed to POST on endpoint " + UNFOLLOW_ARTIST + "'")
    @WithMockUser(roles = {"ADMINISTRATOR"})
    void administrator_is_allowed_to_unfollow_artists() throws Exception {
      mockMvc.perform(post(UNFOLLOW_ARTIST + "/{source}/{id}", SPOTIFY, "123")
              .with(csrf())
              .contentType(APPLICATION_JSON))
          .andExpect(status().isOk());
    }
  }

  @Nested
  class UserRoleTest {

    @Test
    @DisplayName("User user is allowed to GET on endpoint " + TOP_ARTISTS + "'")
    @WithMockUser
    void user_is_allowed_to_get_top_artists() throws Exception {
      mockMvc.perform(get(TOP_ARTISTS))
          .andExpect(status().isOk());
    }

    @Test
    @DisplayName("User is allowed to GET on endpoint " + SEARCH_ARTIST + "'")
    @WithMockUser
    void user_is_allowed_to_search_artists() throws Exception {
      mockMvc.perform(get(SEARCH_ARTIST))
          .andExpect(status().isOk());
    }

    @Test
    @DisplayName("User is allowed to POST on endpoint " + FOLLOW_ARTIST + "'")
    @WithMockUser
    void user_is_allowed_to_follow_artists() throws Exception {
      mockMvc.perform(post(FOLLOW_ARTIST + "/{source}/{id}", SPOTIFY, "123")
              .with(csrf())
              .contentType(APPLICATION_JSON))
          .andExpect(status().isOk());
    }

    @Test
    @DisplayName("User is allowed to POST on endpoint " + UNFOLLOW_ARTIST + "'")
    @WithMockUser
    void user_is_allowed_to_unfollow_artists() throws Exception {
      mockMvc.perform(post(UNFOLLOW_ARTIST + "/{source}/{id}", SPOTIFY, "123")
              .with(csrf())
              .contentType(APPLICATION_JSON))
          .andExpect(status().isOk());
    }
  }

  @Nested
  class AnonymousUserTest {

    @Test
    @DisplayName("Anonymous user is allowed to GET on endpoint " + TOP_ARTISTS + "'")
    @WithAnonymousUser
    void anonymous_user_is_allowed_to_get_top_artists() throws Exception {
      mockMvc.perform(get(TOP_ARTISTS))
          .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Anonymous user is allowed to GET on endpoint " + SEARCH_ARTIST + "'")
    @WithAnonymousUser
    void anonymous_user_is_allowed_to_search_artists() throws Exception {
      mockMvc.perform(get(SEARCH_ARTIST))
          .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Anonymous user is not allowed to POST on endpoint " + FOLLOW_ARTIST + "'")
    @WithAnonymousUser
    void anonymous_user_is_not_allowed_to_follow_artists() throws Exception {
      mockMvc.perform(post(FOLLOW_ARTIST + "/{source}/{id}", SPOTIFY, "123")
              .with(csrf())
              .contentType(APPLICATION_JSON))
          .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Anonymous user is not allowed to POST on endpoint " + UNFOLLOW_ARTIST + "'")
    @WithAnonymousUser
    void anonymous_user_not_is_allowed_to_unfollow_artists() throws Exception {
      mockMvc.perform(post(UNFOLLOW_ARTIST + "/{source}/{id}", SPOTIFY, "123")
              .with(csrf())
              .contentType(APPLICATION_JSON))
          .andExpect(status().isUnauthorized());
    }
  }
}
