package rocks.metaldetector.testutil;

import org.springframework.context.support.StaticApplicationContext;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import rocks.metaldetector.service.exceptions.AppExceptionsHandler;

public interface WithExceptionResolver {

  default HandlerExceptionResolver exceptionResolver() {
    StaticApplicationContext applicationContext = new StaticApplicationContext();
    applicationContext.registerSingleton("exceptionHandler", AppExceptionsHandler.class);

    WebMvcConfigurationSupport webMvcConfigurationSupport = new WebMvcConfigurationSupport();
    webMvcConfigurationSupport.setApplicationContext(applicationContext);

    return webMvcConfigurationSupport.handlerExceptionResolver(new ContentNegotiationManager());
  }
}
