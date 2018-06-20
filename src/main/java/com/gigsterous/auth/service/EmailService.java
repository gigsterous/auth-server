package com.gigsterous.auth.service;

import lombok.extern.slf4j.Slf4j;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * <p>
 * EmailService class.
 * </p>
 * 
 * @author Martin Myslik
 */
@Slf4j
@Service
public class EmailService {

  private final JavaMailSender mailSender;
  private final MailContentBuilder mailContentBuilder;

  /**
   * <p>
   * EmailService constructor.
   * </p>
   */
  public EmailService(JavaMailSender mailSender, MailContentBuilder mailContentBuilder) {
    this.mailSender = mailSender;
    this.mailContentBuilder = mailContentBuilder;
  }

  /**
   * <p>
   * Prepare Mime message and send it using java mail sender.
   * </p>
   */
  @Async
  public void prepareAndSend(String to, String from, String subject, String message, String link) {
    MimeMessagePreparator messagePreparator = mimeMessage -> {
      MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);
      messageHelper.setFrom(from);
      messageHelper.setTo(to);
      messageHelper.setSubject(subject);

      String content = mailContentBuilder.build(message, link);
      messageHelper.setText(content, true);
    };

    log.debug("Sending email...");
    mailSender.send(messagePreparator);
  }

}
