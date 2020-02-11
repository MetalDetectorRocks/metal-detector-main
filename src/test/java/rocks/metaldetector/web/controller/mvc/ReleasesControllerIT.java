package rocks.metaldetector.web.controller.mvc;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.MockMvcSecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import rocks.metaldetector.config.constants.Endpoints;
import rocks.metaldetector.config.constants.ViewNames;
import rocks.metaldetector.testutil.BaseWebMvcTest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(value = ReleasesController.class, excludeAutoConfiguration = MockMvcSecurityAutoConfiguration.class)
public class ReleasesControllerIT extends BaseWebMvcTest {

  @Test
  @DisplayName("Requesting '" + Endpoints.Frontend.RELEASES + "' should return the releases view")
  void get_search_should_return_releases_view() throws Exception {
    mockMvc.perform(get(Endpoints.Frontend.RELEASES))
        .andExpect(view().name(ViewNames.Frontend.RELEASES))
        .andExpect(model().size(0))
        .andExpect(content().contentType("text/html;charset=UTF-8"));
  }
}
