package rocks.metaldetector.service.user;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.oidc.OidcScopes;

import java.util.Arrays;
import java.util.List;

@Configuration
public class OAuth2UserServiceConfig {

    private static final String GOOGLE_SCOPE_PROFILE = "https://www.googleapis.com/auth/userinfo.profile";
    private static final String GOOGLE_SCOPE_EMAIL = "https://www.googleapis.com/auth/userinfo.email";
    private static final List<String> ALLOWED_SCOPES = Arrays.asList(OidcScopes.PROFILE, OidcScopes.EMAIL, GOOGLE_SCOPE_PROFILE, GOOGLE_SCOPE_EMAIL);

    @Bean
    public OidcUserService oidcUserService() {
        OidcUserService oidcUserService = new OidcUserService();
        oidcUserService.setRetrieveUserInfo(oidcUserRequest ->
                oidcUserRequest.getAccessToken().getScopes().stream().anyMatch(ALLOWED_SCOPES::contains));
        return oidcUserService;
    }
}
