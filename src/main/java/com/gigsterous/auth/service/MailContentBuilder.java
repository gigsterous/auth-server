package com.gigsterous.auth.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

/**
 * <p>
 * MailContentBuilder class.
 * </p>
 * 
 * @author Martin Myslik
 */
@Service
public class MailContentBuilder {

  private TemplateEngine templateEngine;

  /**
   * <p>
   * MailContentBuilder constructor.
   * </p>
   */
  @Autowired
  public MailContentBuilder(TemplateEngine templateEngine) {
    this.templateEngine = templateEngine;
  }

  /**
   * <p>
   * Populate email template with custom message.
   * </p>
   */
  public String build(String message, String link) {
    Context context = new Context();
    context.setVariable("message", message);
    context.setVariable("link", link);

    return templateEngine.process("mail/mail", context);
  }

}
