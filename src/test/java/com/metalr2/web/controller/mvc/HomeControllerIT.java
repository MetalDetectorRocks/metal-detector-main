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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(value = HomeController.class, excludeAutoConfiguration = MockMvcSecurityAutoConfiguration.class)
class HomeControllerIT implements WithIntegrationTestProfile {

  @Autowired
  private MockMvc mockMvc;

  @Test
  @DisplayName("Requesting '" + Endpoints.Frontend.HOME + "' should return the home view")
  void get_should_return_home_view() throws Exception {
    mockMvc.perform(get(Endpoints.Frontend.HOME))
              .andExpect(status().isOk())
              .andExpect(view().name(ViewNames.Frontend.HOME))
              .andExpect(model().size(0))
              .andExpect(content().contentType("text/html;charset=UTF-8"))
              .andExpect(content().string(containsString("Home")));
  }

}
