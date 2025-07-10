package com.canhtv05.user.exception;

import lombok.Getter;

import java.io.Serializable;
import java.util.Map;

@Getter
public class DetailException extends RuntimeException {

    private final Map<String, Serializable> errors;

    public DetailException(Map<String, Serializable> errors) {
        this.errors = errors;
    }

}
