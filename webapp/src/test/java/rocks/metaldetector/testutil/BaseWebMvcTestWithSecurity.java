package rocks.metaldetector.testutil;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.test.web.servlet.MockMvc;
import rocks.metaldetector.persistence.domain.user.UserRepository;
import rocks.metaldetector.security.JwtAuthenticationEntryPoint;
import rocks.metaldetector.security.RedirectionHandlerInterceptor;
import rocks.metaldetector.support.JwtsSupport;
import rocks.metaldetector.web.transformer.UserDtoTransformer;

import javax.servlet.http.HttpServletRequest;

public abstract class BaseWebMvcTestWithSecurity {

  @Autowired
  protected MockMvc mockMvc;

  @SpyBean
  protected BCryptPasswordEncoder passwordEncoder;

  @MockBean
  protected RedirectionHandlerInterceptor redirectionInterceptor;

  @SpyBean
  protected ObjectMapper objectMapper;

  @MockBean
  protected UserDtoTransformer userDtoTransformer;

  @MockBean
  protected OAuth2UserService<OidcUserRequest, OidcUser> customOidcUserService;

  @MockBean
  protected OAuth2AuthorizationRequestResolver oAuth2AuthorizationRequestResolver;

  @MockBean
  protected OAuth2AuthorizedClientService oAuth2AuthorizedClientService;

  @MockBean
  protected OAuth2AuthorizedClientRepository oAuth2AuthorizedClientRepository;

  @MockBean
  protected ClientRegistrationRepository clientRegistrationRepository;

  @MockBean
  protected JwtsSupport jwtsSupport;

  @MockBean
  protected UserRepository userRepository;

  @MockBean
  protected AuthenticationDetailsSource<HttpServletRequest, ?> authenticationDetailsSource;

  @MockBean
  protected JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
}
