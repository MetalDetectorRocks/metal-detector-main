package rocks.metaldetector.spotify.config;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;
import rocks.metaldetector.support.ApplicationProperties;

import java.util.List;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.reset;

@ExtendWith(MockitoExtension.class)
class SpotifyPropertiesTest implements WithAssertions {

  @Mock
  private Environment environment;

  @Mock
  private ApplicationProperties applicationProperties;

  @InjectMocks
  private SpotifyProperties underTest;

  private static final String HOST = "host";
  private static final int PORT = 666;

  @BeforeEach
  void setup() {
    underTest.setApplicationHostUrl(HOST);
    underTest.setApplicationPort(PORT);
  }

  @AfterEach
  void tearDown() {
    reset(environment);
  }

  @ParameterizedTest
  @ValueSource(strings = {"preview", "prod"})
  @DisplayName("host without port is returned")
  void test_prod_preview(String profile) {
    // given
    doReturn(List.of(profile).toArray(new String[0])).when(environment).getActiveProfiles();

    // when
    var result = underTest.getApplicationHostUrl();

    // then
    assertThat(result).isEqualTo(HOST);
  }

  @ParameterizedTest
  @ValueSource(strings = {"default", "mockmode"})
  @DisplayName("host with port is returned")
  void test_default_mockmode(String profile) {
    // given
    doReturn(List.of(profile).toArray(new String[0])).when(environment).getActiveProfiles();

    // when
    var result = underTest.getApplicationHostUrl();

    // then
    assertThat(result).isEqualTo(HOST + ":" + PORT);
  }
}