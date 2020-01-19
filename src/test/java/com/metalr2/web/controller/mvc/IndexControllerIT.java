package com.metalr2.web.controller.mvc;

import com.metalr2.config.constants.Endpoints;
import com.metalr2.config.constants.ViewNames;
import com.metalr2.testutil.WithSecurityConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

//@AutoConfigureMockMvc
//@ContextConfiguration(classes = {IndexController.class})
@WebMvcTest(IndexController.class)
//@SpringBootTest
class IndexControllerIT implements WithSecurityConfig {

  @Autowired
  private MockMvc mockMvc;

//  @BeforeEach
//  public void setUp() {
//    mockMvc = MockMvcBuilders.webAppContextSetup(context)
//        .apply(springSecurity())
//        .build();
//  }

  @ParameterizedTest(name = "[{index}] => Endpoint <{0}>")
  @ValueSource(strings = {Endpoints.Guest.SLASH_INDEX, Endpoints.Guest.INDEX})
  @DisplayName("GET on index should return index view for anonymous user")
  @WithAnonymousUser
  void given_index_uri_then_return_index_view(String endpoint) throws Exception {
    mockMvc.perform(get(endpoint))
        .andExpect(status().isOk())
        .andExpect(view().name(ViewNames.Guest.INDEX));
  }

  @Test
  @DisplayName("GET on index should return home view for logged in user")
  @WithMockUser
  void given_index_uri_then_return_home_view() throws Exception {
    mockMvc.perform(get(Endpoints.Guest.SLASH_INDEX))
        .andExpect(status().is3xxRedirection())
        .andExpect(view().name("redirect:" + ViewNames.Frontend.HOME));
  }
}
