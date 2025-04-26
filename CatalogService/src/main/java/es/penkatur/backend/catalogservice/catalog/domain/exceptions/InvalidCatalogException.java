package es.penkatur.backend.catalogservice.catalog.domain.exceptions;

public class InvalidCatalogException extends IllegalStateException {
    public InvalidCatalogException(String message) {
        super(message);
    }
}
