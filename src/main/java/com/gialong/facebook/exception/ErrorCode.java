package com.gialong.facebook.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    UNCATEGORY(9999, "UNCATEGORY", HttpStatus.INTERNAL_SERVER_ERROR),

    UNAUTHENTICATED(401, "you must login to access this resource", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(403, "you dont have authorities", HttpStatus.FORBIDDEN),

    USER_EXISTED(400, "User existed", HttpStatus.BAD_REQUEST),
    USER_NOT_EXISTED(400, "User not existed", HttpStatus.BAD_REQUEST),
    TOKEN_INVALID(401, "Token invalid", HttpStatus.UNAUTHORIZED),
    SIGN_OUT_FAILED(400, "Sign out failed", HttpStatus.BAD_REQUEST),
    CANNOT_SEND_EMAIL(400, "Cannot send email", HttpStatus.BAD_REQUEST),
    POST_NOT_EXISTED(400, "Post not existed", HttpStatus.BAD_REQUEST),

    FILE_NOT_AVAILABLE(400, "File not found", HttpStatus.BAD_REQUEST),
    FILE_TYPE_NOT_SUPPORTED(400, "File type not supported", HttpStatus.BAD_REQUEST),
    FILE_TOO_LARGE(400, "File too large", HttpStatus.BAD_REQUEST)
    ;

    private final int code;
    private final String message;
    private final HttpStatus httpStatus;

    ErrorCode(int code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }
}