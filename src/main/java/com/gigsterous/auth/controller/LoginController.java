package com.gigsterous.auth.controller;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * <p>
 * LoginController class.
 * </p>
 * 
 * @author Martin Myslik
 */
@Controller
public class LoginController {

  /**
   * <p>
   * Return home page.
   * </p>
   */
  @GetMapping("/")
  public String home() {
    return "index";
  }

  /**
   * <p>
   * Return login page or redirect user to profile if already logged in.
   * </p>
   */
  @GetMapping("/login")
  public String login() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();

    if (!(auth instanceof AnonymousAuthenticationToken)) {

      // The user is logged in
      return "redirect:/profile";
    }

    return "login";
  }

}
