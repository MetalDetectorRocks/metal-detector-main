package rocks.metaldetector.config.misc;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfig {

  @Bean
  public SpringApplicationContext springApplicationContext() {
    return new SpringApplicationContext();
  }

  @Bean
  public ModelMapper modelMapper() {
    return new ModelMapper();
  }

}
