package com.metalr2.web.controller.mvc;

import com.metalr2.config.constants.Endpoints;
import com.metalr2.config.constants.ViewNames;
import com.metalr2.testutil.WithIntegrationTestProfile;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.MockMvcSecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(value = SearchArtistsController.class, excludeAutoConfiguration = MockMvcSecurityAutoConfiguration.class)
class SearchArtistsControllerIT implements WithIntegrationTestProfile {

  @Autowired
  private MockMvc mockMvc;

  @Test
  @DisplayName("Requesting '" + Endpoints.Frontend.SEARCH_ARTISTS + "' should return the view to search artists")
  void get_should_return_search_artists_view() throws Exception {
    mockMvc.perform(get(Endpoints.Frontend.SEARCH_ARTISTS))
              .andExpect(status().isOk())
              .andExpect(view().name(ViewNames.Frontend.SEARCH_ARTISTS))
              .andExpect(model().size(0))
              .andExpect(content().contentType("text/html;charset=UTF-8"))
              .andExpect(content().string(containsString("Search")));
  }

}
