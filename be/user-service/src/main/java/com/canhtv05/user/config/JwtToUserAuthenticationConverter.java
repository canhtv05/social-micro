package com.canhtv05.user.config;

import com.canhtv05.user.service.impl.CustomUserDetailService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;

public class JwtToUserAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private final CustomUserDetailService customUserDetailService;

    public JwtToUserAuthenticationConverter(CustomUserDetailService customUserDetailService) {
        this.customUserDetailService = customUserDetailService;
    }

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        String email = jwt.getClaimAsString("email");

        UserDetails userDetails = customUserDetailService.loadUserByUsername(email);

        return new UsernamePasswordAuthenticationToken(
                userDetails,
                jwt,
                userDetails.getAuthorities()
        );
    }
}