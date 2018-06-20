package com.gigsterous.auth.controller;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class HealthControllerIT {

  @Autowired
  private TestRestTemplate testRestTemplate;

  @Test
  public void testGivenUnauthorizedUserWhenRequestingHealthThenReturnOk() {

    ResponseEntity<String> response = testRestTemplate.getForEntity("/health", String.class);

    assertThat(response.getStatusCode(), is(HttpStatus.OK));
  }

}
