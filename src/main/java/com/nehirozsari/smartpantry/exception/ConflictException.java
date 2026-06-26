package com.nehirozsari.smartpantry.exception;

public class ConflictException extends BusinessException {

    public ConflictException(String message) {
        super(ErrorCode.CONFLICT, message);
    }
}
