package com.gigsterous.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * <p>
 * WebSecurityConfig class.
 * </p>
 * 
 * @author Martin Myslik
 */
@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

  private static final String LOGIN = "/login";
  private static final String LOGOUT_SUCCESS = "/login?logout";
  private static final String REGISTER = "/register";
  private static final String CONFIRM = "/confirm";
  private static final String CONFIRM_REDIRECT = "/confirmRedirect";
  private static final String PROFILE = "/profile";
  private static final String FORGOTTEN = "/forgotten";
  private static final String VERIFY = "/verifyEmail";
  private static final String ERROR = "/error";

  private final UserDetailsService userService;
  private final AuthenticationManager authenticationManager;

  public WebSecurityConfig(UserDetailsService userService, @Lazy AuthenticationManager authenticationManager) {
    this.userService = userService;
    this.authenticationManager = authenticationManager;
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {

    // @formatter:off
    http
      // Enable CORS
      .cors().and()
      // Set login page
      .formLogin().loginPage(LOGIN).permitAll().defaultSuccessUrl(PROFILE).and()
      // These requests are permitted without authorization
      .authorizeRequests().antMatchers("/", "/css/*", "/health", 
          LOGIN, REGISTER, CONFIRM, CONFIRM_REDIRECT, FORGOTTEN, VERIFY, ERROR)
          .permitAll()
      // These requests are secured by the following way
      .anyRequest().authenticated().and()
      // Set logout handling
      .logout().logoutSuccessUrl(LOGOUT_SUCCESS);
      // @formatter:on

  }

  @Override
  public void configure(WebSecurity web) throws Exception {
    web.ignoring().antMatchers(HttpMethod.OPTIONS, "/**");
  }

  @Override
  protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth.parentAuthenticationManager(authenticationManager);
    auth.userDetailsService(userService);

    // nullify parent auth manager to prevent infinite loop when userDetails not
    // found for username
    auth.parentAuthenticationManager(null);
  }

  @Bean
  @Override
  public AuthenticationManager authenticationManagerBean() throws Exception {
    return super.authenticationManagerBean();
  }

}
