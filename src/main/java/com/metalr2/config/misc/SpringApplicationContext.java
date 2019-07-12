package com.metalr2.config.misc;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

@SuppressWarnings("unused") // util class
public class SpringApplicationContext implements ApplicationContextAware {

  private static ApplicationContext CONTEXT;

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    CONTEXT = applicationContext;
  }

  public static Object getBean(String beanName) {
    return CONTEXT.getBean(beanName);
  }

  public static <T> T getBean(String beanName, Class<T> classType) {
    return CONTEXT.getBean(beanName, classType);
  }
	
}
