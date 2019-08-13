package com.metalr2.config.mvc;

import com.metalr2.config.constants.Endpoints;
import com.metalr2.config.constants.ViewNames;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

  @Override
  public void addViewControllers(ViewControllerRegistry registry) {
    registry.addViewController(Endpoints.Frontend.SETTINGS).setViewName(ViewNames.Frontend.SETTINGS);
    registry.addViewController(Endpoints.Frontend.PROFILE).setViewName(ViewNames.Frontend.PROFILE);
    registry.addViewController(Endpoints.Frontend.ARTISTS_RELEASES).setViewName(ViewNames.Frontend.ARTISTS_RELEASES);
    registry.addViewController(Endpoints.Frontend.ALL_RELEASES).setViewName(ViewNames.Frontend.ALL_RELEASES);
    registry.addViewController(Endpoints.Frontend.MY_ARTISTS).setViewName(ViewNames.Frontend.MY_ARTISTS);
    registry.addViewController(Endpoints.Frontend.REPORT_ARTIST_RELEASE).setViewName(ViewNames.Frontend.REPORT_ARTIST_RELEASE);
    registry.addViewController(Endpoints.Frontend.ABOUT).setViewName(ViewNames.Frontend.ABOUT);
    registry.addViewController(Endpoints.Frontend.TEAM).setViewName(ViewNames.Frontend.TEAM);
    registry.addViewController(Endpoints.Frontend.CONTACT).setViewName(ViewNames.Frontend.CONTACT);
    registry.addViewController(Endpoints.Frontend.IMPRINT).setViewName(ViewNames.Frontend.IMPRINT);
    registry.addViewController(Endpoints.Frontend.STATUS).setViewName(ViewNames.Frontend.STATUS);
  }

}
