package com.metalr2.web.controller;

import com.metalr2.config.constants.Endpoints;
import com.metalr2.config.constants.ViewNames;
import com.metalr2.model.user.UserEntity;
import com.metalr2.model.user.UserFactory;
import com.metalr2.model.user.UserRepository;
import com.metalr2.model.user.UserRole;
import com.metalr2.service.user.UserService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@WebMvcTest(SearchArtistsController.class)
@Import(WebSecurity.class)
@Tag("integration-test")
class SearchArtistsControllerIT {

  private static final String USERNAME  = "JohnD";
  private static final String PASSWORD  = "john.doe";
  private static final String EMAIL     = "john.doe@example.com";

  @Autowired
  private MockMvc mockMvc;

//  @MockBean
//  private UserRepository userRepository;

  @MockBean
  private UserService userService;

  @Autowired
  private BCryptPasswordEncoder passwordEncoder;

  private UserEntity userEntity;

  @BeforeEach
  void setUp() {
    when(userService.loadUserByUsername(any())).thenAnswer(invocationOnMock -> {
      String usernameArg = invocationOnMock.getArgument(0);
      if (usernameArg.equalsIgnoreCase(USERNAME)) {
        return UserFactory.createUser(USERNAME, EMAIL, passwordEncoder.encode(PASSWORD));
      }
      else {
        throw new UsernameNotFoundException("username not found");
      }
    });
//    userEntity = UserEntity.builder()
//            .username(USERNAME)
//            .email(EMAIL)
//            .password("$2a$10$2IevDskxEeSmy7Sy41Xl7.u22hTcw3saxQghS.bWaIx3NQrzKTvxK")
//            .enabled(true)
//            .userRoles(UserRole.createUserRole())
//            .build();
//    userRepository.save(userEntity);
  }

  @AfterEach
  void tearDown() {
//    userRepository.deleteAll();
  }

  //@Test
  @DisplayName("Requesting '" + Endpoints.Frontend.SEARCH_ARTISTS + "' should return the view to search artists for logged in user")
  void given_search_artists_uri_should_return_search_artists_view() throws Exception {
//    Authentication auth = new UsernamePasswordAuthenticationToken(userEntity,PASSWORD,userEntity.getAuthorities());
    // Login
    mockMvc.perform(post(Endpoints.Guest.LOGIN)
              .with(SecurityMockMvcRequestPostProcessors.csrf())
//              .with(SecurityMockMvcRequestPostProcessors.user(USERNAME)))
              .param("username", USERNAME)
              .param("password", PASSWORD))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl(Endpoints.Frontend.HOME));
//            .andExpect(cookie().exists("JSESSIONID"));


    mockMvc.perform(get(Endpoints.Frontend.SEARCH_ARTISTS))
              .andExpect(status().isOk())
              .andExpect(view().name(ViewNames.Frontend.SEARCH_ARTISTS))
              .andExpect(model().size(0))
              .andExpect(content().contentType("text/html;charset=UTF-8"))
              .andExpect(cookie().exists("JSESSIONID"));
  //            .andExpect(content().string(containsString("Login")));
  }

  @TestConfiguration
  static class TestConfig {

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
      return new BCryptPasswordEncoder();
    }

  }
}