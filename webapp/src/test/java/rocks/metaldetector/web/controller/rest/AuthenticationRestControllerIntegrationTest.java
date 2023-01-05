package rocks.metaldetector.web.controller.rest;

import jakarta.servlet.http.Cookie;
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
import rocks.metaldetector.service.auth.RefreshTokenService;
import rocks.metaldetector.service.auth.RefreshTokenData;
import rocks.metaldetector.testutil.BaseSpringBootTest;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static rocks.metaldetector.persistence.domain.user.UserRole.ROLE_USER;
import static rocks.metaldetector.support.Endpoints.Rest.REFRESH_ACCESS_TOKEN;

@SpringBootTest
@AutoConfigureMockMvc
class AuthenticationRestControllerIntegrationTest extends BaseSpringBootTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private RefreshTokenService refreshTokenService;

  @Nested
  class AnonymousUserTest {

    @Test
    @DisplayName("Anonymous user with refresh token in cookie is allowed to GET on endpoint " + REFRESH_ACCESS_TOKEN + "'")
    @WithAnonymousUser
    void anonymous_user_with_cookie_is_allowed_to_refresh_tokens() throws Exception {
      var refreshTokenData = new RefreshTokenData(
          "dummy",
          List.of(ROLE_USER.getDisplayName()),
          "access-token",
          ResponseCookie.from("refresh_token", "refresh-token").build()
      );
      doReturn(refreshTokenData).when(refreshTokenService).refreshTokens(any());

      mockMvc.perform(get(REFRESH_ACCESS_TOKEN)
              .cookie(new Cookie("refresh_token", "eyFoo"))
              .accept(APPLICATION_JSON))
          .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Anonymous user needs refresh token in cookie to GET on endpoint " + REFRESH_ACCESS_TOKEN + "'")
    @WithAnonymousUser
    void anonymous_user_needs_refresh_token_in_cookie_to_refresh_tokens() throws Exception {
      mockMvc.perform(get(REFRESH_ACCESS_TOKEN)
              .accept(APPLICATION_JSON))
          .andExpect(status().isUnauthorized());
    }
  }
}
