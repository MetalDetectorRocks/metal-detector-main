package rocks.metaldetector.security;

import lombok.RequiredArgsConstructor;
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

@EnableWebSecurity
@Configuration
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
  private final SearchQuerySanitizingFilter searchQuerySanitizingFilter;
  private final CspNonceFilter cspNonceFilter;

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http
      .requiresChannel().requestMatchers(request -> request.getHeader("X-Forwarded-Proto") != null).requiresSecure()
      .and()
      .csrf().ignoringAntMatchers(Endpoints.AntPattern.REST_ENDPOINTS)
      .and()
      .authorizeRequests()
        .antMatchers(Endpoints.AntPattern.ADMIN).hasRole(UserRole.ROLE_ADMINISTRATOR.getName())
        .antMatchers(Endpoints.AntPattern.RESOURCES).permitAll()
        .antMatchers(Endpoints.AntPattern.AUTH_PAGES).permitAll()
        .antMatchers(Endpoints.Rest.CSP_VIOLATION_REPORT).permitAll()
        .anyRequest().authenticated()
      .and()
      .formLogin()
        .loginPage(Endpoints.Guest.LOGIN)
        .loginProcessingUrl(Endpoints.Guest.LOGIN)
        .successHandler(new CustomAuthenticationSuccessHandler(new SavedRequestAwareAuthenticationSuccessHandler()))
        .failureHandler(new CustomAuthenticationFailureHandler())
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
      .exceptionHandling()
        .accessDeniedHandler(new CustomAccessDeniedHandler(() -> SecurityContextHolder.getContext().getAuthentication()))
        .defaultAuthenticationEntryPointFor(new LoginUrlAuthenticationEntryPoint(Endpoints.Guest.LOGIN), new AntPathRequestMatcher(Endpoints.Guest.SLASH_INDEX))
        .defaultAuthenticationEntryPointFor(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED), new AntPathRequestMatcher(Endpoints.AntPattern.REST_ENDPOINTS));
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
  public FilterRegistrationBean<CspNonceFilter> nonceFilterFilterRegistrationBean() {
    FilterRegistrationBean<CspNonceFilter> filterRegistrationBean = new FilterRegistrationBean<>();
    filterRegistrationBean.setFilter(cspNonceFilter);
    filterRegistrationBean.addUrlPatterns(Endpoints.Frontend.ALL_FRONTEND_PAGES.toArray(new String[0]));
    filterRegistrationBean.addUrlPatterns(Endpoints.Guest.ALL_GUEST_INDEX_PAGES.toArray(new String[0]));
    filterRegistrationBean.addUrlPatterns(Endpoints.Guest.ALL_AUTH_PAGES.toArray(new String[0]));
    return filterRegistrationBean;
  }

  @Bean
  public FilterRegistrationBean<SearchQuerySanitizingFilter> sanitizingFilterFilterRegistrationBean() {
    FilterRegistrationBean<SearchQuerySanitizingFilter> filterFilterRegistrationBean = new FilterRegistrationBean<>();
    filterFilterRegistrationBean.setFilter(searchQuerySanitizingFilter);
    filterFilterRegistrationBean.addUrlPatterns(Endpoints.Rest.ARTISTS + Endpoints.Rest.SEARCH);
    return filterFilterRegistrationBean;
  }
}
