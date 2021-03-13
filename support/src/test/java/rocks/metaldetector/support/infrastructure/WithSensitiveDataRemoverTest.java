package rocks.metaldetector.support.infrastructure;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

class WithSensitiveDataRemoverTest implements WithAssertions {

  private final WithSensitiveDataRemover underTest = new WithSensitiveDataRemover() {};

  @Nested
  @DisplayName("Tokens from Authorization header are removed")
  class RemoveTokenFromHeaderTest {

    @Test
    @DisplayName("Method is null safe")
    void test_is_null_safe() {
      // when
      String result = underTest.removeTokenFromHeader(null);

      // then
      assertThat(result).isNull();
    }

    @Test
    @DisplayName("If input contains authorization header, the value is removed")
    void authorization_header_value_is_removed() {
      // given
      String givenHeaderString = "[... Authorization:\"TOKEN_STRING\" ...]";
      String expectedHeaderString = "[... Authorization:\"REMOVED_FOR_LOGGING\" ...]";

      // when
      String result = underTest.removeTokenFromHeader(givenHeaderString);

      // then
      assertThat(result).isEqualTo(expectedHeaderString);
    }

    @Test
    @DisplayName("If input does not contain authorization header, the input is returned")
    void input_is_returned() {
      // given
      String givenHeaderString = "... Accept:\"application/json\" ...";

      // when
      String result = underTest.removeTokenFromHeader(givenHeaderString);

      // then
      assertThat(result).isEqualTo(givenHeaderString);
    }
  }

  @Nested
  @TestInstance(TestInstance.Lifecycle.PER_CLASS)
  @DisplayName("Sensitive data is removed from the payload")
  class RemoveSensitiveDataFromPayloadTest {

    @Test
    @DisplayName("Should be null-safe")
    void test_is_null_safe() throws JsonProcessingException {
      // when
      String result = underTest.removeSensitiveDataFromPayload(null);

      // then
      assertThat(result).isNull();
    }

    @Test
    @DisplayName("Should remove sensitive data only in payload block")
    void remove_sensitive_data_only_in_payload() throws JsonProcessingException {
      // given
      String given = "POST /rest/v1/users, token=abc, plainPassword=abc, payload={}";

      // when
      String result = underTest.removeSensitiveDataFromPayload(given);

      // then
      assertThat(result).isEqualTo(given);
    }

    @ParameterizedTest(name = "Should remove sensitive data from payload block")
    @MethodSource("messageProvider")
    @DisplayName("Should remove sensitive data from payload block")
    void remove_sensitive_data_from_payload(String given, String expected) throws JsonProcessingException {
      // when
      String result = underTest.removeSensitiveDataFromPayload(given);

      // then
      assertThat(result).isEqualTo(expected);
    }

    private Stream<Arguments> messageProvider() {
      return Stream.of(
              Arguments.of(
                      "POST /rest/v1/users, payload={\"username\":\"TestUser\",\"plainPassword\":\"foobar123\",\"verifyPlainPassword\":\"foobar123\"}",
                      "POST /rest/v1/users, payload={\"username\":\"TestUser\",\"plainPassword\":\"REMOVED_FOR_LOGGING\",\"verifyPlainPassword\":\"REMOVED_FOR_LOGGING\"}"
              ),
              Arguments.of(
                      "POST /rest/v1/users, payload={\"username\":\"TestUser\",\"code\":\"foobar123\"}",
                      "POST /rest/v1/users, payload={\"username\":\"TestUser\",\"code\":\"REMOVED_FOR_LOGGING\"}"
              ),
              Arguments.of(
                      "POST /rest/v1/users, payload={\"token\":\"foobar123\"}",
                      "POST /rest/v1/users, payload={\"token\":\"REMOVED_FOR_LOGGING\"}"
              ),
              Arguments.of(
                      "POST /rest/v1/users, payload={\"token\":\"foobar\\\"123\"}",
                      "POST /rest/v1/users, payload={\"token\":\"REMOVED_FOR_LOGGING\"}"
              )
      );
    }

    @Test
    @DisplayName("Should not remove sensitive data from payload block")
    void input_is_returned() throws JsonProcessingException {
      // given
      String given = "POST /rest/v1/users, payload={\"username\":\"Testuser\",\"email\":\"test@example.com\"}";

      // when
      String result = underTest.removeSensitiveDataFromPayload(given);

      // then
      assertThat(result).isEqualTo(given);
    }
  }
}
