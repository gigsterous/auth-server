package com.gigsterous.auth.repository;

import com.gigsterous.auth.model.User;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * UserRepository interface. Uses Spring JPA repository
 * </p>
 * 
 * @author Martin Myslik
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

  /**
   * Find a user by email.
   *
   * @param email
   *          the user's email
   * @return user which contains the user with the given email or null.
   */
  Optional<User> findOneByEmail(String email);

  /**
   * Find a user by confirmation token.
   * 
   * @param confirmationToken
   *          generated for registration confirmation
   * @return user associated with this confirmation token
   */
  Optional<User> findByConfirmationToken(String confirmationToken);

  /**
   * Find a user by ID.
   *
   * @param id
   *          the user's ID
   * @return User returns an Optional User object which contains the user or null.
   */
  Optional<User> findById(Long id);
}
