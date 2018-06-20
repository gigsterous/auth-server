package com.gigsterous.auth.controller;

import com.gigsterous.auth.service.TokenService;

import java.security.Principal;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import lombok.extern.slf4j.Slf4j;

import org.springframework.context.MessageSource;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * <p>
 * LogoutControllerClass.
 * </p>
 *
 * @author Martin Myslik
 */
@Controller
@Slf4j
public class LogoutController {

  private static final String LOGOUT = "logout";
  private static final String CONFIRMATION_MESSAGE = "confirmationMessage";

  private final TokenService tokenService;
  private final MessageSource messages;

  public LogoutController(TokenService tokenService, MessageSource messages) {
    this.tokenService = tokenService;
    this.messages = messages;
  }

  /**
   * <p>
   * Return logout page.
   * </p>
   */
  @GetMapping("/logout")
  public String login() {
    return LOGOUT;
  }

  /**
   * <p>
   * One click logout. Invalidates the session.
   * </p>
   */
  @PostMapping("/logout")
  public String logout(HttpServletRequest request) {
    log.debug("Direct logout");

    // Current user was validated -> Clear securityContext
    SecurityContextHolder.getContext().setAuthentication(null);
    SecurityContextHolder.clearContext();

    // Invalidate session
    final HttpSession session = request.getSession(false);
    if (session != null) {
      session.invalidate();
    }

    return "redirect:/login?logout";
  }

  /**
   * <p>
   * One click logout. Revokes all tokens.
   * </p>
   */
  @PostMapping("/globalLogout")
  public ModelAndView globalLogout(Principal principal, Locale locale) {
    log.debug("Global logout");

    ModelAndView modelAndView = new ModelAndView(LOGOUT);

    // Revoke tokens
    tokenService.revokeTokens(principal.getName());

    modelAndView.addObject(CONFIRMATION_MESSAGE, messages.getMessage("logout.globalConfirmation", null, locale));

    return modelAndView;
  }

}
