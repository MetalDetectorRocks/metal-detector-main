package rocks.metaldetector.security;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.exc.StreamConstraintsException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import rocks.metaldetector.support.SecurityProperties;

import java.util.Base64;
import java.util.Map;
import java.util.Set;

import static org.apache.tomcat.util.http.SameSiteCookies.LAX;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static rocks.metaldetector.security.CustomCookieAuthorizationRequestRepository.COOKIE_MAX_AGE_SECONDS;
import static rocks.metaldetector.security.CustomCookieAuthorizationRequestRepository.SESSION_STATE_COOKIE_NAME;

@ExtendWith(MockitoExtension.class)
class CustomCookieAuthorizationRequestRepositoryTest implements WithAssertions {

  private static final OAuth2AuthorizationRequest AUTHORIZATION_REQUEST = OAuth2AuthorizationRequest.authorizationCode()
      .clientId("clientId")
      .redirectUri("redirectUri")
      .authorizationUri("authorizationUri")
      .authorizationRequestUri("authorizationRequestUri")
      .scopes(Set.of("scope"))
      .state("state")
      .attributes(Map.of("attribute1", "value1", "attribute2", "value2"))
      .additionalParameters(Map.of("parameter1", "value1", "parameter2", "value2"))
      .build();

  @Spy
  private ObjectMapper objectMapper;

  @Mock
  private SecurityProperties securityProperties;

  @InjectMocks
  private CustomCookieAuthorizationRequestRepository underTest;

  @AfterEach
  void tearDown() {
    reset(objectMapper, securityProperties);
  }

  @Test
  @DisplayName("loadAuthorizationRequest returns null if cookie does not exist")
  void test_load_authorization_request_returns_null() {
    // when
    OAuth2AuthorizationRequest result = underTest.loadAuthorizationRequest(new MockHttpServletRequest());

    // then
    assertThat(result).isNull();
  }

  @Test
  @DisplayName("loadAuthorizationRequest calls objectMapper")
  void test_load_authorization_request_calls_object_mapper() throws JsonProcessingException {
    // given
    MockHttpServletRequest request = new MockHttpServletRequest();
    Cookie cookie = new Cookie(SESSION_STATE_COOKIE_NAME, new String(Base64.getEncoder().encode(objectMapper.writeValueAsString(AUTHORIZATION_REQUEST).getBytes())));
    request.setCookies(cookie);

    // when
    underTest.loadAuthorizationRequest(request);

    // then
    verify(objectMapper).writeValueAsString(AUTHORIZATION_REQUEST);
  }

  @Test
  @DisplayName("loadAuthorizationRequest returns cookie if it exists")
  void test_load_authorization_request_returns_cookie() throws JsonProcessingException {
    // given
    MockHttpServletRequest request = new MockHttpServletRequest();

    Cookie cookie = new Cookie(SESSION_STATE_COOKIE_NAME, new String(Base64.getEncoder().encode(objectMapper.writeValueAsString(AUTHORIZATION_REQUEST).getBytes())));
    request.setCookies(cookie);

    // when
    OAuth2AuthorizationRequest result = underTest.loadAuthorizationRequest(request);

    // then
    assertThat(result).isNotNull();
    assertThat(result.getClientId()).isEqualTo("clientId");
    assertThat(result.getRedirectUri()).isEqualTo("redirectUri");
    assertThat(result.getState()).isEqualTo("state");
    assertThat(result.getScopes()).hasSize(1);
    assertThat(result.getScopes()).containsExactly("scope");
    assertThat(result.getAttributes()).hasSize(2);
    assertThat(result.getAttributes()).containsEntry("attribute1", "value1");
    assertThat(result.getAttributes()).containsEntry("attribute2", "value2");
    assertThat(result.getAdditionalParameters()).hasSize(2);
    assertThat(result.getAdditionalParameters()).containsEntry("parameter1", "value1");
    assertThat(result.getAdditionalParameters()).containsEntry("parameter2", "value2");
  }

  @Test
  @DisplayName("saveAuthorizationRequest throws exception if request cannot be serialized")
  void test_save_authorization_request_throws_exception() throws JsonProcessingException {
    // given
    doThrow(new StreamConstraintsException("boom")).when(objectMapper).writeValueAsString(any());

    // when
    Throwable throwable = catchThrowable(() -> underTest.saveAuthorizationRequest(mock(OAuth2AuthorizationRequest.class), new MockHttpServletRequest(), new MockHttpServletResponse()));

    // then
    assertThat(throwable).isInstanceOf(RuntimeException.class);
    assertThat(throwable.getCause()).isInstanceOf(StreamConstraintsException.class);
  }

  @Test
  @DisplayName("saveAuthorizationRequest calls objectMapper")
  void test_save_authorization_request_calls_object_mapper() throws JsonProcessingException {
    // when
    underTest.saveAuthorizationRequest(AUTHORIZATION_REQUEST, new MockHttpServletRequest(), new MockHttpServletResponse());

    // then
    verify(objectMapper).writeValueAsString(AUTHORIZATION_REQUEST);
  }

