package rocks.metaldetector.security;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import rocks.metaldetector.persistence.domain.user.AbstractUserEntity;
import rocks.metaldetector.persistence.domain.user.OAuthUserEntity;
import rocks.metaldetector.persistence.domain.user.UserEntity;
import rocks.metaldetector.persistence.domain.user.UserRepository;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CurrentUserSupplierImplTest implements WithAssertions {

  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private CurrentUserSupplierImpl underTest;

  @Mock
  private SecurityContext securityContext;

  @Mock
  private Authentication authentication;

  @Mock
  private UserEntity user;

  @Mock
  private OAuth2AuthenticatedPrincipal oAuthPrincipal;

  @Mock
  private OAuthUserEntity oauthUser;

  @AfterEach
  void tearDown() {
    reset(userRepository, securityContext, authentication, user, oAuthPrincipal, oauthUser);
  }

  @Test
  @DisplayName("should call user repository with public Id from principal to re-fetch the user entity")
  void should_call_user_repository_for_user_entity() {
    // given
    var publicId = "publicId";
    doReturn(publicId).when(user).getPublicId();
    doReturn(Optional.of(user)).when(userRepository).findByPublicId(any());
    doReturn(authentication).when(securityContext).getAuthentication();
    doReturn(user).when(authentication).getPrincipal();

    // when
    try (MockedStatic<SecurityContextHolder> mock = mockStatic(SecurityContextHolder.class)) {
      mock.when(SecurityContextHolder::getContext).thenReturn(securityContext);
      underTest.get();
    }

    // then
    verify(userRepository).findByPublicId(publicId);
  }

  @Test
  @DisplayName("should return user entity")
  void should_return_user_entity() {
    // given
    doReturn(Optional.of(user)).when(userRepository).findByPublicId(any());
    doReturn(authentication).when(securityContext).getAuthentication();
    doReturn(user).when(authentication).getPrincipal();

    // when
    AbstractUserEntity result;
    try (MockedStatic<SecurityContextHolder> mock = mockStatic(SecurityContextHolder.class)) {
      mock.when(SecurityContextHolder::getContext).thenReturn(securityContext);
      result = underTest.get();
    }

    // then
    assertThat(result).isEqualTo(user);
  }

  @Test
  @DisplayName("should throw exception if public id from user entity principal is not found via user repository")
  void should_throw_exception_for_user_entity() {
    // given
    doReturn(Optional.empty()).when(userRepository).findByPublicId(any());
    doReturn(authentication).when(securityContext).getAuthentication();
    doReturn(user).when(authentication).getPrincipal();

    // when
    Throwable throwable;
    try (MockedStatic<SecurityContextHolder> mock = mockStatic(SecurityContextHolder.class)) {
      mock.when(SecurityContextHolder::getContext).thenReturn(securityContext);
      throwable = catchThrowable(() -> underTest.get());
    }

    // then
    assertThat(throwable).isInstanceOf(RuntimeException.class);
  }

  @Test
  @DisplayName("should call user repository with public Id from principal to re-fetch the oauth user entity")
  void should_call_user_repository_for_oauth_user_entity() {
    // given
    var email = "mail@mail.mail";
    doReturn(email).when(oAuthPrincipal).getAttribute(any());
    doReturn(Optional.of(oauthUser)).when(userRepository).findByEmail(any());
    doReturn(authentication).when(securityContext).getAuthentication();
    doReturn(oAuthPrincipal).when(authentication).getPrincipal();

    // when
    try (MockedStatic<SecurityContextHolder> mock = mockStatic(SecurityContextHolder.class)) {
      mock.when(SecurityContextHolder::getContext).thenReturn(securityContext);
      underTest.get();
    }

    // then
    verify(userRepository).findByEmail(email);
  }

  @Test
  @DisplayName("should get correct attribute from oauth principal")
  void should_get_email_from_oauth_principal() {
    // given
    doReturn(Optional.of(oauthUser)).when(userRepository).findByEmail(any());
    doReturn(authentication).when(securityContext).getAuthentication();
    doReturn(oAuthPrincipal).when(authentication).getPrincipal();

    // when
    try (MockedStatic<SecurityContextHolder> mock = mockStatic(SecurityContextHolder.class)) {
      mock.when(SecurityContextHolder::getContext).thenReturn(securityContext);
      underTest.get();
    }

    // then
    verify(oAuthPrincipal).getAttribute("email");
  }

  @Test
  @DisplayName("should return oauth user entity")
  void should_return_oauth_user_entity() {
    // given
    doReturn(Optional.of(oauthUser)).when(userRepository).findByEmail(any());
    doReturn(authentication).when(securityContext).getAuthentication();
    doReturn(oAuthPrincipal).when(authentication).getPrincipal();

    // when
    AbstractUserEntity result;
    try (MockedStatic<SecurityContextHolder> mock = mockStatic(SecurityContextHolder.class)) {
      mock.when(SecurityContextHolder::getContext).thenReturn(securityContext);
      result = underTest.get();
    }

    // then
    assertThat(result).isEqualTo(oauthUser);
  }

  @Test
  @DisplayName("should throw exception if public id from oauth user entity principal is not found via user repository")
  void should_throw_exception_for_oauth_user_entity() {
    // given
    doReturn(Optional.empty()).when(userRepository).findByEmail(any());
    doReturn(authentication).when(securityContext).getAuthentication();
    doReturn(oAuthPrincipal).when(authentication).getPrincipal();

    // when
    Throwable throwable;
    try (MockedStatic<SecurityContextHolder> mock = mockStatic(SecurityContextHolder.class)) {
      mock.when(SecurityContextHolder::getContext).thenReturn(securityContext);
      throwable = catchThrowable(() -> underTest.get());
    }

    // then
    assertThat(throwable).isInstanceOf(RuntimeException.class);
  }

  @Test
  @DisplayName("should return null for anonymousUser")
  void should_return_null() {
    // given
    doReturn(authentication).when(securityContext).getAuthentication();
    doReturn(null).when(authentication).getPrincipal();

    // when
    AbstractUserEntity result;
    try (MockedStatic<SecurityContextHolder> mock = mockStatic(SecurityContextHolder.class)) {
      mock.when(SecurityContextHolder::getContext).thenReturn(securityContext);
      result = underTest.get();
    }

    // then
    assertThat(result).isNull();
  }
}