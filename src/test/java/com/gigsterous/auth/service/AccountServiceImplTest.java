package com.gigsterous.auth.service;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.gigsterous.auth.AuthProperties;
import com.gigsterous.auth.model.User;
import com.gigsterous.auth.repository.UserRepository;

import java.util.Locale;
import java.util.Optional;

import javax.mail.MessagingException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.MessageSource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class AccountServiceImplTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private EmailService emailService;

  @Mock
  private BCryptPasswordEncoder bCryptPasswordEncoder;

  @Mock
  private MessageSource messages;

  private AuthProperties properties;
  private AccountService accountService;

  private ArgumentCaptor<String> mailMessageCaptor = ArgumentCaptor.forClass(String.class);
  private ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

  @Before
  public void setUp() {

    MockitoAnnotations.initMocks(this);

    properties = new AuthProperties();
    properties.setRedirectionUrl("http://www.example.com");
    properties.setEmailFrom("noreply@example.com");

    accountService = new AccountServiceImpl(userRepository, emailService, bCryptPasswordEncoder, properties, messages);

  }

  @Test
  public void givenExistingUserWhenCheckingUserRegisteredThenReturnTrue() {

    User user = new User();
    user.setEmail("user@example.com");
    user.setEnabled(true);

    // given existing user
    given(userRepository.findOneByEmail("user@example.com")).willReturn(Optional.of(user));

    // when checking user registered
    boolean isRegistered = accountService.isUserRegistered(user);

    // then expect true
    assertThat(isRegistered, is(true));

  }

  @Test
  public void givenExistingNonEnabledUserWhenCheckingUserRegisteredThenReturnFalse() {

    User user = new User();
    user.setEmail("user@example.com");
    user.setEnabled(false);

    // given existing user
    given(userRepository.findOneByEmail("user@example.com")).willReturn(Optional.of(user));

    // when checking user registered
    boolean isRegistered = accountService.isUserRegistered(user);

    // then expect true
    assertThat(isRegistered, is(false));

  }

  @Test
  public void givenNonExistingUserWhenCheckingUserRegisteredThenReturnFalse() {

    // given new user
    User user = new User();
    user.setEmail("user@example.com");

    // when checking user registered
    boolean isRegistered = accountService.isUserRegistered(user);

    // then expect false
    assertThat(isRegistered, is(false));

  }

  @Test
  public void givenTokenWhenCheckingUserForTokenThenReturnCorrectUser() {

    User user = new User();
    user.setEmail("user@example.com");
    user.setConfirmationToken("1234");

    // given existing user
    given(userRepository.findByConfirmationToken("1234")).willReturn(Optional.of(user));

    // when getting user for token
    Optional<User> loadedUser = accountService.getUserForToken("1234");

    // then expect correct user to be loaded
    assertThat(loadedUser.get().getEmail(), is("user@example.com"));

  }

  @Test
  public void givenNewUserWhenRegisteringUserThenVerifyEmailWasSentWithCorrectTo() throws MessagingException {

    User user = new User();
    user.setEmail("user@example.com");

    // given user
    given(userRepository.findOneByEmail("user@example.com")).willReturn(Optional.of(user));

    // when registering new user
    accountService.registerUser(user, Locale.ENGLISH);

    // then email was sent
    verify(emailService).prepareAndSend(mailMessageCaptor.capture(), eq("noreply@example.com"),
        eq("Registration confirmation"), any(), any(String.class));

    assertThat(mailMessageCaptor.getValue(), is("user@example.com"));

  }

  @Test
  public void givenNewUserWhenRegisteringUserThenVerifyCorrectMessageUsed() throws MessagingException {

    User user = new User();
    user.setEmail("user@example.com");

    given(messages.getMessage("email.registration", null, Locale.ENGLISH)).willReturn("Registration email");

    // given user
    given(userRepository.findOneByEmail("user@example.com")).willReturn(Optional.of(user));

    // when registering new user
    accountService.registerUser(user, Locale.ENGLISH);

    // then email was sent
    verify(emailService).prepareAndSend(eq("user@example.com"), eq("noreply@example.com"),
        eq("Registration confirmation"), mailMessageCaptor.capture(), any(String.class));

    assertThat(mailMessageCaptor.getValue(), is("Registration email"));

  }

  @Test
  public void givenTokenAndPasswordWhenConfirmingUserThenVerifyUserWasSavedAndEnabled() {

    User user = new User();
    user.setEmail("user@example.com");
    user.setConfirmationToken("1234");
    user.setEnabled(false);
    user.setPassword(null);

    // given existing user
    given(userRepository.findByConfirmationToken("1234")).willReturn(Optional.of(user));
    given(bCryptPasswordEncoder.encode("password")).willReturn("encoded");

    // when confirming user
    accountService.confirmUser("1234", "password");

    // then expect user to be updated
    verify(userRepository).save(userCaptor.capture());

    User updatedUser = userCaptor.getValue();
    assertThat(updatedUser.isEnabled(), is(true));
    assertThat(updatedUser.getPassword(), is("encoded"));

  }

  @Test
  public void givenUserWhenResettingPasswordThenUserIsEnabled() {

    User user = new User();
    user.setEmail("user@example.com");
    user.setConfirmationToken("1234");
    user.setEnabled(true);
    user.setPassword("secret");

    // given existing user
    given(userRepository.findOneByEmail("user@example.com")).willReturn(Optional.of(user));

    User temp = new User();
    temp.setEmail("user@example.com");

    // when resetting password
    accountService.resetPassword(temp, Locale.ENGLISH);

    // then user was updated
    verify(userRepository).save(userCaptor.capture());

    User updatedUser = userCaptor.getValue();
    assertThat(updatedUser.isEnabled(), is(true));

  }

  @Test
  public void givenUserWhenResettingPasswordThenEmailIsSent() throws MessagingException {

    User user = new User();
    user.setEmail("user@example.com");
    user.setConfirmationToken("1234");
    user.setEnabled(true);
    user.setPassword("secret");

    // given existing user
    given(userRepository.findOneByEmail("user@example.com")).willReturn(Optional.of(user));

    User temp = new User();
    temp.setEmail("user@example.com");

    // when resetting password
    accountService.resetPassword(temp, Locale.ENGLISH);

    // then email was sent
    verify(emailService).prepareAndSend(mailMessageCaptor.capture(), eq("noreply@example.com"), eq("Password reset"),
        any(), any(String.class));

    assertThat(mailMessageCaptor.getValue(), is("user@example.com"));

  }

  @Test
  public void givenPasswordChangeWhenOldPasswordMatchesThenReturnTrue() {
    User user = new User();
    user.setEmail("user@example.com");
    user.setPassword("passwordHash");

    // given existing user
    given(userRepository.findOneByEmail("user@example.com")).willReturn(Optional.of(user));

    // given
    given(bCryptPasswordEncoder.matches("password", "passwordHash")).willReturn(true);

    boolean success = accountService.changePassword("user@example.com", "password", "secret");

    assertThat(success, is(true));
  }

  @Test
  public void givenPasswordChangeWhenOldPasswordDoesNotMatchThenReturnFalse() {
    User user = new User();
    user.setEmail("user@example.com");
    user.setPassword("passwordHash");

    // given existing user
    given(userRepository.findOneByEmail("user@example.com")).willReturn(Optional.of(user));

    // given
    given(bCryptPasswordEncoder.matches("password", "passwordHash")).willReturn(false);

    boolean success = accountService.changePassword("user@example.com", "password", "secret");

    assertThat(success, is(false));
  }

  @Test
  public void givenEmailChangeWhenOldPasswordMatchesThenReturnTrue() {
    User user = new User();
    user.setEmail("user@example.com");
    user.setPassword("passwordHash");

    // given existing user
    given(userRepository.findOneByEmail("user@example.com")).willReturn(Optional.of(user));

    // given
    given(bCryptPasswordEncoder.matches("password", "passwordHash")).willReturn(true);

    boolean success = accountService.changeEmail("user@example.com", "password", "john@example.com", Locale.ENGLISH);

    assertThat(success, is(true));
  }

  @Test
  public void givenEmailChangeWhenOldPasswordDoesNotMatchThenReturnFalse() {
    User user = new User();
    user.setEmail("user@example.com");
    user.setPassword("passwordHash");

    // given existing user
    given(userRepository.findOneByEmail("user@example.com")).willReturn(Optional.of(user));

    // given
    given(bCryptPasswordEncoder.matches("password", "passwordHash")).willReturn(false);

    boolean success = accountService.changeEmail("user@example.com", "password", "user2@example.com", Locale.ENGLISH);

    assertThat(success, is(false));
  }

  @Test
  public void givenEmailChangeWhenEmailAlreadyInUseThenReturnFalse() {
    User user = new User();
    user.setEmail("user@example.com");
    user.setPassword("passwordHash");

    User user2 = new User();
    user2.setEmail("user2@example.com");

    // given existing users
    given(userRepository.findOneByEmail("user@example.com")).willReturn(Optional.of(user));
    given(userRepository.findOneByEmail("user2@example.com")).willReturn(Optional.of(user2));

    // given
    given(bCryptPasswordEncoder.matches("password", "passwordHash")).willReturn(true);

    boolean success = accountService.changeEmail("user@example.com", "password", "user2@example.com", Locale.ENGLISH);

    assertThat(success, is(false));
  }

  @Test
  public void givenEmailChangeWhenPasswordMatchesThenVerifyEmailSent() {
    User user = new User();
    user.setEmail("user@example.com");
    user.setPassword("passwordHash");

    // given existing users
    given(userRepository.findOneByEmail("user@example.com")).willReturn(Optional.of(user));

    // given
    given(bCryptPasswordEncoder.matches("password", "passwordHash")).willReturn(true);

    accountService.changeEmail("user@example.com", "password", "user2@example.com", Locale.ENGLISH);

    // then email was sent
    verify(emailService).prepareAndSend(eq("user2@example.com"), eq("noreply@example.com"), eq("E-mail change"), any(),
        any(String.class));
  }

}
