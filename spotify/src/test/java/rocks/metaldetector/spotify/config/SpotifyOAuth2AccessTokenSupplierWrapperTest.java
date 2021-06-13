package rocks.metaldetector.spotify.config;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import rocks.metaldetector.support.oauth.OAuth2AccessTokenSupplier;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SpotifyOAuth2AccessTokenSupplierWrapperTest implements WithAssertions {

  @Mock
  private OAuth2AccessTokenSupplier accessTokenSupplier;

  @InjectMocks
  private SpotifyOAuth2AccessTokenSupplierWrapper underTest;

  @AfterEach
  void tearDown() {
    reset(accessTokenSupplier);
  }

  @Test
  @DisplayName("accessTokenSupplier is called on get")
  void test_get_access_token_supplier_called() {
    // when
    underTest.get();

    // then
    verify(accessTokenSupplier).get();
  }

  @Test
  @DisplayName("accessToken is returned on get")
  void test_get_access_token_returned() {
    // given
    var token = "token";
    doReturn(token).when(accessTokenSupplier).get();

    // when
    var result = underTest.get();

    // then
    assertThat(result).isEqualTo(token);
  }

  @Test
  @DisplayName("accessTokenSupplier is called on set")
  void test_set_access_token_supplier_called() {
    // given
    var registrationId = "registrationId";

    // when
    underTest.setRegistrationId(registrationId);

    // then
    verify(accessTokenSupplier).setRegistrationId(registrationId);
  }
}
