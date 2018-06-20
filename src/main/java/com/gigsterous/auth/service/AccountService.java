package com.gigsterous.auth.service;

import com.gigsterous.auth.model.User;

import java.util.Locale;
import java.util.Optional;

/**
 * <p>
 * Account service interface.
 * </p>
 * 
 * @author Martin Myslik
 */
public interface AccountService {

  /**
   * <p>
   * Register given user.
   * </p>
   */
  public void registerUser(User user, Locale locale);

  /**
   * <p>
   * Confirm given user based on token and set his password.
   * </p>
   */
  public void confirmUser(String token, String password);

  /**
   * <p>
   * Return true if this user is already registered in the system.
   * </p>
   */
  public boolean isUserRegistered(User user);

  /**
   * <p>
   * Get user for given registration token.
   * </p>
   */
  public Optional<User> getUserForToken(String token);

  /**
   * <p>
   * Reset password for given user.
   * </p>
   */
  public void resetPassword(User user, Locale locale);

  /**
   * <p>
   * Change password for given user.
   * </p>
   */
  public boolean changePassword(String username, String oldPassword, String newPassword);

  /**
   * <p>
   * Change email for given user.
   * </p>
   */
  public boolean changeEmail(String username, String password, String newEmail, Locale locale);

  /**
   * <p>
   * Verify email for given user.
   * </p>
   */
  public void verifyEmail(User user);

}
