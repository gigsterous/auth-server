package com.gigsterous.auth.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * <p>
 * ProfileController class.
 * </p>
 * 
 * @author Martin Myslik
 */
@Controller
public class ProfileController {

  /**
   * <p>
   * Return profile page.
   * </p>
   */
  @GetMapping("/profile")
  public String profile() {
    return "profile";
  }

}
