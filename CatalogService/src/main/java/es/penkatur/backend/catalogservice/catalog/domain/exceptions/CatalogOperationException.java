package es.penkatur.backend.catalogservice.catalog.domain.exceptions;

public class CatalogOperationException extends CatalogException {
    public CatalogOperationException(String message, Throwable cause) {
        super(message, "CATALOG_OPERATION_ERROR", cause);
    }
}
