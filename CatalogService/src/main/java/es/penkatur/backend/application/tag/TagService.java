package es.penkatur.backend.application.tag;

import es.penkatur.backend.domain.tag.Tag;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;

import java.time.Instant;
import java.util.UUID;

public interface TagService {
    Multi<Tag> findAllTags(Instant updatedAt);

    Uni<Tag> findTagById(UUID id);
}
