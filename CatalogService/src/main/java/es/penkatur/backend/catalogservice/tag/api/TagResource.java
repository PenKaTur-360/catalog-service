package es.penkatur.backend.catalogservice.tag.api;

import es.penkatur.backend.catalogservice.tag.api.dto.TagDTO;
import es.penkatur.backend.catalogservice.tag.application.TagService;
import es.penkatur.backend.catalogservice.tag.domain.Tag;
import es.penkatur.backend.catalogservice.tag.domain.exceptions.InvalidTagException;
import es.penkatur.backend.catalogservice.tag.domain.exceptions.TagOperationException;
import es.penkatur.backend.catalogservice.tag.infrastructure.exceptions.TagNotFoundException;
import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import org.jboss.logging.Logger;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

public class TagResource implements TagApi {
    private final Logger logger;
    private final TagService service;

    @Inject
    public TagResource(Logger logger, TagService service) {
        this.service = service;
        this.logger = logger;
    }

    @Override
    public CompletionStage<List<TagDTO>> listAllTags(Instant updatedAt) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                List<Tag> tags = service.findAllTagsByUpdatedAtAfter(updatedAt);

                if (tags.isEmpty()) {
                    logger.info("No tags were found matching the criteria");
                    throw new WebApplicationException("No content available", Response.Status.NO_CONTENT);
                }

                return tags.stream()
                        .map(TagDTO::from)
                        .collect(Collectors.toList());
            } catch (WebApplicationException e) {
                throw e;
            } catch (Exception e) {
                logger.error("Error listing tags", e);
                throw new TagOperationException("Internal error processing the request", e);
            }
        });
    }

    @Override
    public CompletionStage<TagDTO> getTag(UUID tagId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                if (tagId == null)
                    throw new InvalidTagException("Tag ID cannot be null or empty");

                Tag tag = service.findTagById(tagId);
                return TagDTO.from(tag);
            } catch (TagNotFoundException e) {
                throw e;
            } catch (InvalidTagException e) {
                logger.warnf("Invalid tag data: %s", e.getMessage());
                throw e;
            } catch (Exception e) {
                logger.errorf(e, "Error listing tag: %s", tagId);
                throw new TagOperationException("Internal error processing the request", e);
            }
        });
    }

    @Override
    public CompletionStage<Response> updateTag(UUID tagId, TagDTO dto) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                if (tagId == null)
                    throw new InvalidTagException("Tag ID cannot be null or empty");
                if (dto == null)
                    throw new InvalidTagException("Tag data cannot be null");
                if (dto.tagId() != null && !dto.tagId().equals(tagId))
                    throw new InvalidTagException("The IDs not match");

                Tag tag = service.findTagById(tagId);
                if (dto.color() != null) tag.changeColor(dto.color());

                service.updateTag(tag);
                return Response.ok().build();
            } catch (TagNotFoundException e) {
                throw e;
            } catch (InvalidTagException e) {
                logger.warnf("Invalid catalog data: %s", e.getMessage());
                throw e;
            } catch (Exception e) {
                logger.errorf(e, "Error updating tag: %s", tagId);
                throw new TagOperationException("Error processing update request", e);
            }
        });
    }
}
