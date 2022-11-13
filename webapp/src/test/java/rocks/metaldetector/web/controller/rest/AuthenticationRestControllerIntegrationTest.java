package rocks.metaldetector.web.controller.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseCookie;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.web.servlet.MockMvc;
import rocks.metaldetector.service.user.AuthService;
import rocks.metaldetector.service.user.RefreshTokenService;
import rocks.metaldetector.testutil.BaseSpringBootTest;
import rocks.metaldetector.testutil.DtoFactory.LoginRequestFactory;
import rocks.metaldetector.web.api.response.LoginResponse;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static rocks.metaldetector.support.Endpoints.Rest.LOGIN;

@SpringBootTest
@AutoConfigureMockMvc
class AuthenticationRestControllerIntegrationTest extends BaseSpringBootTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  @SuppressWarnings("unused")
  private AuthService authService;

  @MockBean
  @SuppressWarnings("unused")
  private RefreshTokenService refreshTokenService;

  @Autowired
  private ObjectMapper objectMapper;

  @Nested
  class AnonymousUserTest {

    @Test
    @DisplayName("Anonymous user is allowed to POST on endpoint " + LOGIN + "'")
    @WithAnonymousUser
    void anonymous_user_is_allowed_to_login() throws Exception {
      doReturn(mock(LoginResponse.class)).when(authService).loginUser(any());
      doReturn(ResponseCookie.from("foo", "bar").build()).when(refreshTokenService).createRefreshTokenCookie(any());

      mockMvc.perform(post(LOGIN)
              .content(objectMapper.writeValueAsString(LoginRequestFactory.createDefault()))
              .contentType(APPLICATION_JSON))
          .andExpect(status().isOk());
    }
  }
}
