package com.metalr2.security;

import com.metalr2.config.constants.Endpoints;
import com.metalr2.security.handler.CustomAuthenticationFailureHandler;
import com.metalr2.security.handler.CustomLogoutSuccessHandler;
import com.metalr2.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
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
        .antMatchers(Endpoints.AntPattern.RESOURCES).permitAll()
        .antMatchers(Endpoints.AntPattern.INDEX).permitAll()
        .antMatchers(Endpoints.AntPattern.LOGIN).permitAll()
        .antMatchers(Endpoints.AntPattern.REGISTER).permitAll()
        .antMatchers(Endpoints.AntPattern.REGISTRATION_VERIFICATION).permitAll()
        .antMatchers(Endpoints.AntPattern.RESEND_VERIFICATION_TOKEN).permitAll()
        .antMatchers(Endpoints.AntPattern.FORGOT_PASSWORD).permitAll()
        .antMatchers(Endpoints.AntPattern.RESET_PASSWORD).permitAll()
        .anyRequest().authenticated()
      .and()
      .formLogin()
        .loginPage(Endpoints.LOGIN)
        .loginProcessingUrl(Endpoints.LOGIN)
        .defaultSuccessUrl(Endpoints.USERS_LIST, false) // if this is true, user see always this site after login
        .failureHandler(authenticationFailureHandler())
      //.failureUrl(Endpoints.LOGIN + "?error=true")
      .and()
      .logout()
        .logoutUrl(Endpoints.LOGOUT).permitAll()
        .invalidateHttpSession(true)
        .clearAuthentication(true)
        .deleteCookies("JSESSIONID")
        .logoutSuccessHandler(logoutSuccessHandler());
        //.logoutSuccessUrl(Endpoints.LOGIN);
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
