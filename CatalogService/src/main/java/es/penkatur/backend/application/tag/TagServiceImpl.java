package es.penkatur.backend.application.tag;

import es.penkatur.backend.domain.tag.Tag;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class TagServiceImpl implements TagService {

    private final Logger logger;

    @Inject
    public TagServiceImpl(Logger logger) {
        this.logger = logger;
    }

    @Override
    public Multi<Tag> findAllTags(Instant updatedAt) {
        var timestamp = Optional.ofNullable(updatedAt)
                .orElse(Instant.MIN);
        logger.debug("Find a list of tags from " + timestamp.toString());

        var tag = Tag.builder()
                .id(UUID.randomUUID())
                .name("test")
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        return Multi.createFrom().item(tag);
    }

    @Override
    public Uni<Tag> findTagById(UUID id) {
        logger.debug("Buscando etiqueta con ID: " + id);

        var tag = Tag.builder()
                .id(id)
                .name("test")
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
        return Uni.createFrom().item(tag);
    }
}
