package com.asy.spring_boot_rest_api.advice;

public class UserNotFound extends RuntimeException {
    public UserNotFound(String message) {
        super(message);
    }

}
