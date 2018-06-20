package com.gigsterous.auth.config;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;

/**
 * <p>
 * OAuth2Config class.
 * </p>
 * 
 * @author Martin Myslik
 */
@Configuration
@EnableAuthorizationServer
@EnableGlobalMethodSecurity(securedEnabled = true)
public class OAuth2Config extends AuthorizationServerConfigurerAdapter {

  private final UserDetailsService userService;
  private final TokenStore tokenStore;
  private final DataSource dataSource;
  private final AuthenticationManager authenticationManager;

  /**
   * <p>
   * OAuth2Config constructor.
   * </p>
   */
  public OAuth2Config(UserDetailsService userService, TokenStore tokenStore, DataSource dataSource,
      @Lazy AuthenticationManager authenticationManager) {
    this.userService = userService;
    this.tokenStore = tokenStore;
    this.dataSource = dataSource;
    this.authenticationManager = authenticationManager;
  }

  @Bean
  public BCryptPasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Override
  public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
    security.tokenKeyAccess("permitAll()").checkTokenAccess("isAuthenticated()");
  }

  @Override
  public void configure(AuthorizationServerEndpointsConfigurer configurer) throws Exception {
    configurer.authenticationManager(authenticationManager);
    configurer.userDetailsService(userService);
    configurer.tokenStore(tokenStore);
  }

  @Override
  public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
    clients.jdbc(dataSource);
  }

}
