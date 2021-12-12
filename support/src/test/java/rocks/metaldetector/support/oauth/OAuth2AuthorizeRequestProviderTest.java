package rocks.metaldetector.support.oauth;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.AuthorizationGrantType;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.springframework.security.core.authority.AuthorityUtils.createAuthorityList;
import static org.springframework.security.oauth2.core.AuthorizationGrantType.AUTHORIZATION_CODE;
import static org.springframework.security.oauth2.core.AuthorizationGrantType.CLIENT_CREDENTIALS;

@ExtendWith(MockitoExtension.class)
class OAuth2AuthorizeRequestProviderTest implements WithAssertions {

  private static final AnonymousAuthenticationToken PRINCIPAL = new AnonymousAuthenticationToken("key", "anonymous", createAuthorityList("ROLE_ANONYMOUS"));

  @Mock
  private OAuth2AuthenticationProvider authenticationProvider;

  @InjectMocks
  private OAuth2AuthorizeRequestProvider underTest;

  @BeforeEach
  void setup() {
    doReturn(PRINCIPAL).when(authenticationProvider).provideForGrant(any());
  }

  @AfterEach
  void tearDown() {
    reset(authenticationProvider);
  }

  @Test
  @DisplayName("authenticationProvider is called for grant 'client_credentials'")
  void test_authentication_provider_called_client_credentials() {
    // when
    underTest.provideForGrant(CLIENT_CREDENTIALS, null, "registrationId");

    // then
    verify(authenticationProvider).provideForGrant(CLIENT_CREDENTIALS);
  }

  @Test
  @DisplayName("expected request is returned for grant 'client_credentials'")
  void test_expected_request_returned_client_credentials() {
    // given
    var registrationId = "registrationId";

    // when
    var result = underTest.provideForGrant(CLIENT_CREDENTIALS, null, registrationId);

    // then
    assertThat(result.getClientRegistrationId()).isEqualTo(registrationId);
    assertThat(result.getPrincipal()).isEqualTo(PRINCIPAL);
  }

  @Test
  @DisplayName("authenticationProvider is called for grant 'authorization_code'")
  void test_authentication_provider_called_authorization_code() {
    // given
    var authorizedClient = mock(OAuth2AuthorizedClient.class);
    doReturn(mock(ClientRegistration.class)).when(authorizedClient).getClientRegistration();

    // when
    underTest.provideForGrant(AUTHORIZATION_CODE, authorizedClient, "registrationId");

    // then
    verify(authenticationProvider).provideForGrant(AUTHORIZATION_CODE);
  }

  @Test
  @DisplayName("expected request is returned for grant 'authorization_code'")
  void test_expected_request_returned_authorization_code() {
    // given
    var authorizedClient = mock(OAuth2AuthorizedClient.class);
    doReturn(mock(ClientRegistration.class)).when(authorizedClient).getClientRegistration();

    // when
    var result = underTest.provideForGrant(AUTHORIZATION_CODE, authorizedClient, "registrationId");

    // then
    assertThat(result.getAuthorizedClient()).isEqualTo(authorizedClient);
    assertThat(result.getPrincipal()).isEqualTo(PRINCIPAL);
  }

  @ParameterizedTest
  @DisplayName("exception is thrown for other grants")
  @ValueSource(strings = {"refresh_token", "password", "implicit", "urn:ietf:params:oauth:grant-type:jwt-bearer"})
  void test_exception_thrown_for_other_grants(String grant) {
    // when
    var throwable = catchThrowable(() -> underTest.provideForGrant(new AuthorizationGrantType(grant), null, null));

    // then
    assertThat(throwable).isInstanceOf(IllegalArgumentException.class);
    assertThat(throwable).hasMessageContaining(grant);
  }
}