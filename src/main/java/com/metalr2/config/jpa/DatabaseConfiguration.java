package com.metalr2.config.jpa;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

@Configuration
@EnableJpaAuditing
public class DatabaseConfiguration {

  @Bean
  public AuditorAware<String> auditorAware() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication != null) {
      Object principal = authentication.getPrincipal();
      if (principal instanceof UserDetails) {
        return () -> Optional.of(((UserDetails) principal).getUsername());
      }
      else {
        return () -> Optional.of(principal.toString());
      }
    }
    else {
      return () -> Optional.of("ANONYMOUS");
    }
  }

}
