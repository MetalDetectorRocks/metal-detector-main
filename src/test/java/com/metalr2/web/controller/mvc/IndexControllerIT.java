package com.metalr2.web.controller.mvc;

import com.metalr2.config.constants.Endpoints;
import com.metalr2.config.constants.ViewNames;
import com.metalr2.testutil.WithIntegrationTestProfile;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.MockMvcSecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(value = IndexController.class, excludeAutoConfiguration = MockMvcSecurityAutoConfiguration.class)
class IndexControllerIT implements WithIntegrationTestProfile {

  @Autowired
  private MockMvc mockMvc;

  @Test
  void given_index_uri_then_return_index_view() throws Exception {
    mockMvc.perform(get(Endpoints.Guest.SLASH_INDEX))
            .andExpect(status().isOk())
            .andExpect(view().name(ViewNames.Guest.INDEX));
  }

}
