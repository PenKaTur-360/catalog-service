package es.penkatur.backend.catalog.application;

import es.penkatur.backend.catalog.domain.Catalog;
import es.penkatur.backend.catalog.domain.CatalogRepository;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class CatalogServiceImpl implements CatalogService {

    private final Logger logger;
    private final CatalogRepository repository;

    @Inject
    public CatalogServiceImpl(Logger logger, CatalogRepository repository) {
        this.repository = repository;
        this.logger = logger;
    }

    @Override
    public Uni<List<Catalog>> findAllCatalogsByUpdatedAtAfter(Instant updatedAt) {
        var timestamp = Optional.ofNullable(updatedAt).orElse(Instant.MIN);
        logger.debugf("Find a list of catalogs from %s", timestamp.toString());
        return repository.findAllByUpdatedAtAfter(timestamp);
    }

    @Override
    public Uni<Catalog> findCatalogById(UUID id) {
        logger.debugf("Searching for catalog with ID: %s", id);
        return repository.findById(id);
    }

    @Override
    public Uni<Catalog> createCatalog(Catalog catalog) {
        // TODO: validar la url del catalogo
        logger.debugf("Creating a new catalog with key: %s", catalog.getKey());
        var result = repository.save(catalog);
        refreshCatalog(catalog.getId());
        return result;
    }

    @Override
    public Uni<Catalog> updateCatalog(Catalog catalog) {
        // TODO: validar la url del catalogo
        logger.debugf("Updating a catalog with key: %s", catalog.getKey());
        return repository.save(catalog);
    }

    @Override
    public Uni<Void> refreshCatalog(UUID id) {
        return null;
    }

    @Override
    public Uni<Boolean> deleteCatalog(UUID id) {
        logger.debugf("Searching for catalog with ID: %s", id);
        return repository.delete(id);
    }
}
