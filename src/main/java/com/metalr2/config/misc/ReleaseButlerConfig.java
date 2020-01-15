package com.metalr2.config.misc;

import lombok.Getter;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class ReleaseButlerConfig {

  private String restBaseUrl = "http://localhost:8095/metal-release-butler";

}
