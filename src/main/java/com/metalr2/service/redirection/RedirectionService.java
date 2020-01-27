package com.metalr2.service.redirection;

import org.springframework.security.core.Authentication;
import org.springframework.web.servlet.ModelAndView;

import java.util.Optional;

public interface RedirectionService {

  Optional<ModelAndView> getRedirectionIfNeeded(Authentication authentication);

}
