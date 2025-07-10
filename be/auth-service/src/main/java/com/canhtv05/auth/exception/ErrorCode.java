package com.canhtv05.auth.exception;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

@Slf4j
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(500, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),
    UNAUTHENTICATED(401, "Unauthenticated", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(401, "You do not have permission", HttpStatus.FORBIDDEN),
    API_ENDPOINT_NOT_FOUND(404, "API endpoint not found.", HttpStatus.NOT_FOUND),
    TOKEN_BLACKLISTED(400, "Tokens blacklisted.", HttpStatus.BAD_REQUEST),
    INVALID_EMAIL_OR_PASSWORD(400, "Invalid email or password.", HttpStatus.BAD_REQUEST),
    JSON_PROCESSING_ERROR(500, "Failed to process JSON data.", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_TOKEN(400, "Invalid token.", HttpStatus.BAD_REQUEST),
    REFRESH_TOKEN_INVALID(400, "Refresh token invalid.", HttpStatus.BAD_REQUEST),
    ;


    ErrorCode(int code, String message, HttpStatus status) {
        this.code = code;
        this.message = message;
        this.status = status;
    }

    int code;
    String message;
    HttpStatus status;
}
