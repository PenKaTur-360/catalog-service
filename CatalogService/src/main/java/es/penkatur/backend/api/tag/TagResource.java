package es.penkatur.backend.api.tag;

import es.penkatur.backend.api.tag.dto.TagDTO;
import es.penkatur.backend.application.tag.TagService;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import org.jboss.logging.Logger;

import java.time.Instant;
import java.util.UUID;

@ApplicationScoped
public class TagResource implements TagApi {

    private final Logger logger;

    private final TagService service;

    @Inject
    public TagResource(Logger logger, TagService service) {
        this.service = service;
        this.logger = logger;
    }

    @Override
    public Multi<TagDTO> listAllTags(Instant updatedAt) {
        return service.findAllTags(updatedAt)
                .map(TagDTO::from)
                .onCompletion().ifEmpty().failWith(() -> {
                    logger.info("No se encontraron etiquetas que coincidan con los criterios");
                    return new WebApplicationException("No content available", Response.Status.NO_CONTENT);
                })
                .onFailure().invoke(throwable -> logger.error("Error al recuperar la lista de etiquetas: {}", throwable.getMessage(), throwable))
                .onFailure().recoverWithItem(throwable -> {
                    throw new WebApplicationException("Internal server error", Response.Status.INTERNAL_SERVER_ERROR);
                });
    }

    @Override
    public Uni<TagDTO> getTag(UUID tagId) {
        return service.findTagById(tagId)
                .map(tag -> tag != null ? TagDTO.from(tag) : null)
                .onItem().ifNull().failWith(() -> {
                    logger.info("No se encontró la etiqueta con ID: " + tagId);
                    return new WebApplicationException("Tag not found", Response.Status.NOT_FOUND);
                })
                .onFailure().invoke(throwable -> logger.error(String.format("Error al recuperar la etiqueta con ID %s: %s", tagId, throwable.getMessage()), throwable));
    }

    @Override
    public Uni<Response> updateTag(UUID tagId, TagDTO tag) {
        logger.debug("Update tag " + tagId);
        return Uni.createFrom().item(Response.noContent().build());
    }
}
