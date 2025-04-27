package es.penkatur.backend.catalogservice.catalog.domain.exceptions;

public class InvalidCatalogException extends CatalogException {
    public InvalidCatalogException(String message) {
        super(message, "INVALID_CATALOG");
    }
}
