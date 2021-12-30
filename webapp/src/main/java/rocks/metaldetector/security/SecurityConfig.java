package rocks.metaldetector.security;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import rocks.metaldetector.persistence.domain.user.UserRole;
import rocks.metaldetector.security.handler.CustomAccessDeniedHandler;
import rocks.metaldetector.security.handler.CustomAuthenticationFailureHandler;
import rocks.metaldetector.security.handler.CustomAuthenticationSuccessHandler;
import rocks.metaldetector.security.handler.CustomLogoutSuccessHandler;
import rocks.metaldetector.service.user.UserService;
import rocks.metaldetector.support.Endpoints;
import rocks.metaldetector.support.SecurityProperties;

import javax.sql.DataSource;
import java.time.Duration;

import static rocks.metaldetector.support.Endpoints.AntPattern.ACTUATOR_ENDPOINTS;
import static rocks.metaldetector.support.Endpoints.AntPattern.ADMIN;
import static rocks.metaldetector.support.Endpoints.AntPattern.GUEST_PAGES;
import static rocks.metaldetector.support.Endpoints.AntPattern.RESOURCES;
import static rocks.metaldetector.support.Endpoints.AntPattern.REST_ENDPOINTS;

@Configuration
@EnableWebSecurity
@ConditionalOnProperty(
        name = "rocks.metaldetector.security.enabled",
        havingValue = "true",
        matchIfMissing = true
)
@EnableGlobalMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

  private final UserService userService;
  private final DataSource dataSource;
  private final BCryptPasswordEncoder bCryptPasswordEncoder;
  private final SecurityProperties securityProperties;
  private final OAuth2AuthorizedClientService oAuth2AuthorizedClientService;
  private final OAuth2AuthorizedClientRepository oAuth2AuthorizedClientRepository;
  private final OAuth2UserService<OidcUserRequest, OidcUser> customOidcUserService;
  private final OAuth2AuthorizationRequestResolver oAuth2AuthorizationRequestResolver;

  @Value("${telegram.bot-id}")
  private String botId;

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http
      .csrf().ignoringAntMatchers(REST_ENDPOINTS, ACTUATOR_ENDPOINTS)
      .and()
      .authorizeRequests()
        .antMatchers(ADMIN).hasRole(UserRole.ROLE_ADMINISTRATOR.getName())
        .antMatchers(RESOURCES).permitAll()
        .antMatchers(GUEST_PAGES).permitAll()
        .antMatchers(ACTUATOR_ENDPOINTS).permitAll()
        .antMatchers(Endpoints.Rest.NOTIFICATION_TELEGRAM + "/" + botId).permitAll()
        .anyRequest().authenticated()
      .and()
      .formLogin()
        .loginPage(Endpoints.Guest.LOGIN)
        .loginProcessingUrl(Endpoints.Guest.LOGIN)
        .successHandler(new CustomAuthenticationSuccessHandler(new SavedRequestAwareAuthenticationSuccessHandler()))
        .failureHandler(new CustomAuthenticationFailureHandler())
      .and()
        .oauth2Login()
          .loginPage(Endpoints.Guest.LOGIN)
          .successHandler(new CustomAuthenticationSuccessHandler(new SavedRequestAwareAuthenticationSuccessHandler()))
          .failureHandler(new CustomAuthenticationFailureHandler())
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
      .rememberMe()
        .tokenValiditySeconds((int) Duration.ofDays(14).toSeconds())
        .tokenRepository(jdbcTokenRepository())
        .userDetailsService(userService)
        .key(securityProperties.getRememberMeSecret())
      .and()
      .logout()
        .logoutUrl(Endpoints.Guest.LOGOUT).permitAll()
        .invalidateHttpSession(true)
        .clearAuthentication(true)
        .deleteCookies("JSESSIONID", "remember-me")
        .logoutSuccessHandler(new CustomLogoutSuccessHandler())
      .and()
        .cors()
      .and()
        // These headers are set in the proxy, so disabled here
        .headers()
          .frameOptions().disable()
          .xssProtection().disable()
          .contentTypeOptions().disable()
          .httpStrictTransportSecurity().disable()
      .and()
      .exceptionHandling()
        .accessDeniedHandler(new CustomAccessDeniedHandler(() -> SecurityContextHolder.getContext().getAuthentication()))
        .defaultAuthenticationEntryPointFor(new LoginUrlAuthenticationEntryPoint(Endpoints.Guest.LOGIN), new AntPathRequestMatcher(Endpoints.Guest.SLASH_INDEX))
        .defaultAuthenticationEntryPointFor(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED), new AntPathRequestMatcher(REST_ENDPOINTS));
  }

  @Override
  protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth.userDetailsService(userService).passwordEncoder(bCryptPasswordEncoder);
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
