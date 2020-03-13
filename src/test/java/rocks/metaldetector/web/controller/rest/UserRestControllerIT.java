package rocks.metaldetector.web.controller.rest;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import rocks.metaldetector.config.constants.Endpoints;
import rocks.metaldetector.testutil.BaseWebMvcTestWithSecurity;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserRestController.class)
class UserRestControllerIT extends BaseWebMvcTestWithSecurity implements WithAssertions {

  @Autowired
  MockMvc mockMvc;

  @SpyBean
  ModelMapper mapper;

  @Test
  @WithMockUser(roles = "ADMINISTRATOR")
  void test() throws Exception {
    mockMvc.perform(get(Endpoints.Rest.USERS)).andExpect(status().isOk());
  }

  @Test
  @WithMockUser(roles = "USER")
  void test2() throws Exception {
    mockMvc.perform(get(Endpoints.Rest.USERS)).andExpect(status().isForbidden());
  }

  // Ein bisschen doof ist noch, dass wir ohne @WithMockUser als Status ein 302 erhalten. Das liegt daran, dass eine Weiterleitung zur Login-Seite gemacht wird.
  // Das ist bei MVC-Controller auch durchaus gewollt, jedoch nicht bei RestControllern.
  // Das zu fixen, ist aber out of scope der aktuellen Karte. Sollten wir in einer gesonderten Karte betrachten.

}
