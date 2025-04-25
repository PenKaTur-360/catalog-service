package es.penkatur.backend.catalogservice.catalog.infraestructure.persistence;

import es.penkatur.backend.catalogservice.catalog.domain.Catalog;
import es.penkatur.backend.catalogservice.catalog.domain.CatalogRepository;
import es.penkatur.backend.catalogservice.catalog.infraestructure.exceptions.CatalogNotFoundException;
import es.penkatur.backend.catalogservice.catalog.infraestructure.persistence.mapper.CatalogMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@ApplicationScoped
public class CatalogRepositoryImpl implements CatalogRepository {

    private final Logger logger;
    private final CatalogPanacheRepository repository;

    @Inject
    public CatalogRepositoryImpl(Logger logger, CatalogPanacheRepository repository) {
        this.repository = repository;
        this.logger = logger;
    }

    @Override
    public List<Catalog> findAllByUpdatedAtAfter(Instant updatedAt) {
        return repository.findAllByUpdatedAtAfter(updatedAt)
                .stream()
                .map(CatalogMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Catalog findById(UUID id) {
        var entity = repository.findById(id);
        if (entity == null) throw new CatalogNotFoundException(id);
        return CatalogMapper.toDomain(entity);
    }

    @Override
    public Catalog save(Catalog catalog) {
        if (catalog.getCreatedAt() == null) {
            var catalogEntity = CatalogMapper.toEntity(catalog);
            repository.persist(catalogEntity);
            logger.debugf("Catalog created successfully: %s", catalogEntity.getId());
            return CatalogMapper.toDomain(catalogEntity);
        } else {
            var entity = repository.findById(catalog.getId());
            if (entity == null) throw new CatalogNotFoundException(catalog.getId());

            logger.debugf("Found catalog for update with ID: %s", catalog.getId());
            entity.setName(catalog.getName());
            entity.setAuthor(catalog.getAuthor());
            entity.setVersion(catalog.getVersion());
            entity.setExternalUpdatedAt(catalog.getExternalUpdatedAt());

            repository.persist(entity);
            logger.debugf("Catalog updated successfully with ID: %s", catalog.getId());
            return CatalogMapper.toDomain(entity);
        }
    }

    @Override
    public boolean delete(UUID id) {
        return repository.deleteById(id);
    }
}
