package rocks.metaldetector.web.controller.rest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import rocks.metaldetector.security.SecurityConfig;
import rocks.metaldetector.service.artist.ArtistSearchService;
import rocks.metaldetector.service.artist.FollowArtistService;
import rocks.metaldetector.service.dashboard.ArtistCollector;
import rocks.metaldetector.testutil.BaseWebMvcTestWithSecurity;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static rocks.metaldetector.support.Endpoints.Rest.TOP_ARTISTS;

@WebMvcTest(controllers = ArtistsRestController.class)
@Import({SecurityConfig.class})
class ArtistsRestControllerIntegrationTest extends BaseWebMvcTestWithSecurity {

  @MockitoBean
  @SuppressWarnings("unused")
  private ArtistSearchService artistSearchService;

  @MockitoBean
  @SuppressWarnings("unused")
  private FollowArtistService followArtistService;

  @MockitoBean
  @SuppressWarnings("unused")
  private ArtistCollector artistCollector;

  @Test
  @DisplayName("Anonymous user is allowed to GET on endpoint " + TOP_ARTISTS + "'")
  @WithAnonymousUser
  void anonymous_user_is_allowed_to_get_top_artists() throws Exception {
    mockMvc.perform(get(TOP_ARTISTS))
        .andExpect(status().isOk());
  }
}
