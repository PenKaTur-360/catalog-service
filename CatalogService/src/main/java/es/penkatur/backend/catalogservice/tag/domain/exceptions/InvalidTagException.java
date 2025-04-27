package es.penkatur.backend.catalogservice.tag.domain.exceptions;

public class InvalidTagException extends TagException {
    public InvalidTagException(String message) {
        super(message, "INVALID_TAG");
    }
}
