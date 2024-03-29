package rocks.metaldetector.web.controller.rest;

import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import rocks.metaldetector.support.Endpoints;
import rocks.metaldetector.support.oauth.OAuth2AuthenticationProvider;
import rocks.metaldetector.web.RestAssuredMockMvcUtils;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.security.core.authority.AuthorityUtils.createAuthorityList;
import static org.springframework.security.oauth2.core.AuthorizationGrantType.AUTHORIZATION_CODE;

@ExtendWith(MockitoExtension.class)
class OAuth2AuthorizationRestControllerTest implements WithAssertions {

  private static final AnonymousAuthenticationToken PRINCIPAL = new AnonymousAuthenticationToken("key", "anonymous", createAuthorityList("ROLE_ANONYMOUS"));
  private static final String USER_ID = "userId";

  @Mock
  private OAuth2AuthorizedClientService oAuth2AuthorizedClientService;

  @Mock
  private OAuth2AuthenticationProvider authenticationProvider;

  private RestAssuredMockMvcUtils restAssuredUtils;

  @BeforeEach
  void setup() {
    OAuth2AuthorizationRestController underTest = new OAuth2AuthorizationRestController(oAuth2AuthorizedClientService, authenticationProvider);
    restAssuredUtils = new RestAssuredMockMvcUtils(Endpoints.Rest.OAUTH);
    RestAssuredMockMvc.standaloneSetup(underTest);
    doReturn(PRINCIPAL).when(authenticationProvider).provideForGrant(any());
  }

  @AfterEach
  void tearDown() {
    reset(oAuth2AuthorizedClientService, authenticationProvider);
  }

  @Nested
  @DisplayName("Tests for check endpoint")
  class CheckEndpointTest {

    @Test
    @DisplayName("authorizedClientService is called with registrationId")
    void test_authorized_client_service_called_with_registration_id() {
      // given
      var registrationId = "registrationId";

      // when
      restAssuredUtils.doGet("/" + registrationId);

      // then
      verify(oAuth2AuthorizedClientService).loadAuthorizedClient(eq(registrationId), any());
    }

    @Test
    @DisplayName("authorizedClientService is called with current user's name")
    void test_authorized_client_service_called_with_username() {
      // when
      restAssuredUtils.doGet("/registrationId");

      // then
      verify(oAuth2AuthorizedClientService).loadAuthorizedClient(any(), eq(PRINCIPAL.getName()));
    }

    @Test
    @DisplayName("authenticationProvider is called")
    void test_current_user_supplier_called() {
      // when
      restAssuredUtils.doGet("/registrationId");

      // then
      verify(authenticationProvider).provideForGrant(AUTHORIZATION_CODE);
    }

    @Test
    @DisplayName("200 is returned if client is present")
    void test_200_if_present() {
      // given
      doReturn(mock(OAuth2AuthorizedClient.class)).when(oAuth2AuthorizedClientService).loadAuthorizedClient(any(), any());

      // when
      var result = restAssuredUtils.doGet("/registrationId");

      // then
      result.status(OK);
    }

    @Test
    @DisplayName("result is true if client is present")
    void test_true_if_present() {
      // given
      doReturn(mock(OAuth2AuthorizedClient.class)).when(oAuth2AuthorizedClientService).loadAuthorizedClient(any(), any());

      // when
      var result = restAssuredUtils.doGet("/registrationId");

      // then
      var resultObject = result.extract().body().jsonPath().getObject("exists", Boolean.class);
      assertThat(resultObject).isTrue();
    }

    @Test
    @DisplayName("200 is returned if client is not present")
    void test_200_if_not_present() {
      // when
      var result = restAssuredUtils.doGet("/registrationId");

      // then
      result.status(OK);
    }

    @Test
    @DisplayName("result is false if client is present")
    void test_false_if_present() {
      // when
      var result = restAssuredUtils.doGet("/registrationId");

      // then
      var resultObject = result.extract().body().jsonPath().getObject("exists", Boolean.class);
      assertThat(resultObject).isFalse();
    }
  }

  @Nested
  @DisplayName("Tests for delete endpoint")
  class DeleteEndpointTest {

    @Test
    @DisplayName("authorizedClientService is called with registrationId")
    void test_authorized_client_service_called_with_registration_id() {
      // given
      var registrationId = "registrationId";

      // when
      restAssuredUtils.doDelete("/" + registrationId);

      // then
      verify(oAuth2AuthorizedClientService).removeAuthorizedClient(eq(registrationId), any());
    }

    @Test
    @DisplayName("authorizedClientService is called with current user's name")
    void test_authorized_client_service_called_with_username() {
      // when
      restAssuredUtils.doDelete("/registrationId");

      // then
      verify(oAuth2AuthorizedClientService).removeAuthorizedClient(any(), eq(PRINCIPAL.getName()));
    }

    @Test
    @DisplayName("authenticationProvider is called")
    void test_current_user_supplier_called() {
      // when
      restAssuredUtils.doDelete("/registrationId");

      // then
      verify(authenticationProvider).provideForGrant(AUTHORIZATION_CODE);
    }

    @Test
    @DisplayName("200 is returned")
    void test_200_if_present() {
      // when
      var result = restAssuredUtils.doDelete("/registrationId");

      // then
      result.status(OK);
    }
  }
}
