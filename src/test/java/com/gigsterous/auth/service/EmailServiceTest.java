package com.gigsterous.auth.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import javax.mail.MessagingException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessagePreparator;

public class EmailServiceTest {

  @Mock
  private JavaMailSender mailSender;

  @Mock
  private MailContentBuilder contentBuilder;

  private EmailService emailService;

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);

    emailService = new EmailService(mailSender, contentBuilder);
  }

  @Test
  public void testGivenEmailServiceWhenSendingEmailThenVerifyEmailSentThroughMailSender() throws MessagingException {
    emailService.prepareAndSend("john@example.com", "noreply@example.com", "test", "Hello!", "http://www.example.com");

    verify(mailSender).send(any(MimeMessagePreparator.class));
  }

}
