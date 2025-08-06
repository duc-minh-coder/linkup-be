package com.example.linkup.exception;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum ErrorCode {
    // 1000 - 1099: User
    USER_EXISTED(1000, "user existed", HttpStatus.BAD_REQUEST),
    USER_NOT_EXISTED(1001, "user not existed", HttpStatus.NOT_FOUND),
    USERNAME_INVALID(1002, "username is invalid", HttpStatus.BAD_REQUEST),
    PASSWORD_INVALID(1003, "password is invalid", HttpStatus.BAD_REQUEST),
    NAME_SHORT(1004, "name is too short", HttpStatus.BAD_REQUEST),
    WRONG_PASSWORD(1005, "wrong password", HttpStatus.BAD_REQUEST),

    // 1100 - 1199: Profile
    PROFILE_NOT_EXISTED(1100, "profile not existed", HttpStatus.NOT_FOUND),
    NAME_CHANGE_TOO_SOON(1101, "name change too soon", HttpStatus.BAD_REQUEST),
    NAME_INVALID(1102, "name is limited to 3-20 letters only", HttpStatus.BAD_REQUEST),

    // 2000 - 2099: Post
    POST_NOT_EXISTED(2000, "post not existed", HttpStatus.NOT_FOUND),

    // 3000 - 3099: Friend Request & Friendship
    INVALID_FRIEND_REQUEST_SENT(3000, "invalid friend request sent", HttpStatus.BAD_REQUEST),
    FRIEND_REQUEST_NOT_FOUND(3001, "friend request not found", HttpStatus.NOT_FOUND),
    FRIEND_REQUEST_NOT_PENDING(3002, "friend request not pending", HttpStatus.BAD_REQUEST),
    FRIENDSHIP_NOT_FOUND(3003, "friendship not found", HttpStatus.NOT_FOUND),
    FRIENDSHIP_EXISTED(3004, "friendship already existed", HttpStatus.BAD_REQUEST),
    INVALID_FRIEND_REQUEST_STATUS(3005, "invalid friend request status", HttpStatus.BAD_REQUEST),

    // 4000 - 4099: Comment
    COMMENT_NOT_EXISTED(4000, "comment not existed", HttpStatus.NOT_FOUND),

    // 5000 - 5099: Notification
    NOTIFICATION_NOT_EXISTED(5000, "notification not existed", HttpStatus.NOT_FOUND),
    NOTIFICATION_FORBIDEN(5001, "you don't have permission for this notification", HttpStatus.BAD_REQUEST),

    // 6000 - 6099: Message
    MESSAGE_NOT_EXISTED(6000, "message not existed", HttpStatus.NOT_FOUND),

    // 7000 - 7099: Validation
    VALIDATION_ERROR(7000, "validation error", HttpStatus.BAD_REQUEST),

    // 8000 - 8099: File & Media
    FILE_UPLOAD_ERROR(8000, "file upload error", HttpStatus.BAD_REQUEST),
    INVALID_MEDIA_TYPE(8001, "invalid media type", HttpStatus.BAD_REQUEST),

    // 9000 - 9099: Auth & General
    UNAUTHORIZED(9000, "not have permission", HttpStatus.FORBIDDEN),
    TOKEN_INVALID(9001, "token is not valid", HttpStatus.BAD_REQUEST),
    INTERNAL_SERVER_ERROR(9002, "internal server error", HttpStatus.INTERNAL_SERVER_ERROR),
    DUPLICATE_RESOURCE(9003, "duplicate resource", HttpStatus.BAD_REQUEST),
    UNCATEGORIZED_EXCEPTION(9999, "uncategorized exception", HttpStatus.INTERNAL_SERVER_ERROR)
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
