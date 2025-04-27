package es.penkatur.backend.catalogservice.catalog.infrastructure.exceptions;

import es.penkatur.backend.catalogservice.catalog.domain.exceptions.CatalogException;

import java.util.UUID;

public class CatalogNotFoundException extends CatalogException {
    public CatalogNotFoundException(UUID id) {
        super(String.format("Catalog with ID %s couldn't be found.", id.toString()), "CATALOG_NOT_FOUND");
    }
}
