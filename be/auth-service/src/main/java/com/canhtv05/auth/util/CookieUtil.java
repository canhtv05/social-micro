package com.canhtv05.auth.util;

import com.canhtv05.auth.exception.AppException;
import com.canhtv05.auth.exception.ErrorCode;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CookieUtil {

    @NonFinal
    @Value("${jwt.refresh-duration}")
    Long refreshDuration;

    @NonFinal
    @Value("${spring.application.name}")
    String KEY;

    public Cookie setCookie(String accessToken, String refreshToken) {
        Map<String, String> tokenData = new HashMap<>();
        tokenData.put("accessToken", accessToken);
        tokenData.put("refreshToken", refreshToken);

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonData;

        try {
            jsonData = objectMapper.writeValueAsString(tokenData);
        } catch (JsonProcessingException | AppException e) {
            throw new AppException(ErrorCode.JSON_PROCESSING_ERROR);
        }

        // replace các kí tự sao cho giống với thư viện js-cookie
        // https://www.npmjs.com/package/js-cookie
        String formattedJsonData = jsonData.replace("\"", "%22").replace(",", "%2C");

        Cookie cookie = new Cookie("MY_CHAT_APP", formattedJsonData);
        // cookie.setHttpOnly(true);

        // cho phép lấy cookie từ phía client
        cookie.setHttpOnly(false);
        cookie.setMaxAge(refreshDuration.intValue()); // 2 weeks
        cookie.setPath("/");
        cookie.setSecure(true); // true nếu chỉ cho gửi qua HTTPS
        cookie.setDomain("localhost");
        return cookie;
    }

    public void deleteCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie(KEY, "");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        cookie.setSecure(true);
        cookie.setDomain("localhost");
        response.addCookie(cookie);
    }
}
