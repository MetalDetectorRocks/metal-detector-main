package com.metalr2.web.controller;

import com.metalr2.config.constants.Endpoints;
import com.metalr2.config.constants.ViewNames;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(IndexController.class)
@Tag("integration-test")
@ActiveProfiles("test")
class IndexControllerIT {

  @Autowired
  private MockMvc mockMvc;

  @Test
  void given_index_uri_then_return_index_view() throws Exception {
    mockMvc.perform(get(Endpoints.Guest.SLASH_INDEX))
            .andExpect(status().isOk())
            .andExpect(view().name(ViewNames.Guest.INDEX));
  }

}
