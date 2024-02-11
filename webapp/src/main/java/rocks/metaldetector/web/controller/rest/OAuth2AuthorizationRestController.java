package rocks.metaldetector.web.controller.rest;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import rocks.metaldetector.support.oauth.OAuth2AuthenticationProvider;
import rocks.metaldetector.web.api.response.OAuth2UserAuthorizationExistsResponse;

import java.net.URI;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.security.oauth2.core.AuthorizationGrantType.AUTHORIZATION_CODE;
import static rocks.metaldetector.support.Endpoints.Rest.OAUTH_CALLBACK;
import static rocks.metaldetector.support.Endpoints.Rest.OAUTH_REGISTRATION_ID;

@RestController
@AllArgsConstructor
@Slf4j
public class OAuth2AuthorizationRestController {

  private final OAuth2AuthorizedClientService oAuth2AuthorizedClientService;
  private final OAuth2AuthenticationProvider authenticationProvider;

  @GetMapping(path = OAUTH_REGISTRATION_ID, produces = APPLICATION_JSON_VALUE)
  public ResponseEntity<OAuth2UserAuthorizationExistsResponse> checkAuthorization(@PathVariable("registration-id") String registrationId) {
    Authentication currentOAuthAuthentication = authenticationProvider.provideForGrant(AUTHORIZATION_CODE);
    OAuth2AuthorizedClient authorizedClient = oAuth2AuthorizedClientService.loadAuthorizedClient(registrationId, currentOAuthAuthentication.getName());
    if (authorizedClient != null) {
      return ResponseEntity.ok(new OAuth2UserAuthorizationExistsResponse(true));
    }
    return ResponseEntity.ok(new OAuth2UserAuthorizationExistsResponse(false));
  }

  @GetMapping(path = OAUTH_CALLBACK)
  public ResponseEntity<Void> handleCallback(@Value("${frontend.origin}") String frontendOrigin) {
    return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(frontendOrigin + "/settings/spotify-synchronization")).build();
  }

  @DeleteMapping(path = OAUTH_REGISTRATION_ID)
  public ResponseEntity<Void> deleteAuthorization(@PathVariable("registration-id") String registrationId) {
    Authentication currentOAuthAuthentication = authenticationProvider.provideForGrant(AUTHORIZATION_CODE);
    oAuth2AuthorizedClientService.removeAuthorizedClient(registrationId, currentOAuthAuthentication.getName());
    return ResponseEntity.ok().build();
  }
}
