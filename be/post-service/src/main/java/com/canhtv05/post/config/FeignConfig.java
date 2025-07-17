package com.canhtv05.post.config;

import feign.codec.Encoder;
import feign.form.spring.SpringFormEncoder;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {

  @Bean
  Encoder multipartForm() {
    return new SpringFormEncoder();
  }
}
