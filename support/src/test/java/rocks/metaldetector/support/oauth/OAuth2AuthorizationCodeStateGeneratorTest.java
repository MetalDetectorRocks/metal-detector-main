package rocks.metaldetector.support.oauth;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.keygen.StringKeyGenerator;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class OAuth2AuthorizationCodeStateGeneratorTest implements WithAssertions {

  @InjectMocks
  private OAuth2AuthorizationCodeStateGenerator underTest;

  @Mock
  private StringKeyGenerator stringKeyGenerator;

  @AfterEach
  void tearDown() {
    reset(stringKeyGenerator);
  }

  @Test
  @DisplayName("If state is null, new state is generated and returned")
  void test_generate_state() {
    // given
    var state = "state";
    doReturn(state).when(stringKeyGenerator).generateKey();

    // when
    var result = underTest.generateState();

    // then
    verify(stringKeyGenerator).generateKey();
    assertThat(result).isEqualTo(state);
  }

  @Test
  @DisplayName("If state is not null, the same state is returned")
  void test_generate_state_not_null() {
    // given
    var state = "state";
    underTest.setState(state);

    // when
    var result = underTest.generateState();

    // then
    verifyNoInteractions(stringKeyGenerator);
    assertThat(result).isEqualTo(state);
  }
}
