package com.metalr2.web.controller.mvc;

import com.metalr2.config.constants.Endpoints;
import com.metalr2.config.constants.ViewNames;
import com.metalr2.testutil.BaseWebMvcTestWithSecurity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(value = IndexController.class, excludeAutoConfiguration = WebMvcAutoConfiguration.class)
class IndexControllerIT extends BaseWebMvcTestWithSecurity {

  @ParameterizedTest(name = "[{index}] => Endpoint <{0}>")
  @ValueSource(strings = {Endpoints.Guest.INDEX, Endpoints.Guest.SLASH_INDEX})
  @DisplayName("GET on index should return index view")
  void given_index_uri_then_return_index_view(String endpoint) throws Exception {
    mockMvc.perform(get(endpoint))
        .andExpect(status().isOk())
        .andExpect(view().name(ViewNames.Guest.INDEX))
        .andExpect(model().size(0))
        .andExpect(content().contentType("text/html;charset=UTF-8"));
  }
}
