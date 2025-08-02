package com.example.linkup.exception;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum ErrorCode {
    USER_EXISTED(1001, "user existed", HttpStatus.BAD_REQUEST),
    USER_NOT_EXISTED(1002, "user not existed", HttpStatus.NOT_FOUND),
    PROFILE_NOT_EXISTED(2002, "profile not existed", HttpStatus.NOT_FOUND),
    UNCATEGORIZED_EXCEPTION(9999, "uncategorized exception", HttpStatus.INTERNAL_SERVER_ERROR),
    UNAUTHORIZED(1003, "NOT HAVE PERMISSION", HttpStatus.FORBIDDEN),
    TOKEN_INVALID(2000, "token is not valid", HttpStatus.BAD_REQUEST),
    USERNAME_INVALID(2999, "username is invalid", HttpStatus.BAD_REQUEST),
    PASSWORD_INVALID(3000, "password is invalid", HttpStatus.BAD_REQUEST),
    NAME_SHORT(1005, "name is too short", HttpStatus.BAD_REQUEST),
    WRONG_PASSWORD(3001, "wrong password", HttpStatus.BAD_REQUEST),
    INTERNAL_SERVER_ERROR(4004, "INTERNAL SERVER ERROR", HttpStatus.INTERNAL_SERVER_ERROR),
    FILE_UPLOAD_ERROR(4005, "file upload error", HttpStatus.BAD_REQUEST),
    INVALID_MEDIA_TYPE(4006, "invalid media type", HttpStatus.BAD_REQUEST),
    POST_NOT_EXISTED(1010, "post not existed", HttpStatus.NOT_FOUND),
    INVALID_FRIEND_REQUEST_SENT(4000, "Invalid friend request sent", HttpStatus.BAD_REQUEST),
    DUPLICATE_RESOURCE(2005,"duplicate resource", HttpStatus.BAD_REQUEST),
    FRIEND_REQUEST_NOT_FOUND(4007, "friend request not found", HttpStatus.NOT_FOUND),
    FRIEND_REQUEST_NOT_PENDING(4008, "friend request not pending", HttpStatus.NOT_FOUND),
    FRIENDSHIP_NOT_FOUND(4010, "friendship not found", HttpStatus.NOT_FOUND),
    COMMENT_NOT_EXISTED(4020, "comment not existed", HttpStatus.NOT_FOUND),
    FRIENDSHIP_EXISTED(4030, "friendship already existed", HttpStatus.BAD_REQUEST),
    INVALID_FRIEND_REQUEST_STATUS(4040, "invalid friend request status", HttpStatus.BAD_REQUEST),
    VALIDATION_ERROR(5000, "validation error", HttpStatus.BAD_REQUEST),
    NAME_CHANGE_TOO_SOON(6666, "name change too soon", HttpStatus.BAD_REQUEST),
    NAME_INVALID(4844, "name are limited to 3-20 letters only", HttpStatus.BAD_REQUEST),
    NOTIFICATION_NOT_EXISTED(5000, "notification not existed", HttpStatus.NOT_FOUND),
    NOTIFICATION_FORBIDEN(5001, "u don't have permission at this notification", HttpStatus.BAD_REQUEST),

    ;
    private int code;
    private String message;
    private HttpStatusCode statusCode;

    ErrorCode(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }
}
