package rocks.metaldetector.security;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.security.MessageDigest;
import java.util.Random;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class NonceSupplierImplTest implements WithAssertions {

  @Mock
  private Random secureRandom;

  @Mock
  private MessageDigest messageDigest;

  @InjectMocks
  private NonceSupplierImpl underTest;

  @BeforeEach
  void setup() {
    doReturn("bytes".getBytes()).when(messageDigest).digest(any());
  }

  @AfterEach
  void tearDown() {
    reset(secureRandom, messageDigest);
  }

  @Test
  @DisplayName("secureRandom is called with byte array initialized with zeros")
  void test_secure_random_called() {
    // given
    ArgumentCaptor<byte[]> argumentCaptor = ArgumentCaptor.forClass(byte[].class);

    // when
    underTest.get();

    // then
    verify(secureRandom).nextBytes(argumentCaptor.capture());
    byte[] bytes = argumentCaptor.getValue();
    assertThat(bytes).isEqualTo(new byte[32]);
  }

  @Test
  @DisplayName("messageDigest is called with byte array (altered by secureRandom)")
  void test_message_digest_called() {
    // when
    underTest.get();

    // then
    verify(messageDigest).digest(new byte[32]);
  }

  @Test
  @DisplayName("Byte64-encoded string is returned")
  void test_byte64_encoded_string_returned() {
    // given
    var expectedResult = "Ynl0ZXM=";

    // when
    var result = underTest.get();

    // then
    assertThat(result).isEqualTo(expectedResult);
  }
}