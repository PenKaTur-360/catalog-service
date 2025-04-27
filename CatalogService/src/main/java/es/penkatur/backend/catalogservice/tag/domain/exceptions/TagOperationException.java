package es.penkatur.backend.catalogservice.tag.domain.exceptions;

public class TagOperationException extends TagException {
    public TagOperationException(String message, Throwable cause) {
        super(message, "CATALOG_OPERATION_ERROR", cause);
    }
}
