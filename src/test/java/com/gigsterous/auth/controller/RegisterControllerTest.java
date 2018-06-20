package com.gigsterous.auth.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gigsterous.auth.AuthProperties;
import com.gigsterous.auth.model.User;
import com.gigsterous.auth.service.AccountService;

import java.util.Locale;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

@RunWith(SpringRunner.class)
@WebMvcTest(value = RegisterController.class, secure = false)
public class RegisterControllerTest {

  @Autowired
  private MockMvc mvc;

  @Autowired
  private ObjectMapper mapper;

  @MockBean
  private AccountService registrationService;

  @MockBean
  private AuthProperties properties;

  @Test
  public void testGivenRegisterEndpointWhenAccessingRegisterPageThenVerifyRegisterViewIsReturned() throws Exception {
    // @formatter:off
    this.mvc.perform(get("/register"))
         .andExpect(status().isOk())
         .andExpect(view().name("register"));
    // @formatter:on
  }

  @Test
  public void testGivenRegisterEndpointWhenCreatingNewUserThenVerifyUserIsRegistered() throws Exception {
    User user = new User();
    user.setEmail("john@example.com");

    // @formatter:off
    this.mvc.perform(post("/register")
         .contentType(MediaType.APPLICATION_JSON)
         .content(mapper.writeValueAsString(user)))
         .andExpect(status().isOk())
         .andExpect(view().name("register"));
    // @formatter:on

    verify(registrationService).registerUser(any(User.class), any(Locale.class));
  }

  @Test
  public void testGivenConfirmEndpointWhenAccessingConfirmationPageWithoutTokenThenReturn400Status() throws Exception {
    // @formatter:off
    this.mvc.perform(get("/confirm"))
         .andExpect(status().isBadRequest());
    // @formatter:on
  }

  @Test
  public void testGivenConfirmEndpointWhenAccessingConfirmationPageWithTokenThenVerifyConfirmViewIsReturned()
      throws Exception {
    // @formatter:off
    this.mvc.perform(get("/confirm")
         .param("token", "1234"))
         .andExpect(status().isOk())
         .andExpect(view().name("confirm"));
    // @formatter:on
  }

  @Test
  public void testGivenConfirmEndpointWhenAccessingRedirectConfirmationPageThenVerifyUserIsRedirected()
      throws Exception {
    // @formatter:off
    this.mvc.perform(get("/confirmRedirect")
         .param("token", "1234"))
         .andExpect(status().is3xxRedirection())
         .andExpect(view().name("redirect:/confirm?token=1234"));
    // @formatter:on
  }

}
