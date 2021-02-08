package rocks.metaldetector.config;

import nz.net.ultraq.thymeleaf.LayoutDialect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.FixedLocaleResolver;
import rocks.metaldetector.config.constants.ViewNames;
import rocks.metaldetector.security.RedirectionHandlerInterceptor;
import rocks.metaldetector.support.Endpoints;

import java.util.Locale;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

  private final RedirectionHandlerInterceptor redirectionHandlerInterceptor;

  @Autowired
  public WebMvcConfig(RedirectionHandlerInterceptor redirectionHandlerInterceptor) {
    this.redirectionHandlerInterceptor = redirectionHandlerInterceptor;
  }

  @Override
  public void addViewControllers(ViewControllerRegistry registry) {
    // Frontend pages
    registry.addViewController(Endpoints.Frontend.BLOG).setViewName(ViewNames.Frontend.BLOG);
    registry.addViewController(Endpoints.Frontend.BLOG_POST_WE_ARE_ONLINE).setViewName(ViewNames.Frontend.BLOG_POST_WE_ARE_ONLINE);
    registry.addViewController(Endpoints.Frontend.BLOG_POST_TOP_RELEASES_2020).setViewName(ViewNames.Frontend.BLOG_POST_TOP_RELEASES_2020);
    registry.addViewController(Endpoints.Frontend.BLOG_POST_FUTURE_PLANS).setViewName(ViewNames.Frontend.BLOG_POST_FUTURE_PLANS);
    registry.addViewController(Endpoints.Frontend.RELEASES).setViewName(ViewNames.Frontend.RELEASES);
    registry.addViewController(Endpoints.Frontend.IMPRINT).setViewName(ViewNames.Frontend.IMPRINT);
    registry.addViewController(Endpoints.Frontend.PRIVACY_POLICY).setViewName(ViewNames.Frontend.PRIVACY_POLICY);
    registry.addViewController(Endpoints.Frontend.ACCOUNT_DETAILS).setViewName(ViewNames.Frontend.ACCOUNT_DETAILS);
    registry.addViewController(Endpoints.Frontend.SPOTIFY_SYNCHRONIZATION).setViewName(ViewNames.Frontend.SPOTIFY_SYNCHRONIZATION);
    registry.addViewController(Endpoints.Frontend.NOTIFICATION_SETTINGS).setViewName(ViewNames.Frontend.NOTIFICATION_SETTINGS);
    registry.addViewController(Endpoints.Frontend.STATUS).setViewName(ViewNames.Frontend.STATUS);

    // Backend pages
    registry.addViewController(Endpoints.AdminArea.IMPORT).setViewName(ViewNames.AdminArea.IMPORT);
    registry.addViewController(Endpoints.AdminArea.ANALYTICS).setViewName(ViewNames.AdminArea.ANALYTICS);
    registry.addViewController(Endpoints.AdminArea.SETTINGS).setViewName(ViewNames.AdminArea.SETTINGS);
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
    registry.addInterceptor(redirectionHandlerInterceptor).addPathPatterns(Endpoints.AntPattern.GUEST_PAGES);
  }

  @Bean
  public LayoutDialect layoutDialect() {
    return new LayoutDialect();
  }

  @Bean
  LocaleResolver localeResolver() {
    return new FixedLocaleResolver(Locale.ENGLISH);
  }
}
