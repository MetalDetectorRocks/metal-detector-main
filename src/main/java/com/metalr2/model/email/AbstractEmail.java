package com.metalr2.model.email;

import com.metalr2.config.misc.AppProperties;
import com.metalr2.config.misc.SpringApplicationContext;

import java.util.Map;

public abstract class AbstractEmail {

  static final String  HOST = ((AppProperties) SpringApplicationContext.getBean("appProperties")).getHost();
  static final Integer PORT = ((AppProperties) SpringApplicationContext.getBean("appProperties")).getHttpPort();

  public String getFrom() {
    return ((AppProperties) SpringApplicationContext.getBean("appProperties")).getDefaultMailFrom();
  }

  public abstract String getRecipient();

  public abstract String getSubject();

  public abstract Map<String, Object> getViewModel();

  public abstract String getTemplateName();

}
