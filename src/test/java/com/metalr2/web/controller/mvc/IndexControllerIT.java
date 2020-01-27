package com.metalr2.web.controller.mvc;

import com.metalr2.config.constants.Endpoints;
import com.metalr2.config.constants.ViewNames;
import com.metalr2.service.redirection.RedirectionService;
import com.metalr2.service.user.UserService;
import com.metalr2.testutil.WithSecurityConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.ModelAndView;

import javax.sql.DataSource;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(IndexController.class)
class IndexControllerIT implements WithSecurityConfig {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private UserService userService;

  @MockBean
  private BCryptPasswordEncoder passwordEncoder;

  @MockBean
  private DataSource dataSource;

  @MockBean
  private RedirectionService redirectionService;

  @AfterEach
  void tearDown() {
    reset(redirectionService);
  }

  @ParameterizedTest(name = "[{index}] => Endpoint <{0}>")
  @ValueSource(strings = {Endpoints.Guest.INDEX, Endpoints.Guest.SLASH_INDEX})
  @DisplayName("GET on index should return index view for anonymous user")
  void given_index_uri_then_return_index_view(String endpoint) throws Exception {
    mockMvc.perform(get(endpoint))
        .andExpect(status().isOk())
        .andExpect(view().name(ViewNames.Guest.INDEX))
        .andExpect(model().size(0))
        .andExpect(content().contentType("text/html;charset=UTF-8"));
  }

  @ParameterizedTest(name = "[{index}] => Endpoint <{0}>")
  @ValueSource(strings = {Endpoints.Guest.INDEX, Endpoints.Guest.SLASH_INDEX})
  @DisplayName("GET on index should return home view for logged in user")
  void given_index_uri_then_return_home_view(String endpoint) throws Exception {
    ModelAndView redirectionModelAndView = new ModelAndView("redirect:" + Endpoints.Frontend.HOME);
    when(redirectionService.getRedirectionIfNeeded(any())).thenReturn(Optional.of(redirectionModelAndView));

    mockMvc.perform(get(endpoint))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(Endpoints.Frontend.HOME))
        .andExpect(model().size(0));

    verify(redirectionService, times(1)).getRedirectionIfNeeded(any());
  }
}
