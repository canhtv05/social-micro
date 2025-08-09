package com.canhtv05.profile.exception;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(500, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),
    UNAUTHORIZED(403, "You do not have permission", HttpStatus.FORBIDDEN),
    API_ENDPOINT_NOT_FOUND(404, "API endpoint not found.", HttpStatus.NOT_FOUND),
    INVALID_TOKEN(400, "Invalid token.", HttpStatus.BAD_REQUEST),
    USER_NOT_FOUND(404, "User not found.", HttpStatus.NOT_FOUND),
    FRIEND_REQUEST_NOT_FOUND(404, "Friend request not found.", HttpStatus.NOT_FOUND),
    INVALID_OPERATION(400, "Invalid operation.", HttpStatus.BAD_REQUEST),
    ALREADY_FRIENDS(400, "Already friends.", HttpStatus.BAD_REQUEST),
    FRIEND_REQUEST_ALREADY_SENT(400, "Friend request already sent.", HttpStatus.BAD_REQUEST),
    ALREADY_FOLLOWING(400, "Already following.", HttpStatus.BAD_REQUEST),
    NOT_FOLLOWING(400, "Not following.", HttpStatus.BAD_REQUEST),
    FRIEND_REQUEST_ALREADY_RECEIVED(400, "Friend request already received.", HttpStatus.BAD_REQUEST),
    FRIEND_REQUEST_SENDER_NOT_FOUND(404, "Friend request sender not found.", HttpStatus.NOT_FOUND),
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
