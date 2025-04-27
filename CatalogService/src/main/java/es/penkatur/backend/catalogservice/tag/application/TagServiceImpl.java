package es.penkatur.backend.catalogservice.tag.application;

import es.penkatur.backend.catalogservice.tag.domain.Tag;
import es.penkatur.backend.catalogservice.tag.domain.TagRepository;
import es.penkatur.backend.catalogservice.tag.domain.exceptions.InvalidTagException;
import es.penkatur.backend.catalogservice.tag.domain.exceptions.TagOperationException;
import es.penkatur.backend.catalogservice.tag.infrastructure.exceptions.TagNotFoundException;
import es.penkatur.backend.sharedkernel.infraestructure.persistence.ContextualOperations;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class TagServiceImpl implements TagService {

    private final Logger logger;
    private final TagRepository repository;
    private final ContextualOperations contextualOperations;

    @Inject
    public TagServiceImpl(Logger logger, TagRepository repository, ContextualOperations contextualOperations) {
        this.contextualOperations = contextualOperations;
        this.repository = repository;
        this.logger = logger;
    }

    @Override
    public List<Tag> findAllTagsByUpdatedAtAfter(Instant updatedAt) {
        var timestamp = Optional.ofNullable(updatedAt).orElse(Instant.parse("0001-01-01T00:00:00Z"));
        logger.debugf("Find a list of tags from %s", timestamp.toString());
        return contextualOperations.executeInSession(() -> repository.findAllByUpdatedAtAfter(timestamp));
    }

    @Override
    public Tag findTagById(UUID id) {
        if (id == null)
            throw new IllegalArgumentException("ID cannot be null");

        logger.debugf("Searching for tag with ID: %s", id);
        try {
            return contextualOperations.executeInSession(() -> repository.findById(id));
        } catch (TagNotFoundException e) {
            logger.infof("Tag with ID %s not found", id);
            throw e;
        } catch (Exception e) {
            logger.errorf("Error searching tag with id %s", id, e);
            throw new TagOperationException("Error searching tag", e);
        }
    }

    @Override
    public Tag updateTag(Tag tag) {
        try {
            logger.debugf("Updating a tag with ID: %s", tag.getId());
            return contextualOperations.executeInTransaction(() -> repository.save(tag));
        } catch (InvalidTagException e) {
            logger.error("Invalid tag data", e);
            throw e;
        } catch (Exception e) {
            logger.error("Error updating tag", e);
            throw new TagOperationException("Error updating tag", e);
        }
    }
}
