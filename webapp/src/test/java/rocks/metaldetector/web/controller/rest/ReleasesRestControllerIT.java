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
import rocks.metaldetector.testutil.DtoFactory.ReleaseRequestFactory;
import rocks.metaldetector.web.api.request.ReleaseUpdateRequest;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static rocks.metaldetector.support.Endpoints.Rest.COVER_JOB;
import static rocks.metaldetector.support.Endpoints.Rest.IMPORT_JOB;
import static rocks.metaldetector.support.Endpoints.Rest.QUERY_ALL_RELEASES;
import static rocks.metaldetector.support.Endpoints.Rest.QUERY_MY_RELEASES;
import static rocks.metaldetector.support.Endpoints.Rest.QUERY_RELEASES;
import static rocks.metaldetector.support.Endpoints.Rest.UPDATE_RELEASE_STATE;
import static rocks.metaldetector.testutil.DtoFactory.PaginatedReleaseRequestFactory;

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
    @DisplayName("Administrator is allowed to GET on endpoint " + QUERY_ALL_RELEASES + "'")
    @WithMockUser(roles = "ADMINISTRATOR")
    void admin_is_allowed_to_query_all_releases() throws Exception {
      mockMvc.perform(get(QUERY_ALL_RELEASES)
              .content(objectMapper.writeValueAsString(ReleaseRequestFactory.createDefault()))
              .contentType(APPLICATION_JSON))
              .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Administrator is allowed to GET on endpoint " + QUERY_RELEASES + "'")
    @WithMockUser(roles = "ADMINISTRATOR")
    void admin_is_allowed_to_query_releases() throws Exception {
      mockMvc.perform(get(QUERY_RELEASES)
              .content(objectMapper.writeValueAsString(PaginatedReleaseRequestFactory.createDefault()))
              .contentType(APPLICATION_JSON))
              .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Administrator is allowed to GET on endpoint " + QUERY_MY_RELEASES + "'")
    @WithMockUser(roles = "ADMINISTRATOR")
    void admin_is_allowed_to_query_my_releases() throws Exception {
      mockMvc.perform(get(QUERY_MY_RELEASES)
              .content(objectMapper.writeValueAsString(PaginatedReleaseRequestFactory.createDefault()))
              .contentType(APPLICATION_JSON))
              .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Administrator is allowed to POST on endpoint " + IMPORT_JOB + "'")
    @WithMockUser(roles = "ADMINISTRATOR")
    void admin_is_allowed_to_create_import_job() throws Exception {
      mockMvc.perform(post(IMPORT_JOB))
              .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Administrator is allowed to GET on endpoint " + IMPORT_JOB + "'")
    @WithMockUser(roles = "ADMINISTRATOR")
    void admin_is_allowed_to_query_import_job_results() throws Exception {
      mockMvc.perform(get(IMPORT_JOB)
              .contentType(APPLICATION_JSON))
              .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Administrator is allowed to GET on endpoint " + COVER_JOB + "'")
    @WithMockUser(roles = "ADMINISTRATOR")
    void admin_is_allowed_to_create_retry_cover_download_job() throws Exception {
      mockMvc.perform(post(COVER_JOB)
                          .contentType(APPLICATION_JSON))
          .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Administrator is allowed to PUT on endpoint " + UPDATE_RELEASE_STATE + "'")
    @WithMockUser(roles = "ADMINISTRATOR")
    void user_not_is_allowed_to_update_release_state() throws Exception {
      mockMvc.perform(put(UPDATE_RELEASE_STATE)
                          .content(objectMapper.writeValueAsString(ReleaseUpdateRequest.builder().releaseId(1L).state("state").build()))
                          .contentType(APPLICATION_JSON))
          .andExpect(status().isOk());
    }
  }

  @Nested
  @DisplayName("Users is not allowed to send requests to all endpoints")
  class UserRoleTest {

    @Test
    @DisplayName("User is not allowed to GET on endpoint " + QUERY_ALL_RELEASES + "'")
    @WithMockUser(roles = "USER")
    void user_is_not_allowed_to_query_all_releases() throws Exception {
      mockMvc.perform(get(QUERY_ALL_RELEASES)
              .content(objectMapper.writeValueAsString(PaginatedReleaseRequestFactory.createDefault()))
              .contentType(APPLICATION_JSON))
              .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("User is allowed to GET on endpoint " + QUERY_RELEASES + "'")
    @WithMockUser(roles = "USER")
    void user_is_allowed_to_query_releases() throws Exception {
      mockMvc.perform(get(QUERY_RELEASES)
              .content(objectMapper.writeValueAsString(PaginatedReleaseRequestFactory.createDefault()))
              .contentType(APPLICATION_JSON))
              .andExpect(status().isOk());
    }

    @Test
    @DisplayName("User is allowed to GET on endpoint " + QUERY_MY_RELEASES + "'")
    @WithMockUser(roles = "USER")
    void user_is_allowed_to_query_my_releases() throws Exception {
      mockMvc.perform(get(QUERY_MY_RELEASES)
              .content(objectMapper.writeValueAsString(PaginatedReleaseRequestFactory.createDefault()))
              .contentType(APPLICATION_JSON))
              .andExpect(status().isOk());
    }

    @Test
    @DisplayName("User is not allowed to POST on endpoint " + IMPORT_JOB + "'")
    @WithMockUser(roles = "USER")
    void user_is_not_allowed_to_create_import_job() throws Exception {
      mockMvc.perform(post(IMPORT_JOB))
              .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("User is not allowed to GET on endpoint " + IMPORT_JOB + "'")
    @WithMockUser(roles = "USER")
    void user_is_not_allowed_to_query_import_job_results() throws Exception {
      mockMvc.perform(get(IMPORT_JOB)
              .contentType(APPLICATION_JSON))
              .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("User is not allowed to GET on endpoint " + COVER_JOB + "'")
    @WithMockUser(roles = "USER")
    void user_not_is_allowed_to_create_retry_cover_download_job() throws Exception {
      mockMvc.perform(post(COVER_JOB)
              .contentType(APPLICATION_JSON))
              .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("User is not allowed to PUT on endpoint " + UPDATE_RELEASE_STATE + "'")
    @WithMockUser(roles = "USER")
    void user_not_is_allowed_to_update_release_state() throws Exception {
      mockMvc.perform(put(UPDATE_RELEASE_STATE)
                        .content(objectMapper.writeValueAsString(ReleaseUpdateRequest.builder().releaseId(1L).state("state").build()))
                        .contentType(APPLICATION_JSON))
          .andExpect(status().isForbidden());
    }
  }
}
