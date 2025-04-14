package es.penkatur.backend.catalog.infrastructure.persistence;

import es.penkatur.backend.catalog.domain.Catalog;
import es.penkatur.backend.catalog.domain.CatalogRepository;
import es.penkatur.backend.catalog.infrastructure.persistence.mapper.CatalogMapper;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@ApplicationScoped
public class CatalogRepositoryImpl implements CatalogRepository {

    private final CatalogPanacheRepository repository;

    @Inject
    public CatalogRepositoryImpl(CatalogPanacheRepository repository) {
        this.repository = repository;
    }

    @Override
    public Uni<List<Catalog>> findAllByUpdatedAtAfter(Instant updatedAt) {
        return repository.findAllByUpdatedAtAfter(updatedAt)
                .onItem().ifNull().continueWith(Collections.emptyList())
                .map(entityList -> entityList.stream()
                        .map(CatalogMapper::toDomain)
                        .collect(Collectors.toList()));
    }

    @Override
    public Uni<Catalog> findById(UUID id) {
        return repository.findById(id)
                .map(entity -> entity != null ? CatalogMapper.toDomain(entity) : null);
    }

    @Override
    public Uni<Catalog> save(Catalog catalog) {
        var swCreate = catalog.getCreatedAt() == null;
        catalog.changeUpdatedAt(Instant.now());
        var entity = CatalogMapper.toEntity(catalog);

        if (swCreate) return repository.persist(entity).map(CatalogMapper::toDomain);
        else return repository.update(
                        "name = ?1, author = ?2, version = ?3, updatedAt = ?4, externalUpdatedAt = ?5" +
                                " where id = ?6",
                        entity.getName(), entity.getAuthor(), entity.getVersion(), entity.getUpdatedAt(),
                        entity.getExternalUpdatedAt(), entity.getId()
                )
                .map(updated -> updated > 0 ? catalog : null);
    }

    @Override
    public Uni<Boolean> delete(UUID id) {
        return repository.deleteById(id);
    }
}
