package rocks.metaldetector.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationCodeGrantFilter;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestRedirectFilter;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import rocks.metaldetector.security.filter.*;
import rocks.metaldetector.security.handler.CustomLogoutHandler;
import rocks.metaldetector.security.handler.CustomOAuth2AuthenticationSuccessHandler;
import rocks.metaldetector.security.handler.CustomUsernamePasswordAuthenticationSuccessHandler;
import rocks.metaldetector.support.Endpoints;

import java.util.List;
import java.util.stream.Stream;

import static jakarta.servlet.http.HttpServletResponse.SC_OK;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;
import static rocks.metaldetector.persistence.domain.user.UserRole.ROLE_ADMINISTRATOR;
import static rocks.metaldetector.security.CustomCookieAuthorizationRequestRepository.SESSION_STATE_COOKIE_NAME;
import static rocks.metaldetector.service.auth.RefreshTokenService.REFRESH_TOKEN_COOKIE_NAME;
import static rocks.metaldetector.support.Endpoints.AntPattern.*;
import static rocks.metaldetector.support.Endpoints.Rest.*;

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

  private final OAuth2AuthorizedClientService oAuth2AuthorizedClientService;
  private final OAuth2AuthorizedClientRepository oAuth2AuthorizedClientRepository;
  private final OAuth2UserService<OidcUserRequest, OidcUser> customOidcUserService;
  private final OAuth2AuthorizationRequestResolver oAuth2AuthorizationRequestResolver;
  private final CustomAuthorizationFilter authenticationFilter;
  private final AuthenticationConfiguration authenticationConfiguration;
  private final CustomUsernamePasswordAuthenticationSuccessHandler usernamePasswordAuthenticationSuccessHandler;
  private final CustomOAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;
  private final CustomLogoutHandler customLogoutHandler;
  private final ObjectMapper objectMapper;
  private final OAuth2AuthorizationCodeSaveRequestFilter authorizationCodeSaveRequestFilter;
  private final OAuth2AuthorizationCodeLoginFilter authorizationCodeLoginFilter;
  private final CustomCookieAuthorizationRequestRepository cookieAuthorizationRequestRepository;

  @Value("${telegram.bot-id}")
  private String botId;

  @Value("${frontend.origin}")
  private String frontendOrigin;

  @Bean
  SecurityFilterChain securityFilterChain(HttpSecurity http, CorsConfigurationSource corsConfigurationSource) throws Exception {
    http
        .csrf(customizer -> customizer.ignoringRequestMatchers(REST_ENDPOINTS)) // TODO DanielW: enable csrf
        .cors(customizer -> customizer.configurationSource(corsConfigurationSource))
        .sessionManagement(customizer -> customizer.sessionCreationPolicy(STATELESS))
        .authorizeHttpRequests(customizer -> customizer
            .requestMatchers(RESOURCES).permitAll()
            .requestMatchers(GUEST_ONLY_PAGES).permitAll()
            .requestMatchers(PUBLIC_PAGES).permitAll()
            .requestMatchers(ACTUATOR_ENDPOINTS,
                             NOTIFICATION_TELEGRAM + "/" + botId,
                             Endpoints.Rest.LOGIN,
                             REGISTER,
                             REGISTRATION_VERIFICATION,
                             REQUEST_PASSWORD_RESET,
                             RESET_PASSWORD,
                             LOGOUT,
                             RELEASES,
                             TOP_UPCOMING_RELEASES,
                             SEARCH_ARTIST,
                             TOP_ARTISTS,
                             AUTHENTICATION,
                             REFRESH_ACCESS_TOKEN,
                             CSRF,
                             OAUTH_CALLBACK).permitAll()
            .requestMatchers(FOLLOW_ARTIST + "/**",
                             UNFOLLOW_ARTIST + "/**",
                             DASHBOARD,
                             MY_ARTISTS,
                             SPOTIFY_SAVED_ARTISTS,
                             SPOTIFY_ARTIST_SYNCHRONIZATION,
                             NOTIFICATION_CONFIG,
                             OAUTH_REGISTRATION_ID,
                             TELEGRAM_CONFIG,
                             "/rest/v1/logging/**",
                             CURRENT_USER + "/**").authenticated()
            .requestMatchers(ADMIN,
                             UPDATE_RELEASE,
                             ALL_RELEASES,
                             IMPORT_JOB,
                             COVER_JOB,
                             REGISTRATION_CLEANUP,
                             USERS + "/**",
                             NOTIFICATION_ON_FREQUENCY,
                             NOTIFICATION_ON_RELEASE_DATE,
                             NOTIFICATION_ON_ANNOUNCEMENT_DATE,
                             STATISTICS).hasRole(ROLE_ADMINISTRATOR.getName())
            .anyRequest().denyAll())
        .oauth2Login(oAuth2LoginConfigurer -> oAuth2LoginConfigurer
            .loginPage(frontendOrigin + Endpoints.Authentication.SIGN_IN)
            .successHandler(oAuth2AuthenticationSuccessHandler)
            .userInfoEndpoint(customizer -> customizer.oidcUserService(customOidcUserService))
            .authorizationEndpoint(customizer -> customizer
                .authorizationRequestResolver(oAuth2AuthorizationRequestResolver)
                .authorizationRequestRepository(cookieAuthorizationRequestRepository))
        )
        .oauth2Client(customizer -> customizer
            .authorizedClientService(oAuth2AuthorizedClientService)
            .authorizedClientRepository(oAuth2AuthorizedClientRepository)
        )
        .logout(logout -> logout.permitAll()
            .logoutUrl(LOGOUT)
            .addLogoutHandler(customLogoutHandler)
            .logoutSuccessHandler((request, response, authentication) -> response.setStatus(SC_OK))
            .invalidateHttpSession(true)
            .clearAuthentication(true)
            .deleteCookies("JSESSIONID", REFRESH_TOKEN_COOKIE_NAME, SESSION_STATE_COOKIE_NAME))
        .cors((it) -> {})
        .headers(customizer ->
                     // These headers are set in the proxy, so disabled here
                     customizer.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable)
                         .xssProtection(HeadersConfigurer.XXssConfig::disable)
                         .contentTypeOptions(HeadersConfigurer.ContentTypeOptionsConfig::disable)
                         .httpStrictTransportSecurity(HeadersConfigurer.HstsConfig::disable)
                         .permissionsPolicy((it) -> it.policy("interest-cohort=()")))
        .exceptionHandling((configurer) ->
                               configurer.defaultAuthenticationEntryPointFor(new HttpStatusEntryPoint(UNAUTHORIZED),
                                                                             new AntPathRequestMatcher(REST_ENDPOINTS)))
        .addFilter(customUsernamePasswordAuthFilter())
        .addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class)
        .addFilterBefore(authorizationCodeSaveRequestFilter, OAuth2AuthorizationRequestRedirectFilter.class)
        .addFilterBefore(authorizationCodeLoginFilter, OAuth2AuthorizationCodeGrantFilter.class);
    return http.build();
  }

  @Bean
  public FilterRegistrationBean<XSSFilter> xssFilterRegistrationBean(XSSFilter xssFilter) {
    return new FilterRegistrationBean<>(xssFilter);
  }

  @Bean
  public UsernamePasswordAuthenticationFilter customUsernamePasswordAuthFilter() throws Exception {
    var authenticationFilter = new CustomUsernamePasswordAuthenticationFilter(authenticationConfiguration, objectMapper);
    authenticationFilter.setFilterProcessesUrl(Endpoints.Rest.LOGIN);
    authenticationFilter.setAuthenticationSuccessHandler(usernamePasswordAuthenticationSuccessHandler);
    return authenticationFilter;
  }

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOrigins(Stream.of(
        frontendOrigin,
        "http://localhost:1080"
    ).distinct().toList());
    configuration.setAllowedMethods(List.of("GET", "HEAD", "OPTIONS", "POST", "PUT", "PATCH", "DELETE"));
    configuration.setAllowedHeaders(List.of("cache-control", "pragma", "authorization", "content-type"));
    configuration.setAllowCredentials(true);
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration(REST_ENDPOINTS, configuration);
    return source;
  }
}
