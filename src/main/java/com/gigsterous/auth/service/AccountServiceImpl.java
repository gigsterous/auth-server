package com.gigsterous.auth.service;

import com.gigsterous.auth.AuthProperties;
import com.gigsterous.auth.model.User;
import com.gigsterous.auth.repository.UserRepository;

import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

import lombok.extern.slf4j.Slf4j;

import org.springframework.context.MessageSource;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * <p>
 * AccountServiceImpl class.
 * </p>
 * 
 * @author Martin Myslik
 */
@Slf4j
@Service
public class AccountServiceImpl implements AccountService {

  private final UserRepository userRepository;
  private final EmailService emailService;
  private final BCryptPasswordEncoder passwordEncoder;
  private final AuthProperties properties;
  private final MessageSource messages;

  /**
   * <p>
   * AccountServiceImpl constructor.
   * </p>
   */
  public AccountServiceImpl(UserRepository userRepository, EmailService emailService,
      BCryptPasswordEncoder passwordEncoder, AuthProperties properties, MessageSource messages) {
    this.userRepository = userRepository;
    this.emailService = emailService;
    this.passwordEncoder = passwordEncoder;
    this.properties = properties;
    this.messages = messages;
  }

  @Override
  public void registerUser(User user, Locale locale) {
    log.debug("Registering new user...");

    // check if user exists in non-enabled state
    Optional<User> loaded = userRepository.findOneByEmail(user.getUsername());
    if (loaded.isPresent()) {
      // if yes, use existing user instead
      user = loaded.get();
    }

    // Disable user until they click on confirmation link in email
    user.setEnabled(false);

    // Generate random 36-character string token for confirmation link
    user.setConfirmationToken(UUID.randomUUID().toString());

    userRepository.save(user);

    // send email
    log.debug("Sending confirmation token to the selected email: {}", user.getEmail());

    String message = messages.getMessage("email.registration", null, locale);
    String link = properties.getRedirectionUrl() + "/confirmRedirect?token=" + user.getConfirmationToken();

    emailService.prepareAndSend(user.getEmail(), properties.getEmailFrom(), "Registration confirmation", message, link);

  }

  @Override
  public void confirmUser(String token, String password) {
    log.debug("Confirming user with token {}", token);

    // Find the user associated with the reset token
    Optional<User> optionalUser = userRepository.findByConfirmationToken(token);

    if (!optionalUser.isPresent()) {
      throw new UsernameNotFoundException("NO user found for token!");
    }

    User user = optionalUser.get();

    // Set new password
    user.setPassword(passwordEncoder.encode((CharSequence) password));

    // Set user to enabled
    user.setEnabled(true);
    user.setConfirmationToken("");

    // Save user
    userRepository.save(user);
  }

  @Override
  public boolean isUserRegistered(User user) {
    Optional<User> loaded = userRepository.findOneByEmail(user.getUsername());
    if (loaded.isPresent()) {
      return loaded.get().isEnabled();
    }

    return false;
  }

  @Override
  public Optional<User> getUserForToken(String token) {
    return userRepository.findByConfirmationToken(token);
  }

  @Override
  public void resetPassword(User user, Locale locale) {
    log.debug("Resetting password for user: {}", user.getEmail());

    Optional<User> optionalUser = userRepository.findOneByEmail(user.getEmail());
    if (!optionalUser.isPresent()) {
      log.error("Cannot find user with this e-mail!");

      return;
    }

    // invalidate current password
    user = optionalUser.get();

    // Generate random 36-character string token for confirmation link
    user.setConfirmationToken(UUID.randomUUID().toString());

    // send email with confirmation token
    log.debug("Sending confirmation token to the selected email: {}", user.getEmail());

    String message = messages.getMessage("email.resetPassword", null, locale);
    String link = properties.getRedirectionUrl() + "/confirmRedirect?token=" + user.getConfirmationToken();

    emailService.prepareAndSend(user.getEmail(), properties.getEmailFrom(), "Password reset", message, link);

    // update user entity
    userRepository.save(user);

  }

  @Override
  public boolean changePassword(String email, String oldPassword, String newPassword) {
    log.debug("Changing password for user: {}", email);

    Optional<User> optionalUser = userRepository.findOneByEmail(email);
    if (!optionalUser.isPresent()) {
      log.error("Cannot find user with this e-mail!");

      return false;
    }

    User user = optionalUser.get();

    boolean passwordMatch = passwordEncoder.matches(oldPassword, user.getPassword());

    log.debug("Current password matches: {}", passwordMatch);
    if (passwordMatch) {
      user.setPassword(passwordEncoder.encode(newPassword));

      userRepository.save(user);

      return true;
    }

    // old password does not match
    return false;
  }

  @Override
  public boolean changeEmail(String email, String password, String newEmail, Locale locale) {
    log.debug("Changing e-mail for user: {}", email);

    Optional<User> optionalUser = userRepository.findOneByEmail(email);
    if (!optionalUser.isPresent()) {
      log.error("Cannot find user with this e-mail!");

      return false;
    }

    User user = optionalUser.get();

    if (userRepository.findOneByEmail(newEmail).isPresent()) {
      log.warn("User with email {} already exists.", newEmail);

      return false;
    }

    boolean passwordMatch = passwordEncoder.matches(password, user.getPassword());

    log.debug("Current password matches: {}", passwordMatch);
    if (passwordMatch) {
      user.setPendingEmail(newEmail);

      // Generate random 36-character string token for confirmation link
      user.setConfirmationToken(UUID.randomUUID().toString());

      // send email with confirmation token
      log.debug("Sending verification token {} to the selected email: {}", user.getConfirmationToken(), newEmail);

      String message = messages.getMessage("email.verification", null, locale);
      String link = properties.getRedirectionUrl() + "/verifyEmail?token=" + user.getConfirmationToken();

      emailService.prepareAndSend(newEmail, properties.getEmailFrom(), "E-mail change", message, link);

      userRepository.save(user);

      return true;
    }

    // password does not match
    return false;
  }

  @Override
  public void verifyEmail(User user) {
    log.debug("Verifying e-mail {}", user.getPendingEmail());

    // Set new e-mail
    user.setEmail(user.getPendingEmail());
    user.setPendingEmail(null);
    user.setConfirmationToken("");

    // Save user
    userRepository.save(user);
  }

}
