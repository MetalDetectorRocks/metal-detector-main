package rocks.metaldetector.web.controller.mvc;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import rocks.metaldetector.config.constants.Endpoints;
import rocks.metaldetector.config.constants.ViewNames;
import rocks.metaldetector.testutil.BaseWebMvcTest;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(value = ArtistsController.class)
@WithMockUser
class ArtistsControllerIT extends BaseWebMvcTest {

  @Test
  @DisplayName("Requesting '" + Endpoints.Frontend.ARTISTS + "' should return the view to search artists")
  void get_search_should_return_search_artists_view() throws Exception {
    mockMvc.perform(get(Endpoints.Frontend.ARTISTS))
        .andExpect(view().name(ViewNames.Frontend.SEARCH))
        .andExpect(model().size(0))
        .andExpect(content().contentType("text/html;charset=UTF-8"))
        .andExpect(content().string(containsString("Search")));
  }
}
