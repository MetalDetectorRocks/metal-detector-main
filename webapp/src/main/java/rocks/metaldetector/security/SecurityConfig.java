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
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import rocks.metaldetector.security.filter.CustomUsernamePasswordAuthenticationFilter;
import rocks.metaldetector.security.filter.CustomAuthorizationFilter;
import rocks.metaldetector.security.filter.XSSFilter;
import rocks.metaldetector.security.handler.CustomAuthenticationSuccessHandler;
import rocks.metaldetector.security.handler.CustomLogoutHandler;
import rocks.metaldetector.support.Endpoints;

import static jakarta.servlet.http.HttpServletResponse.SC_OK;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;
import static rocks.metaldetector.persistence.domain.user.UserRole.ROLE_ADMINISTRATOR;
import static rocks.metaldetector.service.auth.RefreshTokenService.REFRESH_TOKEN_COOKIE_NAME;
import static rocks.metaldetector.support.Endpoints.AntPattern.ACTUATOR_ENDPOINTS;
import static rocks.metaldetector.support.Endpoints.AntPattern.ADMIN;
import static rocks.metaldetector.support.Endpoints.AntPattern.GUEST_ONLY_PAGES;
import static rocks.metaldetector.support.Endpoints.AntPattern.PUBLIC_PAGES;
import static rocks.metaldetector.support.Endpoints.AntPattern.RESOURCES;
import static rocks.metaldetector.support.Endpoints.AntPattern.REST_ENDPOINTS;
import static rocks.metaldetector.support.Endpoints.Rest.ALL_RELEASES;
import static rocks.metaldetector.support.Endpoints.Rest.AUTHENTICATION;
import static rocks.metaldetector.support.Endpoints.Rest.COVER_JOB;
import static rocks.metaldetector.support.Endpoints.Rest.CSRF;
import static rocks.metaldetector.support.Endpoints.Rest.CURRENT_USER;
import static rocks.metaldetector.support.Endpoints.Rest.DASHBOARD;
import static rocks.metaldetector.support.Endpoints.Rest.FOLLOW_ARTIST;
import static rocks.metaldetector.support.Endpoints.Rest.IMPORT_JOB;
import static rocks.metaldetector.support.Endpoints.Rest.LOGOUT;
import static rocks.metaldetector.support.Endpoints.Rest.MY_ARTISTS;
import static rocks.metaldetector.support.Endpoints.Rest.NOTIFICATION_CONFIG;
import static rocks.metaldetector.support.Endpoints.Rest.NOTIFICATION_ON_ANNOUNCEMENT_DATE;
import static rocks.metaldetector.support.Endpoints.Rest.NOTIFICATION_ON_FREQUENCY;
import static rocks.metaldetector.support.Endpoints.Rest.NOTIFICATION_ON_RELEASE_DATE;
import static rocks.metaldetector.support.Endpoints.Rest.NOTIFICATION_TELEGRAM;
import static rocks.metaldetector.support.Endpoints.Rest.REFRESH_ACCESS_TOKEN;
import static rocks.metaldetector.support.Endpoints.Rest.OAUTH;
import static rocks.metaldetector.support.Endpoints.Rest.REGISTER;
import static rocks.metaldetector.support.Endpoints.Rest.REGISTRATION_CLEANUP;
import static rocks.metaldetector.support.Endpoints.Rest.RELEASES;
import static rocks.metaldetector.support.Endpoints.Rest.SEARCH_ARTIST;
import static rocks.metaldetector.support.Endpoints.Rest.SPOTIFY_ARTIST_SYNCHRONIZATION;
import static rocks.metaldetector.support.Endpoints.Rest.SPOTIFY_SAVED_ARTISTS;
import static rocks.metaldetector.support.Endpoints.Rest.TELEGRAM_CONFIG;
import static rocks.metaldetector.support.Endpoints.Rest.TOP_ARTISTS;
import static rocks.metaldetector.support.Endpoints.Rest.TOP_UPCOMING_RELEASES;
import static rocks.metaldetector.support.Endpoints.Rest.UNFOLLOW_ARTIST;
import static rocks.metaldetector.support.Endpoints.Rest.UPDATE_RELEASE;
import static rocks.metaldetector.support.Endpoints.Rest.USERS;

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
  private final CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;
  private final CustomLogoutHandler customLogoutHandler;
  private final ObjectMapper objectMapper;

  @Value("${telegram.bot-id}")
  private String botId;

  @Bean
  SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
      .csrf().ignoringRequestMatchers(REST_ENDPOINTS) // TODO DanielW: enable csrf
      .and()
        .sessionManagement().sessionCreationPolicy(STATELESS)
      .and()
        .authorizeHttpRequests()
          .requestMatchers(RESOURCES).permitAll()
          .requestMatchers(GUEST_ONLY_PAGES).permitAll()
          .requestMatchers(PUBLIC_PAGES).permitAll()
          .requestMatchers(ACTUATOR_ENDPOINTS,
                           NOTIFICATION_TELEGRAM + "/" + botId,
                           Endpoints.Rest.LOGIN,
                           REGISTER,
                           LOGOUT,
                           RELEASES,
                           TOP_UPCOMING_RELEASES,
                           SEARCH_ARTIST,
                           TOP_ARTISTS,
                           AUTHENTICATION,
                           REFRESH_ACCESS_TOKEN,
                           CSRF).permitAll()
          .requestMatchers(FOLLOW_ARTIST + "/**",
                           UNFOLLOW_ARTIST + "/**",
                           DASHBOARD,
                           MY_ARTISTS,
                           SPOTIFY_SAVED_ARTISTS,
                           SPOTIFY_ARTIST_SYNCHRONIZATION,
                           NOTIFICATION_CONFIG,
                           OAUTH + "/{registration-id}",
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
                           NOTIFICATION_ON_ANNOUNCEMENT_DATE).hasRole(ROLE_ADMINISTRATOR.getName())
          .anyRequest().denyAll()
      .and()
        .oauth2Login()
          .loginPage(Endpoints.Authentication.LOGIN)
          .userInfoEndpoint()
            .oidcUserService(customOidcUserService)
      .and()
        .authorizationEndpoint()
          .authorizationRequestResolver(oAuth2AuthorizationRequestResolver)
      .and()
      .and()
        .oauth2Client()
          .authorizedClientService(oAuth2AuthorizedClientService)
          .authorizedClientRepository(oAuth2AuthorizedClientRepository)
      .and()
        .logout(logout -> logout.permitAll()
          .logoutUrl(LOGOUT)
          .addLogoutHandler(customLogoutHandler)
          .logoutSuccessHandler((request, response, authentication) -> response.setStatus(SC_OK))
          .invalidateHttpSession(true)
          .clearAuthentication(true)
          .deleteCookies("JSESSIONID", REFRESH_TOKEN_COOKIE_NAME))
      .cors()
      .and()
        .headers()
          .permissionsPolicy().policy("interest-cohort=()").and()
          // These headers are set in the proxy, so disabled here
          .frameOptions().disable()
          .xssProtection().disable()
          .contentTypeOptions().disable()
          .httpStrictTransportSecurity().disable()
      .and()
        .exceptionHandling()
          .defaultAuthenticationEntryPointFor(new HttpStatusEntryPoint(UNAUTHORIZED), new AntPathRequestMatcher(REST_ENDPOINTS))
      .and()
        .addFilter(customUsernamePasswordAuthFilter())
        .addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class);
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
    authenticationFilter.setAuthenticationSuccessHandler(customAuthenticationSuccessHandler);

    return authenticationFilter;
  }
}
