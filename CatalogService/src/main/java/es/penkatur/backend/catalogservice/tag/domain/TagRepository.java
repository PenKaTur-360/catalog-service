package es.penkatur.backend.catalogservice.tag.domain;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface TagRepository {
    List<Tag> findAllByUpdatedAtAfter(Instant updatedAt);
    Tag findById(UUID id);
    Tag save(Tag tag);
}
