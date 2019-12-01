package com.metalr2.web.controller;

import com.metalr2.config.constants.Endpoints;
import com.metalr2.config.constants.ViewNames;
import com.metalr2.model.user.UserFactory;
import com.metalr2.security.WebSecurity;
import com.metalr2.service.user.UserService;
import com.metalr2.web.controller.mvc.SearchArtistsController;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SearchArtistsController.class)
@Import(WebSecurity.class)
@Tag("integration-test")
class SearchArtistsControllerIT {

  private static final String USERNAME  = "JohnD";
  private static final String PASSWORD  = "john.doe";
  private static final String EMAIL     = "john.doe@example.com";

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private UserService userService;

  @Autowired
  private BCryptPasswordEncoder passwordEncoder;

  private MockHttpSession session;

  @BeforeEach
  void setUp() throws Exception {
    when(userService.loadUserByUsername(any())).thenAnswer(invocationOnMock -> {
      String usernameArg = invocationOnMock.getArgument(0);
      if (usernameArg.equalsIgnoreCase(USERNAME)) {
        return UserFactory.createUser(USERNAME, EMAIL, passwordEncoder.encode(PASSWORD));
      }
      else {
        throw new UsernameNotFoundException("username not found");
      }
    });

    // Login
    MvcResult result = mockMvc.perform(post(Endpoints.Guest.LOGIN)
            .param("username", USERNAME)
            .param("password", PASSWORD))
            .andReturn();

    session = (MockHttpSession)result.getRequest().getSession();
  }

  @AfterEach
  void tearDown() {
    reset(userService);
  }

  @Test
  @DisplayName("Requesting '" + Endpoints.Frontend.SEARCH_ARTISTS + "' should return the view to search artists for logged in user")
  void get_should_return_search_artists_view_for_logged_in_user() throws Exception {
    mockMvc.perform(get(Endpoints.Frontend.SEARCH_ARTISTS).session(session))
              .andExpect(status().isOk())
              .andExpect(view().name(ViewNames.Frontend.SEARCH_ARTISTS))
              .andExpect(model().size(0))
              .andExpect(content().contentType("text/html;charset=UTF-8"))
              .andExpect(content().string(containsString("Search")));
  }

  @Test
  @DisplayName("Requesting '" + Endpoints.Frontend.SEARCH_ARTISTS + "' should return the view to search artists for anonymous user")
  void get_should_return_search_login_for_anonymous_user() throws Exception {
    mockMvc.perform(get(Endpoints.Frontend.SEARCH_ARTISTS))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("http://localhost" + Endpoints.Guest.LOGIN));
  }

  @TestConfiguration
  static class TestConfig {

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
      return new BCryptPasswordEncoder();
    }

  }
}
