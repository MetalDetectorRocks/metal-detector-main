package rocks.metaldetector.web.controller.rest;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.HttpStatus;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import rocks.metaldetector.config.constants.Endpoints;
import rocks.metaldetector.testutil.BaseWebMvcTestWithSecurity;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@WebMvcTest(controllers = UserRestController.class)
class UserRestControllerIT extends BaseWebMvcTestWithSecurity implements WithAssertions {

  @Autowired
  MockMvc mockMvc;

  @SpyBean
  ModelMapper mapper;

  @Test
  @DisplayName("Administrators can call endpoint " + Endpoints.Rest.USERS)
  @WithMockUser(roles = "ADMINISTRATOR")
  void admin_can_get_all_users() throws Exception {
    // when
    MvcResult result = mockMvc.perform(get(Endpoints.Rest.USERS)).andReturn();

    // then
    assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.OK.value());
  }

  @Test
  @DisplayName("Users cannot call endpoint " + Endpoints.Rest.USERS)
  @WithMockUser(roles = "USER")
  void users_cannot_get_all_users() throws Exception {
    //when
    MvcResult result = mockMvc.perform(get(Endpoints.Rest.USERS)).andReturn();

    // then
    assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.FORBIDDEN.value());
  }
}
