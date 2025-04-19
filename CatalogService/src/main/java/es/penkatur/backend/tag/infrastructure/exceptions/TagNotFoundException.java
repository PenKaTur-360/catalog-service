package es.penkatur.backend.tag.infrastructure.exceptions;

import jakarta.ws.rs.NotFoundException;

import java.io.Serial;
import java.util.UUID;

public class TagNotFoundException extends NotFoundException {
    @Serial
    private static final long serialVersionUID = -8627263968037289693L;

    public TagNotFoundException(UUID id) {
        super(String.format("Tag with ID %s couldn't be found.", id.toString()));
    }
}
