package com.gigsterous.auth.model;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

public class UserTest {

  @Test
  public void testGivenUserWithUsernameWhenGettingUsernameThenReturnUsername() {
    User user = new User();
    user.setEmail("john@example.com");

    assertThat(user.getUsername(), is("john@example.com"));
  }

  @Test
  public void testGivenUserWithPasswordWhenGettingUsernameThenReturnPassword() {
    User user = new User();
    user.setEmail("john@example.com");
    user.setPassword("secret");

    assertThat(user.getPassword(), is("secret"));
  }

  @Test
  public void testGivenUserWhenGettingAccountNonExpiredThenReturnTrue() {
    User user = new User();

    assertThat(user.isAccountNonExpired(), is(true));
  }

  @Test
  public void testGivenUserWhenGettingNonLockedThenReturnTrue() {
    User user = new User();

    assertThat(user.isAccountNonLocked(), is(true));
  }

  @Test
  public void testGivenUserWhenGettingCredentialsNonExpiredThenReturnTrue() {
    User user = new User();

    assertThat(user.isCredentialsNonExpired(), is(true));
  }

  @Test
  public void testGivenEnabledUserWhenGettingEnabledThenReturnTrue() {
    User user = new User();
    user.setEnabled(true);

    assertThat(user.isEnabled(), is(true));
  }

  @Test
  public void testGivenUserWithAdminRoleWhenGettingAuthoritiesThenAuthoritiesContainsAdminRole() {
    User user = new User();
    user.setRole(Role.ADMIN);

    assertThat(user.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN")), is(true));
  }

  @Test
  public void testGivenUserWithAdminRoleWhenGettingAuthoritiesThenAuthoritiesContainsUserRole() {
    User user = new User();
    user.setRole(Role.ADMIN);

    assertThat(user.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_USER")), is(true));
  }

  @Test
  public void testGivenUserWithUserRoleWhenGettingAuthoritiesThenAuthoritiesContainsUserRole() {
    User user = new User();
    user.setRole(Role.USER);

    assertThat(user.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_USER")), is(true));
  }

}
