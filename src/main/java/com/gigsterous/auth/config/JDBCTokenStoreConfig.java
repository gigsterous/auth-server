package com.gigsterous.auth.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;

/**
 * <p>
 * JDBCTokenStoreConfig class. Configures data source for tokens.
 * </p>
 * 
 * @author Martin Myslik
 */
@Configuration
public class JDBCTokenStoreConfig {

  @Autowired
  private DataSource dataSource;

  @Bean
  public TokenStore tokenStore() {
    return new JdbcTokenStore(dataSource);
  }
}
