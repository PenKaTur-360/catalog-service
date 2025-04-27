package es.penkatur.backend.catalogservice.tag.domain.exceptions;

import lombok.Getter;

@Getter
public abstract class TagException extends RuntimeException {
    private final String errorCode;

    protected TagException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    protected TagException(String message, String errorCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }
}
