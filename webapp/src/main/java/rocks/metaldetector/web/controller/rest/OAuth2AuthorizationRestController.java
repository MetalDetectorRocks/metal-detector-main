package rocks.metaldetector.web.controller.rest;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationExchange;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationResponse;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import rocks.metaldetector.support.oauth.OAuth2AuthenticationProvider;
import rocks.metaldetector.web.api.response.OAuth2UserAuthorizationExistsResponse;

import java.net.URI;
import java.util.Objects;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.security.oauth2.core.AuthorizationGrantType.AUTHORIZATION_CODE;
import static rocks.metaldetector.support.Endpoints.Rest.OAUTH;

@RestController
@AllArgsConstructor
@Slf4j
public class OAuth2AuthorizationRestController {

  private final OAuth2AuthorizedClientService oAuth2AuthorizedClientService;
  private final OAuth2AuthenticationProvider authenticationProvider;
  private final AuthorizationRequestRepository<OAuth2AuthorizationRequest> authorizationRequestRepository;
  private final OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> accessTokenResponseClient;
  private final ClientRegistrationRepository clientRegistrationRepository;

  @GetMapping(path = OAUTH + "/{registration-id}", produces = APPLICATION_JSON_VALUE)
  public ResponseEntity<OAuth2UserAuthorizationExistsResponse> checkAuthorization(@PathVariable("registration-id") String registrationId) {
    Authentication currentOAuthAuthentication = authenticationProvider.provideForGrant(AUTHORIZATION_CODE);
    OAuth2AuthorizedClient authorizedClient = oAuth2AuthorizedClientService.loadAuthorizedClient(registrationId, currentOAuthAuthentication.getName());
    if (authorizedClient != null) {
      return ResponseEntity.ok(new OAuth2UserAuthorizationExistsResponse(true));
    }
    return ResponseEntity.ok(new OAuth2UserAuthorizationExistsResponse(false));
  }

  @GetMapping(path = OAUTH + "/callback", produces = APPLICATION_JSON_VALUE)
  public ResponseEntity<Void> handleCallback(@RequestParam(value = "code", defaultValue = "") String code,
                                             @RequestParam(value = "state", defaultValue = "") String state,
                                             @Value("${frontend.origin}") String frontendOrigin,
                                             HttpServletRequest servletRequest, HttpServletResponse servletResponse) {
    OAuth2AuthorizationRequest authorizationRequest = authorizationRequestRepository.loadAuthorizationRequest(servletRequest);
    OAuth2AccessTokenResponse tokenResponse = null;
    if (authorizationRequest != null && Objects.equals(state, authorizationRequest.getState())) {
      OAuth2AuthorizationResponse authorizationResponse = OAuth2AuthorizationResponse.success(code).state(state).redirectUri(authorizationRequest.getRedirectUri()).build();
      OAuth2AuthorizationExchange authorizationExchange = new OAuth2AuthorizationExchange(authorizationRequest, authorizationResponse);
      ClientRegistration clientRegistration = clientRegistrationRepository.findByRegistrationId("spotify-user");
      OAuth2AuthorizationCodeGrantRequest authorizationCodeGrantRequest = new OAuth2AuthorizationCodeGrantRequest(clientRegistration, authorizationExchange);
      tokenResponse = accessTokenResponseClient.getTokenResponse(authorizationCodeGrantRequest);
      log.info(tokenResponse.toString());
    }
    return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(frontendOrigin)).build();
  }

  @DeleteMapping(path = OAUTH + "/{registration-id}")
  public ResponseEntity<Void> deleteAuthorization(@PathVariable("registration-id") String registrationId) {
    Authentication currentOAuthAuthentication = authenticationProvider.provideForGrant(AUTHORIZATION_CODE);
    oAuth2AuthorizedClientService.removeAuthorizedClient(registrationId, currentOAuthAuthentication.getName());
    return ResponseEntity.ok().build();
  }
}
