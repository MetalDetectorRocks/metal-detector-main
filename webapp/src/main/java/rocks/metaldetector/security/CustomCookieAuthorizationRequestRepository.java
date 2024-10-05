package rocks.metaldetector.security;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.stereotype.Component;
import rocks.metaldetector.support.SecurityProperties;

import java.io.IOException;
import java.util.Base64;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import static org.apache.tomcat.util.http.SameSiteCookies.LAX;

@Component
@RequiredArgsConstructor
public class CustomCookieAuthorizationRequestRepository implements AuthorizationRequestRepository<OAuth2AuthorizationRequest> {

  public static final String SESSION_STATE_COOKIE_NAME = "session_state";
  static final int COOKIE_MAX_AGE_SECONDS = 60;

  private final ObjectMapper objectMapper;
  private final SecurityProperties securityProperties;
  private String domain;

  @Override
  public OAuth2AuthorizationRequest loadAuthorizationRequest(HttpServletRequest request) {
    Cookie[] cookies = request.getCookies();
    if (cookies != null) {
      for (Cookie cookie : cookies) {
        if (cookie.getName().equals(SESSION_STATE_COOKIE_NAME)) {
          return loadRequest(cookie);
        }
      }
    }
    return null;
  }

  @Override
  public void saveAuthorizationRequest(OAuth2AuthorizationRequest authorizationRequest, HttpServletRequest request, HttpServletResponse response) {
    String serializedRequest = null;
    try {
      serializedRequest = new String(Base64.getEncoder().encode(objectMapper.writeValueAsString(authorizationRequest).getBytes()));
    } catch (JsonProcessingException e) {
      throw new RuntimeException("Failed to serialize OAuth2AuthorizationRequest", e);
    }
    Cookie cookie = new Cookie(SESSION_STATE_COOKIE_NAME, serializedRequest);
    cookie.setPath("/");
    cookie.setMaxAge(COOKIE_MAX_AGE_SECONDS);
    cookie.setHttpOnly(true);
    cookie.setSecure(securityProperties.isSecureCookie());
    cookie.setDomain(domain);
    cookie.setAttribute("SameSite", LAX.getValue());
    response.addCookie(cookie);
  }

  @Override
  public OAuth2AuthorizationRequest removeAuthorizationRequest(HttpServletRequest request, HttpServletResponse response) {
    Cookie[] cookies = request.getCookies();
    if (cookies != null) {
      for (Cookie cookie : cookies) {
        if (cookie.getName().equals(SESSION_STATE_COOKIE_NAME)) {
          OAuth2AuthorizationRequest authorizationRequest = loadRequest(cookie);
          cookie.setValue(null);
          cookie.setMaxAge(0);
          response.addCookie(cookie);
          return authorizationRequest;
        }
      }
    }
    return null;
  }

  private OAuth2AuthorizationRequest loadRequest(Cookie cookie) {
    try {
      String json = new String(Base64.getDecoder().decode(cookie.getValue()));
      Map<String, Object> params = objectMapper.readValue(json, new TypeReference<>(){});
      OAuth2AuthorizationRequest.Builder builder = OAuth2AuthorizationRequest.authorizationCode();
      builder.clientId((String) params.get("clientId"))
          .redirectUri((String) params.get("redirectUri"))
          .authorizationUri((String) params.get("authorizationUri"))
          .authorizationRequestUri((String) params.get("authorizationRequestUri"))
          .scopes(new HashSet<>(((List<String>) params.get("scopes"))))
          .state((String) params.get("state"))
          .attributes((Map<String, Object>) params.get("attributes"))
          .additionalParameters((Map<String, Object>) params.get("additionalParameters"));
      return builder.build();
    } catch (IOException e) {
      throw new RuntimeException("Failed to deserialize OAuth2AuthorizationRequest", e);
    }
  }

  @Value("${application.domain}")
  void setDomain(String domain) {
    this.domain = domain;
  }
}
