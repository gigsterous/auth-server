package com.gigsterous.auth;

import lombok.Getter;
import lombok.Setter;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * <p>
 * AuthProperties class.
 * </p>
 * 
 * @author Martin Myslik
 */
@Getter
@Setter
@Configuration
@ConfigurationProperties("auth")
public class AuthProperties {

  private String redirectionUrl;
  private String emailFrom;
  private String corsAllowedOrigins;

}
