package es.penkatur.backend.catalogservice.catalog.domain.exceptions;

import lombok.Getter;

@Getter
public abstract class CatalogException extends RuntimeException {
    private final String errorCode;

    protected CatalogException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    protected CatalogException(String message, String errorCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }
}