  @Test
  @DisplayName("saveAuthorizationRequest call security properties")
  void test_save_authorization_request_calls_security_props() {
    // when
    underTest.saveAuthorizationRequest(mock(OAuth2AuthorizationRequest.class), new MockHttpServletRequest(), new MockHttpServletResponse());

    // then
    verify(securityProperties).isSecureCookie();
  }

  @Test
  @DisplayName("saveAuthorizationRequest saves cookie")
  void test_save_authorization_request_saves_cookie() throws JsonProcessingException {
    // given
    MockHttpServletResponse response = new MockHttpServletResponse();
    doReturn(true).when(securityProperties).isSecureCookie();
    doReturn("someValue").when(objectMapper).writeValueAsString(any());
    underTest.setDomain("domain");

    // when
    underTest.saveAuthorizationRequest(mock(OAuth2AuthorizationRequest.class), new MockHttpServletRequest(), response);

    // then
    assertThat(response.getCookies()).hasSize(1);
    Cookie responseCookie = response.getCookie(SESSION_STATE_COOKIE_NAME);
    assertThat(responseCookie).isNotNull();
    assertThat(responseCookie.getValue()).isEqualTo(Base64.getEncoder().encodeToString("someValue".getBytes()));
    assertThat(responseCookie.getPath()).isEqualTo("/");
    assertThat(responseCookie.getDomain()).isEqualTo("domain");
    assertThat(responseCookie.getName()).isEqualTo(SESSION_STATE_COOKIE_NAME);
    assertThat(responseCookie.getMaxAge()).isEqualTo(COOKIE_MAX_AGE_SECONDS);
    assertThat(responseCookie.isHttpOnly()).isTrue();
    assertThat(responseCookie.getSecure()).isTrue();
    assertThat(responseCookie.getAttribute("SameSite")).isEqualTo(LAX.getValue());
  }

  @Test
  @DisplayName("removeAuthorizationRequest returns null if cookie does not exist")
  void test_remove_authorization_request_returns_null() {
    // when
    OAuth2AuthorizationRequest result = underTest.removeAuthorizationRequest(new MockHttpServletRequest(), new MockHttpServletResponse());

    // then
    assertThat(result).isNull();
  }

  @Test
  @DisplayName("removeAuthorizationRequest calls objectMapper")
  void test_remove_authorization_request_calls_object_mapper() throws JsonProcessingException {
    // given
    MockHttpServletRequest request = new MockHttpServletRequest();
    Cookie cookie = new Cookie(SESSION_STATE_COOKIE_NAME, new String(Base64.getEncoder().encode(objectMapper.writeValueAsString(AUTHORIZATION_REQUEST).getBytes())));
    request.setCookies(cookie);

    // when
    underTest.removeAuthorizationRequest(request, new MockHttpServletResponse());

    // then
    verify(objectMapper).writeValueAsString(AUTHORIZATION_REQUEST);
  }

  @Test
  @DisplayName("removeAuthorizationRequest returns cookie if it exists")
  void test_remove_authorization_request_returns_cookie() throws JsonProcessingException {
    // given
    MockHttpServletRequest request = new MockHttpServletRequest();

    Cookie cookie = new Cookie(SESSION_STATE_COOKIE_NAME, new String(Base64.getEncoder().encode(objectMapper.writeValueAsString(AUTHORIZATION_REQUEST).getBytes())));
    request.setCookies(cookie);

    // when
    OAuth2AuthorizationRequest result = underTest.removeAuthorizationRequest(request, new MockHttpServletResponse());

    // then
    assertThat(result).isNotNull();
    assertThat(result.getClientId()).isEqualTo("clientId");
    assertThat(result.getRedirectUri()).isEqualTo("redirectUri");
    assertThat(result.getState()).isEqualTo("state");
    assertThat(result.getScopes()).hasSize(1);
    assertThat(result.getScopes()).containsExactly("scope");
    assertThat(result.getAttributes()).hasSize(2);
    assertThat(result.getAttributes()).containsEntry("attribute1", "value1");
    assertThat(result.getAttributes()).containsEntry("attribute2", "value2");
    assertThat(result.getAdditionalParameters()).hasSize(2);
    assertThat(result.getAdditionalParameters()).containsEntry("parameter1", "value1");
    assertThat(result.getAdditionalParameters()).containsEntry("parameter2", "value2");
  }

  @Test
  @DisplayName("removeAuthorizationRequest response cookie is cleared")
  void test_remove_authorization_request_response_cookie_max_age() throws JsonProcessingException {
    // given
    MockHttpServletRequest request = new MockHttpServletRequest();
    MockHttpServletResponse response = new MockHttpServletResponse();

    Cookie cookie = new Cookie(SESSION_STATE_COOKIE_NAME, new String(Base64.getEncoder().encode(objectMapper.writeValueAsString(AUTHORIZATION_REQUEST).getBytes())));
    request.setCookies(cookie);

    // when
    underTest.removeAuthorizationRequest(request, response);

    // then
    Cookie responseCookie = response.getCookie(SESSION_STATE_COOKIE_NAME);
    assertThat(responseCookie).isNotNull();
    assertThat(responseCookie.getValue()).isNull();
    assertThat(responseCookie.getMaxAge()).isZero();
  }
}