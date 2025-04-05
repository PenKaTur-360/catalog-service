package es.penkatur.backend.tag.application;

import es.penkatur.backend.tag.domain.Tag;
import es.penkatur.backend.tag.domain.TagRepository;
import io.smallrye.mutiny.Uni;
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

    @Inject
    public TagServiceImpl(Logger logger, TagRepository repository) {
        this.repository = repository;
        this.logger = logger;
    }

    @Override
    public Uni<List<Tag>> findAllTagsByUpdatedAtAfter(Instant updatedAt) {
        var timestamp = Optional.ofNullable(updatedAt)
                .orElse(Instant.MIN);
        logger.debugf("Find a list of tags from %s", timestamp.toString());
        return repository.findAllByUpdatedAtAfter(timestamp);
    }

    @Override
    public Uni<Tag> findTagById(UUID id) {
        logger.debugf("Searching for tag with ID: %s", id);
        return repository.findById(id);
    }
}
