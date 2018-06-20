package com.gigsterous.auth.service;

import com.gigsterous.auth.model.User;
import com.gigsterous.auth.repository.UserRepository;

import java.util.Optional;

import lombok.extern.slf4j.Slf4j;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * <p>
 * UserService class. Needs to implement UserDetailsService since it is injected
 * into OAuth2 configuration.
 * </p>
 * 
 * @author Martin Myslik
 */
@Slf4j
@Service
public class UserService implements UserDetailsService {

  private final UserRepository userRepository;

  public UserService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public UserDetails loadUserByUsername(String username) {
    log.debug("Loading user details for username: {}", username);

    Optional<User> optionalUser = userRepository.findOneByEmail(username);

    if (!optionalUser.isPresent()) {
      log.error("User not found!");

      throw new UsernameNotFoundException("E-mail address not found.");
    }

    return optionalUser.get();
  }
}
