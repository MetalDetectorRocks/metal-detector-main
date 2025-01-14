package rocks.metaldetector.testutil;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.MockMvc;
import rocks.metaldetector.security.RedirectionHandlerInterceptor;
import rocks.metaldetector.service.user.UserService;
import rocks.metaldetector.support.SecurityProperties;
import rocks.metaldetector.web.transformer.UserDtoTransformer;

import javax.sql.DataSource;

public abstract class BaseWebMvcTestWithSecurity {

  @Autowired
  protected MockMvc mockMvc;

  @SpyBean
  protected BCryptPasswordEncoder passwordEncoder;

  @MockitoBean
  protected DataSource dataSource;

  @MockitoBean
  protected RedirectionHandlerInterceptor redirectionInterceptor;

  @MockitoBean
  protected UserService userService;

  @MockitoSpyBean
  protected ObjectMapper objectMapper;

  @MockitoBean
  protected UserDtoTransformer userDtoTransformer;

  @MockitoBean
  protected SecurityProperties securityProperties;

  @MockitoBean
  protected OAuth2UserService<OidcUserRequest, OidcUser> customOidcUserService;

  @MockitoBean
  protected OAuth2AuthorizationRequestResolver oAuth2AuthorizationRequestResolver;

  @MockitoBean
  protected OAuth2AuthorizedClientService oAuth2AuthorizedClientService;

  @MockitoBean
  protected OAuth2AuthorizedClientRepository oAuth2AuthorizedClientRepository;

  @MockitoBean
  protected ClientRegistrationRepository clientRegistrationRepository;
}
