package com.metalr2.security;

import com.metalr2.config.constants.Endpoints;
import com.metalr2.security.handler.CustomAuthenticationFailureHandler;
import com.metalr2.security.handler.CustomLogoutSuccessHandler;
import com.metalr2.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

@EnableWebSecurity
@Configuration
public class WebSecurity extends WebSecurityConfigurerAdapter {

  @Value("${security.remember-me-secret}")
  private String REMEMBER_ME_SECRET;

  private final UserService userService;
  private final BCryptPasswordEncoder bCryptPasswordEncoder;

  @Autowired
  public WebSecurity(UserService userService, BCryptPasswordEncoder bCryptPasswordEncoder) {
    this.userService = userService;
    this.bCryptPasswordEncoder = bCryptPasswordEncoder;
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http
      .csrf().disable() // // todo danielw: enable later, do logout within a POST
      .authorizeRequests()
        // .antMatchers(Endpoints.AntPattern.ADMIN).hasRole("ROLE_ADMIN")
            // todo danielw: build one big AntPattern for guest area
        .antMatchers(Endpoints.AntPattern.RESOURCES).permitAll()
        .antMatchers(Endpoints.AntPattern.INDEX).anonymous()
        .antMatchers(Endpoints.AntPattern.LOGIN).anonymous()
        .antMatchers(Endpoints.AntPattern.REGISTER).anonymous()
        .antMatchers(Endpoints.AntPattern.REGISTRATION_VERIFICATION).anonymous()
        .antMatchers(Endpoints.AntPattern.RESEND_VERIFICATION_TOKEN).anonymous()
        .antMatchers(Endpoints.AntPattern.FORGOT_PASSWORD).anonymous()
        .antMatchers(Endpoints.AntPattern.RESET_PASSWORD).anonymous()
        .anyRequest().authenticated()
      .and()
      .formLogin()
        .loginPage(Endpoints.Guest.LOGIN)
        .loginProcessingUrl(Endpoints.Guest.LOGIN)
        .defaultSuccessUrl(Endpoints.Frontend.FOLLOW_ARTISTS, false) // if this is true, user see always this site after login
        .failureHandler(authenticationFailureHandler())
      .and()
      .rememberMe()
        .key(REMEMBER_ME_SECRET)
        .tokenValiditySeconds((int) ExpirationTime.TWO_WEEKS.toSeconds())
      .and()
      .logout()
        .logoutUrl(Endpoints.Guest.LOGOUT).permitAll()
        .invalidateHttpSession(true)
        .clearAuthentication(true)
        .deleteCookies("JSESSIONID", "remember-me")
        .logoutSuccessHandler(logoutSuccessHandler());
  }

  @Override
  protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth.userDetailsService(userService).passwordEncoder(bCryptPasswordEncoder);
  }

  private AuthenticationFailureHandler authenticationFailureHandler() {
    return new CustomAuthenticationFailureHandler();
  }

  private LogoutSuccessHandler logoutSuccessHandler() {
    return new CustomLogoutSuccessHandler();
  }

}
