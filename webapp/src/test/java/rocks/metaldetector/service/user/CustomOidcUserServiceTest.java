package rocks.metaldetector.service.user;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import rocks.metaldetector.persistence.domain.user.OAuthUserEntity;
import rocks.metaldetector.persistence.domain.user.UserRepository;
import rocks.metaldetector.service.user.events.UserCreationEvent;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atMost;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static rocks.metaldetector.persistence.domain.user.UserRole.ROLE_USER;

@ExtendWith(MockitoExtension.class)
class CustomOidcUserServiceTest implements WithAssertions {

  @Mock
  private UserRepository userRepository;

  @Mock
  private OidcUserService oidcUserService;

  @Mock
  private ApplicationEventPublisher applicationEventPublisher;

  @InjectMocks
  private CustomOidcUserService underTest;

  @AfterEach
  void tearDown() {
    reset(userRepository, oidcUserService, applicationEventPublisher);
  }

  @DisplayName("Calls oidcUserService with request")
  @Test
  void should_call_oidc_user_service() {
    // given
    var mockRequest = mock(OidcUserRequest.class);
    var mockResponse = mock(OidcUser.class);
    doReturn(mockResponse).when(oidcUserService).loadUser(any());
    doReturn(mock(OidcUserInfo.class)).when(mockResponse).getUserInfo();
    doReturn(true).when(userRepository).existsByEmail(any());

    // when
    underTest.loadUser(mockRequest);

    // then
    verify(oidcUserService).loadUser(mockRequest);
  }

  @DisplayName("Calls userRepository maximum twice to check if the user exists")
  @Test
  void should_call_user_repository_for_user_existence() {
    // given
    var mockRequest = mock(OidcUserRequest.class);
    var mockResponse = mock(OidcUser.class);
    var mockUserInfo = mock(OidcUserInfo.class);
    doReturn("username").when(mockUserInfo).getGivenName();
    doReturn("test@test.test").when(mockUserInfo).getEmail();
    doReturn(mockResponse).when(oidcUserService).loadUser(any());
    doReturn(mockUserInfo).when(mockResponse).getUserInfo();
    doReturn(false).when(userRepository).existsByEmail(any());

    // when
    underTest.loadUser(mockRequest);

    // then
    verify(userRepository, atMost(2)).existsByEmail(anyString());
  }

  @DisplayName("Calls userRepository to save user")
  @Test
  void should_call_user_repository_to_save_user() {
    // given
    ArgumentCaptor<OAuthUserEntity> userEntityCaptor = ArgumentCaptor.forClass(OAuthUserEntity.class);
    var mockRequest = mock(OidcUserRequest.class);
    var mockResponse = mock(OidcUser.class);
    var mockUserInfo = mock(OidcUserInfo.class);
    var username = "username";
    var email = "test@test.test";
    var avatar = "avatar";
    doReturn(username).when(mockUserInfo).getGivenName();
    doReturn(email).when(mockUserInfo).getEmail();
    doReturn(avatar).when(mockUserInfo).getPicture();
    doReturn(mockResponse).when(oidcUserService).loadUser(any());
    doReturn(mockUserInfo).when(mockResponse).getUserInfo();

    // when
    underTest.loadUser(mockRequest);

    // then
    verify(userRepository).save(userEntityCaptor.capture());
    OAuthUserEntity passedUserEntity = userEntityCaptor.getValue();
    assertThat(passedUserEntity.getUsername()).isEqualTo(username);
    assertThat(passedUserEntity.getEmail()).isEqualTo(email);
    assertThat(passedUserEntity.getAvatar()).isEqualTo(avatar);
    assertThat(passedUserEntity.getUserRoles()).containsExactly(ROLE_USER);
    assertThat(passedUserEntity.isEnabled()).isTrue();
  }

  @Test
  @DisplayName("oAuthUser is returned")
  void test_oauth_user_returned() {
    // given
    var mockRequest = mock(OidcUserRequest.class);
    var mockResponse = mock(OidcUser.class);
    doReturn(mockResponse).when(oidcUserService).loadUser(any());
    doReturn(mock(OidcUserInfo.class)).when(mockResponse).getUserInfo();
    doReturn(true).when(userRepository).existsByEmail(any());

    // when
    var result = underTest.loadUser(mockRequest);

    // then
    assertThat(result).isEqualTo(mockResponse);
  }

  @Test
  @DisplayName("Creation event is published")
  void creation_event_published() {
    // given
    ArgumentCaptor<UserCreationEvent> argumentCaptor = ArgumentCaptor.forClass(UserCreationEvent.class);
    OAuthUserEntity user = OAuthUserFactory.createUser("user", "user@user.user");
    var mockResponse = mock(OidcUser.class);
    var mockUserInfo = mock(OidcUserInfo.class);
    doReturn(user).when(userRepository).save(any(OAuthUserEntity.class));
    doReturn(mockResponse).when(oidcUserService).loadUser(any());
    doReturn(mockUserInfo).when(mockResponse).getUserInfo();
    doReturn(user.getUsername()).when(mockUserInfo).getGivenName();
    doReturn(user.getEmail()).when(mockUserInfo).getEmail();

    // when
    underTest.loadUser(mock(OidcUserRequest.class));

    // then
    verify(applicationEventPublisher).publishEvent(argumentCaptor.capture());
    UserCreationEvent userCreationEvent = argumentCaptor.getValue();

    assertThat(userCreationEvent.getSource()).isEqualTo(underTest);
    assertThat(userCreationEvent.getUserEntity()).isEqualTo(user);
  }
}
