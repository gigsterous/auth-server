package com.gigsterous.auth.controller;

import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.gigsterous.auth.AuthProperties;
import com.gigsterous.auth.model.Role;
import com.gigsterous.auth.model.User;
import com.gigsterous.auth.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.MessageSource;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

@RunWith(SpringRunner.class)
@WebMvcTest(value = UserController.class, secure = true)
@AutoConfigureRestDocs(outputDir = "docs/snippets")
public class UserControllerTest {

  @Autowired
  private MockMvc mvc;

  @MockBean
  private UserRepository userRepo;

  @MockBean
  private AuthProperties properties;

  @MockBean
  private MessageSource messages;

  @Before
  public void setUp() {
    User user1 = new User();
    user1.setId(1L);
    user1.setEmail("john@example.com");
    user1.setEnabled(true);
    user1.setPassword("password");
    user1.setRole(Role.ADMIN);

    User user2 = new User();
    user2.setId(1L);
    user2.setEmail("jane@example.com");
    user2.setEnabled(true);
    user2.setPassword("password");
    user2.setRole(Role.USER);

    List<User> users = new ArrayList<>();
    users.add(user1);
    users.add(user2);

    // mock repository
    given(userRepo.findOneByEmail("john@example.com")).willReturn(Optional.of(user1));
    given(userRepo.findAll()).willReturn(users);
  }

  @Test
  @WithMockUser(username = "john@example.com", password = "password")
  public void testGivenUserEndpointWhenGettingCurrentUserThenReturnIdOfAuthenticatedUserAsUsername() throws Exception {
    // @formatter:off
    this.mvc.perform(get("/user")).andExpect(status().isOk()).andExpect(jsonPath("$.email", is("1"))).andDo(document("user-authorized"));
    // @formatter:on
  }

  @Test
  @WithMockUser(username = "john@example.com", password = "password")
  public void testGivenUserEndpointWhenGettingAllUsersAsAdminThenStatusIsOk() throws Exception {
    // @formatter:off
    this.mvc.perform(get("/users")).andExpect(status().isOk()).andDo(document("users-all"));
    // @formatter:on
  }

}
