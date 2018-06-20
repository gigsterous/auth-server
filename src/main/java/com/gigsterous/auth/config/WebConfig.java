package com.gigsterous.auth.config;

import com.gigsterous.auth.AuthProperties;

import java.util.Locale;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

/**
 * <p>
 * WebConfig class.
 * </p>
 * 
 * @author Martin Myslik
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

  private final AuthProperties properties;

  /**
   * <p>
   * WebConfig constructor.
   * </p>
   */
  public WebConfig(AuthProperties properties) {
    this.properties = properties;
  }

  @Override
  public void addCorsMappings(CorsRegistry registry) {
    // @formatter:off
    registry
      .addMapping("/**")
      .allowedOrigins(properties.getCorsAllowedOrigins())
      .allowedHeaders("*")
      .allowedMethods("*")
      .allowCredentials(true);
    // @formatter:on
  }

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(localeChangeInterceptor());
  }

  /**
   * <p>
   * Resolver for localizations.
   * </p>
   */
  @Bean
  public LocaleResolver localeResolver() {
    SessionLocaleResolver slr = new SessionLocaleResolver();
    slr.setDefaultLocale(Locale.US);
    return slr;
  }

  /**
   * <p>
   * Localization interceptors.
   * </p>
   */
  @Bean
  public LocaleChangeInterceptor localeChangeInterceptor() {
    LocaleChangeInterceptor lci = new LocaleChangeInterceptor();
    lci.setParamName("lang");
    return lci;
  }
}
