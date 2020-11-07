package rocks.metaldetector.security;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import rocks.metaldetector.service.user.UserEntityFactory;
import rocks.metaldetector.testutil.WithIntegrationTestConfig;

@SpringJUnitConfig
class CurrentUserSupplierImplIntegrationTest implements WithAssertions, WithIntegrationTestConfig {

  private static final String USERNAME = "user";
  private static final String EMAIL = "user@mail.com";

  private final CurrentUserSupplierImpl underTest = new CurrentUserSupplierImpl();

  @Test
  @DisplayName("userEntity is returned")
  @WithUserDetails
  void test_user_entity_returned() {
    // when
    var result = underTest.get();

    // then
    assertThat(result).isNotNull();
    assertThat(result.getUsername()).isEqualTo(USERNAME);
    assertThat(result.getEmail()).isEqualTo(EMAIL);
  }

  @Test
  @DisplayName("null is returned for anonymousUser")
  @WithAnonymousUser
  void test_null_returned() {
    // when
    var result = underTest.get();

    // then
    assertThat(result).isNull();
  }

  @TestConfiguration
  static class TestBeanConfiguration {

    @Bean
    UserDetailsService userDetailsService() {
      return service -> UserEntityFactory.createUser(USERNAME, EMAIL);
    }
  }
}