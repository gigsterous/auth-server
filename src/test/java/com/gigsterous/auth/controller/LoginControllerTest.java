package com.gigsterous.auth.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import com.gigsterous.auth.AuthProperties;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

@RunWith(SpringRunner.class)
@WebMvcTest(value = LoginController.class, secure = false)
public class LoginControllerTest {

  @Autowired
  private MockMvc mvc;

  @MockBean
  private AuthProperties properties;

  @Test
  public void testGivenLoginEndpointWhenLoggedInThenVerifyUserIsRedirected() throws Exception {
    // @formatter:off
    this.mvc.perform(get("/login"))
         .andExpect(status().is3xxRedirection())
         .andExpect(view().name("redirect:/profile"));
    // @formatter:on
  }

}
