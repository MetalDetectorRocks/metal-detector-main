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
import rocks.metaldetector.butler.facade.JobService;
import rocks.metaldetector.testutil.WithIntegrationTestConfig;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static rocks.metaldetector.support.Endpoints.Rest.COVER_JOB;
import static rocks.metaldetector.support.Endpoints.Rest.IMPORT_JOB;

@SpringBootTest
@AutoConfigureMockMvc
public class JobRestControllerIntegrationTest implements WithIntegrationTestConfig {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  @SuppressWarnings("unused")
  private JobService jobService;

  @Nested
  @DisplayName("Administrator is allowed to send requests to all endpoints")
  class AdministratorRoleTest {

    @Test
    @DisplayName("Administrator is allowed to POST on endpoint " + IMPORT_JOB + "'")
    @WithMockUser(roles = {"ADMINISTRATOR"})
    void admin_is_allowed_to_create_import_job() throws Exception {
      mockMvc.perform(post(IMPORT_JOB)
                          .with(csrf()))
          .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Administrator is allowed to GET on endpoint " + IMPORT_JOB + "'")
    @WithMockUser(roles = {"ADMINISTRATOR"})
    void admin_is_allowed_to_query_import_job_results() throws Exception {
      mockMvc.perform(get(IMPORT_JOB)
                          .contentType(APPLICATION_JSON))
          .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Administrator is allowed to GET on endpoint " + COVER_JOB + "'")
    @WithMockUser(roles = {"ADMINISTRATOR"})
    void admin_is_allowed_to_create_retry_cover_download_job() throws Exception {
      mockMvc.perform(post(COVER_JOB)
                          .with(csrf())
                          .contentType(APPLICATION_JSON))
          .andExpect(status().isOk());
    }
  }

  @Nested
  @DisplayName("Users is not allowed to send requests to all endpoints")
  class UserRoleTest {

    @Test
    @DisplayName("User is not allowed to POST on endpoint " + IMPORT_JOB + "'")
    @WithMockUser
    void user_is_not_allowed_to_create_import_job() throws Exception {
      mockMvc.perform(post(IMPORT_JOB).with(csrf()))
          .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("User is not allowed to GET on endpoint " + IMPORT_JOB + "'")
    @WithMockUser
    void user_is_not_allowed_to_query_import_job_results() throws Exception {
      mockMvc.perform(get(IMPORT_JOB)
                          .contentType(APPLICATION_JSON))
          .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("User is not allowed to GET on endpoint " + COVER_JOB + "'")
    @WithMockUser
    void user_not_is_allowed_to_create_retry_cover_download_job() throws Exception {
      mockMvc.perform(post(COVER_JOB)
                          .with(csrf())
                          .contentType(APPLICATION_JSON))
          .andExpect(status().isForbidden());
    }
  }

  @Nested
  @DisplayName("Anonymous user is not allowed to send requests to all endpoints")
  class AnonymousUserTest {

    @Test
    @DisplayName("Anonymous user is not allowed to POST on endpoint " + IMPORT_JOB + "'")
    @WithAnonymousUser
    void anonymous_user_is_not_allowed_to_create_import_job() throws Exception {
      mockMvc.perform(post(IMPORT_JOB).with(csrf()))
          .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Anonymous user is not allowed to GET on endpoint " + IMPORT_JOB + "'")
    @WithAnonymousUser
    void anonymous_user_is_not_allowed_to_query_import_job_results() throws Exception {
      mockMvc.perform(get(IMPORT_JOB)
              .contentType(APPLICATION_JSON))
          .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Anonymous user is not allowed to GET on endpoint " + COVER_JOB + "'")
    @WithAnonymousUser
    void anonymous_user_not_is_allowed_to_create_retry_cover_download_job() throws Exception {
      mockMvc.perform(post(COVER_JOB)
              .with(csrf())
              .contentType(APPLICATION_JSON))
          .andExpect(status().isUnauthorized());
    }
  }
}
