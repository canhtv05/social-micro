package com.canhtv05.user.config;

import com.canhtv05.user.service.impl.CustomUserDetailService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final String[] PUBLIC_ENDPOINTS = {
            "/internal/email/**", "/internal/update/refresh-token", "/create"
    };

    private final CustomJwtDecoder customJWTDecoder;
    private final CustomUserDetailService customUserDetailService;

    public SecurityConfig(CustomJwtDecoder customJWTDecoder, CustomUserDetailService customUserDetailService) {
        this.customJWTDecoder = customJWTDecoder;
        this.customUserDetailService = customUserDetailService;
    }

    @Bean
    SecurityFilterChain configure(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(authorizeRequests -> authorizeRequests.requestMatchers(PUBLIC_ENDPOINTS).permitAll()
                .anyRequest().authenticated());

        http.oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt
                .decoder(customJWTDecoder)
                .jwtAuthenticationConverter(new JwtToUserAuthenticationConverter(customUserDetailService)))
                .authenticationEntryPoint(new JwtAuthenticationEntrypoint()));

        http.csrf(AbstractHttpConfigurer::disable);

        return http.build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }
}
