package rocks.metaldetector.security;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import rocks.metaldetector.persistence.domain.user.UserEntity;
import rocks.metaldetector.persistence.domain.user.UserRepository;
import rocks.metaldetector.service.user.UserEntityFactory;
import rocks.metaldetector.testutil.WithIntegrationTestConfig;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@SpringJUnitConfig
@ExtendWith(MockitoExtension.class)
class CurrentUserSupplierImplIntegrationTest implements WithAssertions, WithIntegrationTestConfig {

  private static final String USERNAME = "user";
  private static final String EMAIL = "user@mail.com";
  private static final String PUBLIC_USER_ID = UUID.randomUUID().toString();
  private static final UserEntity USER = UserEntityFactory.createUser(USERNAME, EMAIL);

  private CurrentUserSupplier underTest;

  @Mock
  private UserRepository userRepository;

  @BeforeEach
  void setup() {
    underTest = new CurrentUserSupplierImpl(userRepository);
  }

  @Test
  @DisplayName("should call user repository with public Id from principal to re-fetch the user entity")
  @WithUserDetails
  void should_call_user_repository() {
    // given
    doReturn(Optional.of(USER)).when(userRepository).findByPublicId(anyString());

    // when
    underTest.get();

    // then
    verify(userRepository).findByPublicId(PUBLIC_USER_ID);
  }

  @Test
  @DisplayName("should return user entity")
  @WithUserDetails
  void should_return_user_entity() {
    // given
    doReturn(Optional.of(USER)).when(userRepository).findByPublicId(anyString());

    // when
    var result = underTest.get();

    // then
    assertThat(result).isNotNull();
    assertThat(result.getUsername()).isEqualTo(USERNAME);
    assertThat(result.getEmail()).isEqualTo(EMAIL);
  }

  @Test
  @DisplayName("should throw exception if public id from principal is not found via user repository")
  @WithUserDetails
  void should_throw_exception() {
    // given
    doReturn(Optional.empty()).when(userRepository).findByPublicId(anyString());

    // when
    Throwable throwable = catchThrowable(() -> underTest.get());

    // then
    assertThat(throwable).isInstanceOf(RuntimeException.class);
  }

  @Test
  @DisplayName("should return null for anonymousUser")
  @WithAnonymousUser
  void should_return_null() {
    // when
    var result = underTest.get();

    // then
    assertThat(result).isNull();
  }

  @TestConfiguration
  static class TestBeanConfiguration {

    @Bean
    UserDetailsService userDetailsService() {
      USER.setPublicId(PUBLIC_USER_ID);
      return username -> USER;
    }
  }
}