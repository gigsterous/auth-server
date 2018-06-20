package com.gigsterous.auth;

import java.util.TimeZone;

import javax.annotation.PostConstruct;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * <p>
 * AuthApplication class.
 * </p>
 * 
 * @author Martin Myslik
 */
@SpringBootApplication
public class AuthServer {

  public static void main(String[] args) {
    SpringApplication.run(AuthServer.class, args);
  }
  
  @PostConstruct
  void started() {
    TimeZone.setDefault(TimeZone.getTimeZone("Etc/UTC"));
  }

}
