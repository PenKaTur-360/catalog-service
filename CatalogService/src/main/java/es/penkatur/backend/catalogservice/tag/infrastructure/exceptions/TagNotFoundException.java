package es.penkatur.backend.catalogservice.tag.infrastructure.exceptions;

import es.penkatur.backend.catalogservice.tag.domain.exceptions.TagException;

import java.util.UUID;

public class TagNotFoundException extends TagException {
    public TagNotFoundException(UUID id) {
        super(String.format("Tag with ID %s couldn't be found.", id.toString()), "CATALOG_NOT_FOUND");
    }
}
