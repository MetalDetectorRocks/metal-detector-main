package com.metalr2.config.mvc;

import com.metalr2.config.constants.Endpoints;
import com.metalr2.config.constants.ViewNames;
import com.metalr2.security.RedirectionHandlerInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

  private final RedirectionHandlerInterceptor redirectionHandlerInterceptor;

  @Autowired
  public WebConfig(RedirectionHandlerInterceptor redirectionHandlerInterceptor) {
    this.redirectionHandlerInterceptor = redirectionHandlerInterceptor;
  }

  @Override
  public void addViewControllers(ViewControllerRegistry registry) {
    // Frontend pages
    registry.addViewController(Endpoints.Frontend.SETTINGS).setViewName(ViewNames.Frontend.SETTINGS);
    registry.addViewController(Endpoints.Frontend.PROFILE).setViewName(ViewNames.Frontend.PROFILE);
    registry.addViewController(Endpoints.Frontend.ARTISTS_RELEASES).setViewName(ViewNames.Frontend.ARTISTS_RELEASES);
    registry.addViewController(Endpoints.Frontend.ALL_RELEASES).setViewName(ViewNames.Frontend.ALL_RELEASES);
    registry.addViewController(Endpoints.Frontend.REPORT_ARTIST_RELEASE).setViewName(ViewNames.Frontend.REPORT_ARTIST_RELEASE);
    registry.addViewController(Endpoints.Frontend.ABOUT).setViewName(ViewNames.Frontend.ABOUT);
    registry.addViewController(Endpoints.Frontend.TEAM).setViewName(ViewNames.Frontend.TEAM);
    registry.addViewController(Endpoints.Frontend.CONTACT).setViewName(ViewNames.Frontend.CONTACT);
    registry.addViewController(Endpoints.Frontend.IMPRINT).setViewName(ViewNames.Frontend.IMPRINT);
    registry.addViewController(Endpoints.Frontend.STATUS).setViewName(ViewNames.Frontend.STATUS);

    // Backend pages
    registry.addViewController(Endpoints.AdminArea.IMPORT).setViewName(ViewNames.AdminArea.IMPORT);
    registry.addViewController(Endpoints.AdminArea.ANALYTICS).setViewName(ViewNames.AdminArea.ANALYTICS);
    registry.addViewController(Endpoints.AdminArea.SETTINGS).setViewName(ViewNames.AdminArea.SETTINGS);
    registry.addViewController(Endpoints.AdminArea.USERS).setViewName(ViewNames.AdminArea.USERS);
    registry.addViewController(Endpoints.AdminArea.PROFILE).setViewName(ViewNames.AdminArea.PROFILE);
  }

  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    registry.addResourceHandler("/webjars/**")
            .addResourceLocations("/webjars/")
            .resourceChain(false);
  }

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(redirectionHandlerInterceptor);
  }
}
