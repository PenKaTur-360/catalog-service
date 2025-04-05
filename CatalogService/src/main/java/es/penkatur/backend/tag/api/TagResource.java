package es.penkatur.backend.tag.api;

import es.penkatur.backend.tag.api.dto.TagDTO;
import es.penkatur.backend.tag.application.TagService;
import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import org.jboss.logging.Logger;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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
    @WithSession
    public Uni<List<TagDTO>> listAllTags(Instant updatedAt) {
        return service.findAllTagsByUpdatedAtAfter(updatedAt)
                .map(tags -> {
                    if (tags.isEmpty()) {
                        logger.info("No tags were found matching the criteria");
                        throw new WebApplicationException("No content available", Response.Status.NO_CONTENT);
                    }

                    return tags.stream()
                            .map(TagDTO::from)
                            .collect(Collectors.toList());
                })
                .onFailure().invoke(throwable -> logger.errorf(throwable, "Error retrieving the list of tags: %s", throwable.getMessage()));
    }

    @Override
    @WithSession
    public Uni<TagDTO> getTag(UUID tagId) {
        return service.findTagById(tagId)
                .map(tag -> tag != null ? TagDTO.from(tag) : null)
                .onItem().ifNull().failWith(() -> {
                    logger.infof("Tag with ID %s not found", tagId);
                    return new WebApplicationException("Tag not found", Response.Status.NOT_FOUND);
                })
                .onFailure().invoke(throwable -> logger.errorf(throwable, "Error retrieving tag with ID %s: %s", tagId, throwable.getMessage()));
    }

    @Override
    @WithTransaction
    public Uni<Response> updateTag(UUID tagId, TagDTO tag) {
        logger.debug("Update tag " + tagId);
        return Uni.createFrom().item(Response.noContent().build());
    }
}
