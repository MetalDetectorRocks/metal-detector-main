package com.metalr2.model.email;

import com.metalr2.config.misc.AppProperties;
import com.metalr2.config.misc.SpringApplicationContext;

import java.util.Map;

public abstract class AbstractEmail {

  private static final AppProperties appProperties = SpringApplicationContext.getBean("appProperties", AppProperties.class);

  static final String  HOST = appProperties.getHost();
  static final Integer PORT = appProperties.getHttpPort();

  public String getFrom() {
    return appProperties.getDefaultMailFrom();
  }

  public abstract String getRecipient();

  public abstract String getSubject();

  public abstract Map<String, Object> getViewModel();

  public abstract String getTemplateName();

}
