package com.metalr2.web.controller.mvc;

import com.metalr2.config.constants.Endpoints;
import com.metalr2.config.constants.ViewNames;
import com.metalr2.security.RedirectionHandlerInterceptor;
import com.metalr2.testutil.WithIntegrationTestProfile;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.MockMvcSecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(value = MyArtistsController.class, excludeAutoConfiguration = {WebMvcAutoConfiguration.class, MockMvcSecurityAutoConfiguration.class})
public class MyArtistsControllerIT implements WithIntegrationTestProfile {

  @MockBean
  private RedirectionHandlerInterceptor redirectionHandlerInterceptor;

  @Autowired
  private MockMvc mockMvc;

  @Test
  @DisplayName("Requesting '" + Endpoints.Frontend.MY_ARTISTS + "' should return the my-artists view")
  void get_search_should_return_my_artists_view() throws Exception {
    mockMvc.perform(get(Endpoints.Frontend.MY_ARTISTS))
        .andExpect(view().name(ViewNames.Frontend.MY_ARTISTS))
        .andExpect(model().size(0))
        .andExpect(content().contentType("text/html;charset=UTF-8"))
        .andExpect(content().string(containsString("My Artists")));
  }
}
