package rocks.metaldetector.web.controller.rest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import rocks.metaldetector.butler.facade.ReleaseService;
import rocks.metaldetector.service.artist.FollowArtistService;
import rocks.metaldetector.testutil.BaseWebMvcTestWithSecurity;
import rocks.metaldetector.web.api.request.ReleaseUpdateRequest;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static rocks.metaldetector.support.Endpoints.Rest.ALL_RELEASES;
import static rocks.metaldetector.support.Endpoints.Rest.MY_RELEASES;
import static rocks.metaldetector.support.Endpoints.Rest.RELEASES;

@WebMvcTest(controllers = ReleasesRestController.class)
public class ReleasesRestControllerIT extends BaseWebMvcTestWithSecurity {

  @MockBean
  private ReleaseService releaseService;

  @MockBean
  private FollowArtistService followArtistService;

  @Nested
  @DisplayName("Administrator is allowed to send requests to all endpoints")
  class AdministratorRoleTest {

    @Test
    @DisplayName("Administrator is allowed to GET on endpoint " + ALL_RELEASES + "'")
    @WithMockUser(roles = "ADMINISTRATOR")
    void admin_is_allowed_to_query_all_releases() throws Exception {
      mockMvc.perform(get(ALL_RELEASES)
              .contentType(APPLICATION_JSON))
              .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Administrator is allowed to GET on endpoint " + RELEASES + "'")
    @WithMockUser(roles = "ADMINISTRATOR")
    void admin_is_allowed_to_query_releases() throws Exception {
      mockMvc.perform(get(RELEASES).param("sort", "fieldName")
              .contentType(APPLICATION_JSON))
              .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Administrator is allowed to GET on endpoint " + MY_RELEASES + "'")
    @WithMockUser(roles = "ADMINISTRATOR")
    void admin_is_allowed_to_query_my_releases() throws Exception {
      mockMvc.perform(get(MY_RELEASES).param("sort", "fieldName")
              .contentType(APPLICATION_JSON))
              .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Administrator is allowed to PUT on endpoint " + RELEASES + "'")
    @WithMockUser(roles = "ADMINISTRATOR")
    void user_not_is_allowed_to_update_release_state() throws Exception {
      mockMvc.perform(put(RELEASES + "/1")
                          .content(objectMapper.writeValueAsString(ReleaseUpdateRequest.builder().state("state").build()))
                          .contentType(APPLICATION_JSON))
          .andExpect(status().isOk());
    }
  }

  @Nested
  @DisplayName("Users is not allowed to send requests to all endpoints")
  class UserRoleTest {

    @Test
    @DisplayName("User is not allowed to GET on endpoint " + ALL_RELEASES + "'")
    @WithMockUser(roles = "USER")
    void user_is_not_allowed_to_query_all_releases() throws Exception {
      mockMvc.perform(get(ALL_RELEASES)
              .contentType(APPLICATION_JSON))
              .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("User is allowed to GET on endpoint " + RELEASES + "'")
    @WithMockUser(roles = "USER")
    void user_is_allowed_to_query_releases() throws Exception {
      mockMvc.perform(get(RELEASES).param("sort", "fieldName")
              .contentType(APPLICATION_JSON))
              .andExpect(status().isOk());
    }

    @Test
    @DisplayName("User is allowed to GET on endpoint " + MY_RELEASES + "'")
    @WithMockUser(roles = "USER")
    void user_is_allowed_to_query_my_releases() throws Exception {
      mockMvc.perform(get(MY_RELEASES).param("sort", "fieldName")
              .contentType(APPLICATION_JSON))
              .andExpect(status().isOk());
    }

    @Test
    @DisplayName("User is not allowed to PUT on endpoint " + RELEASES + "'")
    @WithMockUser(roles = "USER")
    void user_not_is_allowed_to_update_release_state() throws Exception {
      mockMvc.perform(put(RELEASES + "/1")
                        .content(objectMapper.writeValueAsString(ReleaseUpdateRequest.builder().state("state").build()))
                        .contentType(APPLICATION_JSON))
          .andExpect(status().isForbidden());
    }
  }
}
