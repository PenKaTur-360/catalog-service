package es.penkatur.backend.catalogservice.catalog.domain.exceptions;

public class CatalogOperationException extends RuntimeException {
    public CatalogOperationException(String message, Throwable cause) {
        super(message, cause);
    }
}
