package com.metalr2.security;

import com.metalr2.security.handler.CustomAccessDeniedHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.context.SecurityContextHolder;

@EnableWebSecurity
@Configuration
@Profile("test")
public class WebSecurityNoAuth extends WebSecurityConfigurerAdapter implements WebSecurity {

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http
      .csrf().disable();
  }

}
