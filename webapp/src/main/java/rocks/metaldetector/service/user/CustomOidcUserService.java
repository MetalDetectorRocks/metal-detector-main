package rocks.metaldetector.service.user;

import lombok.AllArgsConstructor;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;
import rocks.metaldetector.persistence.domain.user.OAuthUserEntity;
import rocks.metaldetector.persistence.domain.user.UserRepository;

@Service
@AllArgsConstructor
public class CustomOidcUserService implements OAuth2UserService<OidcUserRequest, OidcUser> {

  private final UserRepository userRepository;
  private final OidcUserService oidcUserService;

  @Override
  public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
    OidcUser user = oidcUserService.loadUser(userRequest);
    OidcUserInfo userInfo = user.getUserInfo();

    if (!userAlreadyExistsByEmail(userInfo.getGivenName(), userInfo.getEmail())) {
      createOAuthUser(userInfo);
    }

    return user;
  }

  private void createOAuthUser(OidcUserInfo userInfo) {
    OAuthUserEntity oAuthUserEntity = OAuthUserEntity.builder()
        .username(userInfo.getGivenName())
        .avatar(userInfo.getPicture())
        .email(userInfo.getEmail())
        .build();

    userRepository.save(oAuthUserEntity);
  }

  private boolean userAlreadyExistsByEmail(String username, String email) {
    return userRepository.existsByEmail(email) || userRepository.existsByEmail(username);
  }
}
