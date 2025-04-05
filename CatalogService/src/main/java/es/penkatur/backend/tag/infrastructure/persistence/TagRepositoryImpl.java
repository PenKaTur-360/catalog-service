package es.penkatur.backend.tag.infrastructure.persistence;

import es.penkatur.backend.tag.domain.Tag;
import es.penkatur.backend.tag.domain.TagRepository;
import es.penkatur.backend.tag.infrastructure.persistence.mapper.TagMapper;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@ApplicationScoped
public class TagRepositoryImpl implements TagRepository {

    private final TagPanacheRepository repository;

    @Inject
    public TagRepositoryImpl(TagPanacheRepository repository) {
        this.repository = repository;
    }

    @Override
    public Uni<List<Tag>> findAllByUpdatedAtAfter(Instant updatedAt) {
        return repository.findAllByUpdatedAtAfter(updatedAt)
                .onItem().ifNull().continueWith(Collections.emptyList())
                .map(entityList -> entityList.stream()
                        .map(TagMapper::toDomain)
                        .collect(Collectors.toList()));
    }

    @Override
    public Uni<Tag> findById(UUID id) {
        return repository.findById(id)
                .map(tag -> tag != null ? TagMapper.toDomain(tag) : null);
    }

    @Override
    public Uni<Tag> save(Tag tag) {
        var swCreate = tag.getCreatedAt() == null;
        tag.changeUpdatedAt(Instant.now());
        var tagEntity = TagMapper.toEntity(tag);

        if (swCreate) return repository.persist(tagEntity).map(TagMapper::toDomain);
        else return repository.update(
                        "color = ?1, name = ?2, updatedAt = ?3 where id = ?4",
                        tagEntity.getColor(), tagEntity.getName(), tagEntity.getUpdatedAt(), tagEntity.getId()
                )
                .map(updated -> updated > 0 ? tag : null);
    }
}
