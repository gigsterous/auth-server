package com.gigsterous.auth.controller;

import com.gigsterous.auth.model.User;
import com.gigsterous.auth.service.AccountService;
import com.gigsterous.auth.service.TokenService;

import java.security.Principal;
import java.util.Locale;
import java.util.Optional;

import javax.validation.Valid;

import lombok.extern.slf4j.Slf4j;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

/**
 * <p>
 * CredentialsController class. Responsible for changing password, handling
 * forgotten password and changing username/email.
 * </p>
 * 
 * @author Martin Myslik
 */
@Slf4j
@Controller
public class CredentialsController {

  private static final String FORGOTTEN = "forgotten";
  private static final String CHANGE_PSWD = "changePassword";
  private static final String CHANGE_EMAIL = "changeEmail";
  private static final String VERIFY_EMAIL = "verifyEmail";

  private static final String SUCCESS_MESSAGE = "successMessage";
  private static final String CONFIRMATION_MESSAGE = "confirmationMessage";
  private static final String ERROR_MESSAGE = "errorMessage";

  private final AccountService accountService;
  private final MessageSource messages;
  private final TokenService tokenService;

  /**
   * <p>
   * CredentialsControlelr constructor.
   * </p>
   */
  public CredentialsController(AccountService accountService, MessageSource messages, TokenService tokenService) {
    this.accountService = accountService;
    this.messages = messages;
    this.tokenService = tokenService;
  }

  /**
   * <p>
   * Return ModelAndView for forgotten password page.
   * </p>
   */
  @RequestMapping(value = "/forgotten", method = RequestMethod.GET)
  public ModelAndView showForgottenPasswordPage(ModelAndView modelAndView, User user) {
    modelAndView.addObject("user", user);
    modelAndView.setViewName(FORGOTTEN);

    return modelAndView;
  }

  /**
   * <p>
   * Process input data from forgotten password form and validate them.
   * </p>
   */
  @RequestMapping(value = "/forgotten", method = RequestMethod.POST)
  public ModelAndView processForgottenPasswordForm(ModelAndView modelAndView, @Valid User user, Locale locale) {
    log.debug("Forgotten password - POST");

    modelAndView.setViewName(FORGOTTEN);

    if (!accountService.isUserRegistered(user)) {
      log.warn("This user is not registered: {}", user);

      modelAndView.addObject(ERROR_MESSAGE,
          messages.getMessage("registration.userNotRegistered", new Object[] { user.getEmail() }, locale));

      return modelAndView;
    }

    // process request and reset password
    accountService.resetPassword(user, locale);

    modelAndView.addObject(CONFIRMATION_MESSAGE,
        messages.getMessage("registration.passwordResetEmail", new Object[] { user.getEmail() }, locale));
    modelAndView.setViewName(FORGOTTEN);

    return modelAndView;
  }

  /**
   * <p>
   * Return changePassword page.
   * </p>
   */
  @GetMapping("/changePassword")
  public ModelAndView changePassword() {
    return new ModelAndView(CHANGE_PSWD);
  }

  /**
   * <p>
   * Process input data from changePassword page.
   * </p>
   */
  @RequestMapping(value = "/changePassword", method = RequestMethod.POST)
  public ModelAndView processPasswordChange(ModelAndView modelAndView,
      @RequestParam("currentPassword") String currentPassword, @RequestParam("newPassword") String newPassword,
      @RequestParam("confirmPassword") String confirmPassword, Locale locale, Principal principal) {
    log.debug("ChangePassword endpoint - POST");

    modelAndView.setViewName(CHANGE_PSWD);

    // new password must be different from the old one
    if (currentPassword.equals(newPassword)) {
      modelAndView.addObject(ERROR_MESSAGE, messages.getMessage("password.notUnique", null, locale));

      return modelAndView;
    }

    if (!newPassword.equals(confirmPassword)) {
      modelAndView.addObject(ERROR_MESSAGE, messages.getMessage("password.notMatching", null, locale));

      return modelAndView;
    }

    boolean success = accountService.changePassword(principal.getName(), currentPassword, newPassword);

    if (success) {
      modelAndView.addObject(SUCCESS_MESSAGE, messages.getMessage("password.changeSuccess", null, locale));

      // revoke tokens
      tokenService.revokeTokens(principal.getName());
    } else {
      modelAndView.addObject(ERROR_MESSAGE, messages.getMessage("password.incorrect", null, locale));
    }

    return modelAndView;
  }

  /**
   * <p>
   * Return changeEmail page.
   * </p>
   */
  @GetMapping("/changeEmail")
  public ModelAndView changeEmail() {
    return new ModelAndView(CHANGE_EMAIL);
  }

  /**
   * <p>
   * Process input data from changeEmail page.
   * </p>
   */
  @RequestMapping(value = "/changeEmail", method = RequestMethod.POST)
  public ModelAndView processEmailChange(ModelAndView modelAndView, @RequestParam("password") String password,
      @RequestParam("email") String email, Locale locale, Principal principal) {
    log.debug("ChangeEmail endpoint - POST");

    modelAndView.setViewName(CHANGE_EMAIL);

    boolean success = accountService.changeEmail(principal.getName(), password, email, locale);

    if (success) {
      modelAndView.addObject(SUCCESS_MESSAGE,
          messages.getMessage("email.changeSuccess", new Object[] { email }, locale));
    } else {
      modelAndView.addObject(ERROR_MESSAGE, messages.getMessage("email.changeFailure", null, locale));
    }

    return modelAndView;
  }

  /**
   * <p>
   * Return changeEmail page.
   * </p>
   */
  @GetMapping("/verifyEmail")
  public ModelAndView showVerifyEmailPage(ModelAndView modelAndView, @RequestParam("token") String token,
      Locale locale) {
    Optional<User> optionalUser = accountService.getUserForToken(token);

    if (!optionalUser.isPresent()) { // No token found in DB
      log.debug("No user found for this token: {}", token);

      modelAndView.addObject(ERROR_MESSAGE, messages.getMessage("email.verificationFailure", null, locale));
    } else { // Token found
      User user = optionalUser.get();
      accountService.verifyEmail(user);

      modelAndView.addObject(SUCCESS_MESSAGE,
          messages.getMessage("email.verificationSuccess", new Object[] { user.getEmail() }, locale));
    }

    modelAndView.setViewName(VERIFY_EMAIL);
    return modelAndView;

  }

}
