package com.metalr2.service.redirection;

import com.metalr2.config.constants.Endpoints;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import java.util.Optional;

@Service
public class RedirectionServiceImpl implements RedirectionService {

  @Override
  public Optional<ModelAndView> getRedirectionIfNeeded(Authentication authentication) {
    if (authentication != null && authentication.isAuthenticated()) {
      return Optional.of(new ModelAndView("redirect:" + Endpoints.Frontend.HOME));
    }
    return Optional.empty();
  }
}
