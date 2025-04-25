package es.penkatur.backend.catalogservice.catalog.infraestructure.exceptions;

import jakarta.ws.rs.NotFoundException;

import java.io.Serial;
import java.util.UUID;

public class CatalogNotFoundException extends NotFoundException {
    @Serial
    private static final long serialVersionUID = -8300710199080990160L;

    public CatalogNotFoundException(UUID id) {
        super(String.format("Catalog with ID %s couldn't be found.", id.toString()));
    }
}
