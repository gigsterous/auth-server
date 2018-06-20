package com.gigsterous.auth.service;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;

import com.gigsterous.auth.model.User;
import com.gigsterous.auth.repository.UserRepository;

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class UserServiceTest {

  @Mock
  private UserRepository userRepo;

  private UserService userService;

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);

    userService = new UserService(userRepo);
  }

  @Test
  public void testGivenUserServiceWhenLoadingUserByUsernameThenReturnCorrectUser() {
    User user = new User();
    user.setId(1L);
    user.setEmail("john@example.com");

    given(userRepo.findOneByEmail("john@example.com")).willReturn(Optional.of(user));

    assertThat(userService.loadUserByUsername("john@example.com"), is(user));
  }

}
