package com.canhtv05.auth.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Slf4j
public class AuthenticationRequestInterceptor implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate template) {
        ServletRequestAttributes servletRequestAttributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if (servletRequestAttributes == null) return;

        var authHeader = servletRequestAttributes.getRequest().getHeader(HttpHeaders.AUTHORIZATION);

        if (StringUtils.hasText(authHeader))
            template.header(HttpHeaders.AUTHORIZATION, authHeader);
    }
}
