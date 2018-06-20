package com.gigsterous.auth.controller;

import com.gigsterous.auth.model.User;
import com.gigsterous.auth.repository.UserRepository;

import java.security.Principal;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import lombok.extern.slf4j.Slf4j;

import org.springframework.context.MessageSource;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

/**
 * <p>
 * UserController class.
 * </p>
 * 
 * @author Martin Myslik
 */
@Slf4j
@RestController
public class UserController {

  private static final String DELETE_ACCOUNT = "deleteAccount";
  private static final String DELETE_SUCCESS = "deleteSuccess";
  private static final String PROFILE = "profile";
  private static final String CONFIRMATION_MESSAGE = "confirmationMessage";
  private static final String WARNING_MESSAGE = "warningMessage";

  private final UserRepository userRepo;
  private final MessageSource messages;

  /**
   * <p>
   * UserController constructor.
   * </p>
   */
  public UserController(UserRepository userRepo, MessageSource messages) {
    this.userRepo = userRepo;
    this.messages = messages;
  }

  /**
   * <p>
   * getUser.
   * </p>
   * 
   * @param principal
   *          containing security context
   * @return user
   */
  @RequestMapping(path = "/user", method = RequestMethod.GET)
  public User getUser(Principal principal) {
    Optional<User> optionalUser = userRepo.findOneByEmail(principal.getName());

    if (!optionalUser.isPresent()) {
      throw new UsernameNotFoundException("User for principal not found!");

    }

    User user = optionalUser.get();

    // override email (in Principal username) with ID - only used at this endpoint
    user.setEmail(Long.toString(user.getId()));

    return user;
  }

  /**
   * <p>
   * Return deleteAccount page.
   * </p>
   */
  @GetMapping("/deleteAccount")
  public ModelAndView deleteAccountPage(Locale locale) {
    ModelAndView modelAndView = new ModelAndView(DELETE_ACCOUNT);

    modelAndView.addObject(WARNING_MESSAGE, messages.getMessage("delete.warning", null, locale));

    return modelAndView;
  }

  /**
   * <p>
   * Delete account.
   * </p>
   */
  @PostMapping(path = "/deleteAccount")
  public ModelAndView deleteAccount(HttpServletRequest request, Principal principal, Locale locale) {
    log.debug("User deletion requested for: {}", principal.getName());

    Optional<User> optionalUser = userRepo.findOneByEmail(principal.getName());

    if (!optionalUser.isPresent()) {
      throw new UsernameNotFoundException("User for principal not found!");
    }

    User user = optionalUser.get();

    userRepo.delete(user);

    ModelAndView modelAndView = new ModelAndView("redirect:/" + DELETE_SUCCESS);
    modelAndView.addObject("success", true);

    return modelAndView;
  }

  /**
   * <p>
   * Return deleteSuccess page.
   * </p>
   */
  @GetMapping("/deleteSuccess")
  public ModelAndView deleteSuccessPage(HttpServletRequest request, Locale locale,
      @RequestParam(value = "success", required = false) boolean success) {

    if (success) {
      // log user out
      SecurityContextHolder.getContext().setAuthentication(null);
      SecurityContextHolder.clearContext();

      // Invalidate session
      final HttpSession session = request.getSession(false);
      if (session != null) {
        session.invalidate();
      }

      ModelAndView modelAndView = new ModelAndView(DELETE_SUCCESS);
      modelAndView.addObject(CONFIRMATION_MESSAGE, messages.getMessage("delete.success", null, locale));

      return modelAndView;
    }

    return new ModelAndView("redirect:/" + PROFILE);

  }

  /**
   * <p>
   * getAllUsers. Only accessible by ADMIN.
   * </p>
   * 
   * @return user list
   */
  @Secured("ROLE_ADMIN")
  @RequestMapping("/users")
  public List<User> getAllUsers() {
    log.debug("Accessing list of users");

    return userRepo.findAll();
  }
}
