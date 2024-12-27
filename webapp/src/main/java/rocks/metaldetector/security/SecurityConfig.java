package rocks.metaldetector.security;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import rocks.metaldetector.security.handler.CustomAccessDeniedHandler;
import rocks.metaldetector.security.handler.CustomAuthenticationFailureHandler;
import rocks.metaldetector.security.handler.CustomAuthenticationSuccessHandler;
import rocks.metaldetector.security.handler.CustomLogoutSuccessHandler;
import rocks.metaldetector.service.user.UserService;
import rocks.metaldetector.support.SecurityProperties;

import javax.sql.DataSource;
import java.time.Duration;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static rocks.metaldetector.persistence.domain.user.UserRole.ROLE_ADMINISTRATOR;
import static rocks.metaldetector.support.Endpoints.AntPattern.ACTUATOR_ENDPOINTS;
import static rocks.metaldetector.support.Endpoints.AntPattern.ADMIN;
import static rocks.metaldetector.support.Endpoints.AntPattern.GUEST_ONLY_PAGES;
import static rocks.metaldetector.support.Endpoints.AntPattern.PUBLIC_PAGES;
import static rocks.metaldetector.support.Endpoints.AntPattern.RESOURCES;
import static rocks.metaldetector.support.Endpoints.AntPattern.REST_ENDPOINTS;
import static rocks.metaldetector.support.Endpoints.Authentication.LOGIN;
import static rocks.metaldetector.support.Endpoints.Frontend.HOME;
import static rocks.metaldetector.support.Endpoints.Frontend.LOGOUT;
import static rocks.metaldetector.support.Endpoints.Rest.AUTHENTICATION;
import static rocks.metaldetector.support.Endpoints.Rest.NOTIFICATION_TELEGRAM;
import static rocks.metaldetector.support.Endpoints.Rest.RELEASES;
import static rocks.metaldetector.support.Endpoints.Rest.SEARCH_ARTIST;
import static rocks.metaldetector.support.Endpoints.Rest.TOP_ARTISTS;
import static rocks.metaldetector.support.Endpoints.Rest.TOP_UPCOMING_RELEASES;

@Configuration
@EnableWebSecurity
@ConditionalOnProperty(
    name = "rocks.metaldetector.security.enabled",
    havingValue = "true",
    matchIfMissing = true
)
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  private final UserService userService;
  private final DataSource dataSource;
  private final SecurityProperties securityProperties;
  private final OAuth2AuthorizedClientService oAuth2AuthorizedClientService;
  private final OAuth2AuthorizedClientRepository oAuth2AuthorizedClientRepository;
  private final OAuth2UserService<OidcUserRequest, OidcUser> customOidcUserService;
  private final OAuth2AuthorizationRequestResolver oAuth2AuthorizationRequestResolver;

  @Value("${telegram.bot-id}")
  private String botId;

  @Bean
  SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .csrf(customizer -> customizer.ignoringRequestMatchers(REST_ENDPOINTS, ACTUATOR_ENDPOINTS))
        .authorizeHttpRequests(customizer -> customizer
            .requestMatchers(ADMIN).hasRole(ROLE_ADMINISTRATOR.getName())
            .requestMatchers(RESOURCES).permitAll()
            .requestMatchers(GUEST_ONLY_PAGES).permitAll()
            .requestMatchers(PUBLIC_PAGES).permitAll()
            .requestMatchers(GET, RELEASES).permitAll()
            .requestMatchers(GET, TOP_UPCOMING_RELEASES).permitAll()
            .requestMatchers(GET, SEARCH_ARTIST).permitAll()
            .requestMatchers(GET, TOP_ARTISTS).permitAll()
            .requestMatchers(GET, AUTHENTICATION).permitAll()
            .requestMatchers(ACTUATOR_ENDPOINTS).permitAll()
            .requestMatchers(NOTIFICATION_TELEGRAM + "/" + botId).permitAll()
            .anyRequest().authenticated()
        )
        .formLogin(customizer -> customizer
            .loginPage(LOGIN)
            .loginProcessingUrl(LOGIN)
            .successHandler(new CustomAuthenticationSuccessHandler(new SavedRequestAwareAuthenticationSuccessHandler()))
            .failureHandler(new CustomAuthenticationFailureHandler())
        )
        .oauth2Login(customizer -> customizer
            .loginPage(LOGIN)
            .successHandler(new CustomAuthenticationSuccessHandler(new SavedRequestAwareAuthenticationSuccessHandler()))
            .failureHandler(new CustomAuthenticationFailureHandler())
            .userInfoEndpoint(infoEndpointCustomizer -> infoEndpointCustomizer
                .oidcUserService(customOidcUserService)
            )
            .authorizationEndpoint(authorizationEndpointCustomizer -> authorizationEndpointCustomizer
                .authorizationRequestResolver(oAuth2AuthorizationRequestResolver)
            )
        )
        .oauth2Client(customizer -> customizer
            .authorizedClientService(oAuth2AuthorizedClientService)
            .authorizedClientRepository(oAuth2AuthorizedClientRepository)
        )
        .rememberMe(customizer -> customizer
            .tokenValiditySeconds((int) Duration.ofDays(14).toSeconds())
            .tokenRepository(jdbcTokenRepository())
            .userDetailsService(userService)
            .key(securityProperties.getRememberMeSecret())
        )
        .logout(customizer -> customizer
            .logoutUrl(LOGOUT).permitAll()
            .invalidateHttpSession(true)
            .clearAuthentication(true)
            .deleteCookies("JSESSIONID", "remember-me")
            .logoutSuccessHandler(new CustomLogoutSuccessHandler())
        )
        .cors(customizer -> {
        })
        .headers(customizer ->
            // These headers are set in the proxy, so disabled here
            customizer.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable)
                .xssProtection(HeadersConfigurer.XXssConfig::disable)
                .contentTypeOptions(HeadersConfigurer.ContentTypeOptionsConfig::disable)
                .httpStrictTransportSecurity(HeadersConfigurer.HstsConfig::disable)
                .permissionsPolicyHeader((it) -> it.policy("interest-cohort=()")))
        .exceptionHandling(customizer -> customizer
            .accessDeniedHandler(new CustomAccessDeniedHandler(() -> SecurityContextHolder.getContext().getAuthentication()))
            .defaultAuthenticationEntryPointFor(new LoginUrlAuthenticationEntryPoint(LOGIN), new AntPathRequestMatcher(HOME))
            .defaultAuthenticationEntryPointFor(new HttpStatusEntryPoint(UNAUTHORIZED), new AntPathRequestMatcher(REST_ENDPOINTS)));
    return http.build();
  }

  @Bean
  public JdbcTokenRepositoryImpl jdbcTokenRepository() {
    JdbcTokenRepositoryImpl jdbcTokenRepository = new JdbcTokenRepositoryImpl();
    jdbcTokenRepository.setCreateTableOnStartup(false);
    jdbcTokenRepository.setDataSource(dataSource);
    return jdbcTokenRepository;
  }

  @Bean
  public FilterRegistrationBean<XSSFilter> xssFilterRegistrationBean(XSSFilter xssFilter) {
    return new FilterRegistrationBean<>(xssFilter);
  }
}
