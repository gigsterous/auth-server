package com.gigsterous.auth.service;

import java.util.Collection;

import lombok.extern.slf4j.Slf4j;

import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;
import org.springframework.stereotype.Service;

/**
 * <p>
 * TokenService class.
 * </p>
 * 
 * @author Martin Myslik
 */
@Slf4j
@Service
public class TokenServiceImpl implements TokenService {

  private final TokenStore tokenStore;

  /**
   * <p>
   * TokenService constructor.
   * </p>
   */
  public TokenServiceImpl(TokenStore tokenStore) {
    this.tokenStore = tokenStore;
  }

  @Override
  public void revokeTokens(String username) {
    log.debug("Revoking tokens for {}", username);

    if (!(tokenStore instanceof JdbcTokenStore)) {
      log.debug("Token store is not instance of JdbcTokenStore. Cannot revoke tokens!");

      return;
    }

    Collection<OAuth2AccessToken> tokens = ((JdbcTokenStore) tokenStore).findTokensByUserName(username);

    for (OAuth2AccessToken token : tokens) {
      log.debug("Revoking access token {}", token);
      tokenStore.removeAccessToken(token);

      log.debug("Revoking refresh token {}", token.getRefreshToken());
      tokenStore.removeRefreshToken(token.getRefreshToken());
    }

  }

}
