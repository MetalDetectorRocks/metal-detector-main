package com.metalr2.security;

import com.metalr2.config.constants.Endpoints;
import com.metalr2.model.user.UserRole;
import com.metalr2.security.handler.CustomAccessDeniedHandler;
import com.metalr2.security.handler.CustomAuthenticationFailureHandler;
import com.metalr2.security.handler.CustomAuthenticationSuccessHandler;
import com.metalr2.security.handler.CustomLogoutSuccessHandler;
import com.metalr2.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenBasedRememberMeServices;

import javax.sql.DataSource;
import java.time.Duration;

@EnableWebSecurity
@Configuration
@ConditionalOnProperty(
        name = "com.metalr2.security.enabled",
        havingValue = "true",
        matchIfMissing = true
)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

  @Value("${security.remember-me-secret}")
  private String REMEMBER_ME_SECRET;

  private final UserService userService;
  private final DataSource dataSource;
  private final BCryptPasswordEncoder bCryptPasswordEncoder;

  @Autowired
  public SecurityConfig(UserService userService, DataSource dataSource, BCryptPasswordEncoder bCryptPasswordEncoder) {
    this.userService = userService;
    this.dataSource = dataSource;
    this.bCryptPasswordEncoder = bCryptPasswordEncoder;
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http
      .csrf().ignoringAntMatchers(Endpoints.AntPattern.REST_ENDPOINTS)
      .and()
      .authorizeRequests()
        .antMatchers(Endpoints.AntPattern.ADMIN).hasRole(UserRole.ROLE_ADMINISTRATOR.getName())
        .antMatchers(Endpoints.AntPattern.RESOURCES).permitAll()
        .antMatchers(Endpoints.AntPattern.AUTH_PAGES).permitAll()
        .anyRequest().authenticated()
      .and()
      .formLogin()
        .loginPage(Endpoints.Guest.LOGIN)
        .loginProcessingUrl(Endpoints.Guest.LOGIN)
        .successHandler(new CustomAuthenticationSuccessHandler())
        .failureHandler(new CustomAuthenticationFailureHandler())
      .and()
      .rememberMe()
        .tokenValiditySeconds((int) Duration.ofDays(14).toSeconds())
        .rememberMeServices(persistentTokenBasedRememberMeServices())
        .key(REMEMBER_ME_SECRET)
      .and()
      .logout()
        .logoutUrl(Endpoints.Guest.LOGOUT).permitAll()
        .invalidateHttpSession(true)
        .clearAuthentication(true)
        .deleteCookies("JSESSIONID", "remember-me")
        .logoutSuccessHandler(new CustomLogoutSuccessHandler())
      .and()
      .exceptionHandling()
        .accessDeniedHandler(new CustomAccessDeniedHandler(() -> SecurityContextHolder.getContext().getAuthentication()));
  }

  @Override
  protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth.userDetailsService(userService).passwordEncoder(bCryptPasswordEncoder);
  }

  @Bean
  public PersistentTokenBasedRememberMeServices persistentTokenBasedRememberMeServices() {
    return new PersistentTokenBasedRememberMeServices(REMEMBER_ME_SECRET, userService, jdbcTokenRepository());
  }

  @Bean
  public JdbcTokenRepositoryImpl jdbcTokenRepository() {
    JdbcTokenRepositoryImpl jdbcTokenRepository = new JdbcTokenRepositoryImpl();
    jdbcTokenRepository.setCreateTableOnStartup(false);
    jdbcTokenRepository.setDataSource(dataSource);
    return jdbcTokenRepository;
  }

}
