package com.gigsterous.auth.controller;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class UserControllerIT {

  @Autowired
  private TestRestTemplate testRestTemplate;

  @Test
  public void testGivenRequestWhenSendingOptionsThenValidMethodsAreReturned() {

    Set<HttpMethod> response = testRestTemplate.optionsForAllow("/user");

    // HEAD, GET
    assertThat(response.size(), is(2));
  }

  @Test
  public void testGivenUnauthorizedUserWhenRequestingUserInfoThenReturnUnauthorized() {

    ResponseEntity<String> response = testRestTemplate.getForEntity("/user", String.class);

    // HEAD, GET
    assertThat(response.getStatusCode(), is(HttpStatus.UNAUTHORIZED));
  }

}
