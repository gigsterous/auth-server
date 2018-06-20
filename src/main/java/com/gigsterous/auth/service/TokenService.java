package com.gigsterous.auth.service;

/**
 * <p>
 * TokenService interface.
 * </p>
 * 
 * @author Martin Myslik
 */
public interface TokenService {

  /**
   * <p>
   * Revoke access and refresh tokens for user with given username.
   * </p>
   *
   * @param username
   *          String
   */
  public void revokeTokens(String username);

}
