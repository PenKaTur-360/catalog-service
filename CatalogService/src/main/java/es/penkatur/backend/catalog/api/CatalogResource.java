package es.penkatur.backend.catalog.api;

import es.penkatur.backend.catalog.api.dto.CatalogDTO;
import es.penkatur.backend.catalog.application.CatalogService;
import es.penkatur.backend.catalog.domain.Catalog;
import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import org.jboss.logging.Logger;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@ApplicationScoped
public class CatalogResource implements CatalogApi {

    private final Logger logger;
    private final CatalogService service;

    @Inject
    public CatalogResource(Logger logger, CatalogService service) {
        this.service = service;
        this.logger = logger;
    }

    @Override
    @WithSession
    public Uni<List<CatalogDTO>> listAllCatalogs(Instant updatedAt) {
        return service.findAllCatalogsByUpdatedAtAfter(updatedAt)
                .map(catalogs -> {
                    if (catalogs.isEmpty()) {
                        logger.info("No catalogs were found matching the criteria");
                        throw new WebApplicationException("No content available", Response.Status.NO_CONTENT);
                    }

                    return catalogs.stream()
                            .map(CatalogDTO::from)
                            .collect(Collectors.toList());
                })
                .onFailure().invoke(throwable -> logger.errorf(throwable,
                        "Error retrieving the list of catalogs: %s", throwable.getMessage()));
    }

    @Override
    @WithTransaction
    public Uni<CatalogDTO> createCatalog(CatalogDTO dto) {
        // TODO: recuperar el usuario del contexto
        var catalog = Catalog.builder()
                .id(dto.catalogId())
                .userId(UUID.randomUUID())
                .url(dto.url())
                .build();

        return service.createCatalog(catalog)
                .map(result -> result != null ? CatalogDTO.from(result) : null)
                .onFailure().invoke(throwable -> logger.errorf(throwable,
                        "Error creating catalog with url %s: %s", dto.url(), throwable.getMessage()));
    }

    @Override
    @WithSession
    public Uni<CatalogDTO> getCatalog(UUID catalogId) {
        return service.findCatalogById(catalogId)
                .map(catalog -> catalog != null ? CatalogDTO.from(catalog) : null)
                .onItem().ifNull().failWith(() -> {
                    logger.infof("Catalog with ID %s not found", catalogId);
                    return new WebApplicationException("Catalog not found", Response.Status.NOT_FOUND);
                })
                .onFailure().invoke(throwable -> logger.errorf(throwable,
                        "Error retrieving tag with ID %s: %s", catalogId, throwable.getMessage()));
    }

    @Override
    @WithTransaction
    public Uni<CatalogDTO> updateCatalog(UUID catalogId, CatalogDTO dto) {
        return service.findCatalogById(catalogId)
                .onItem().ifNull().failWith(() -> {
                    logger.infof("Catalog with ID %s not found", catalogId);
                    return new WebApplicationException("Catalog not found", Response.Status.NOT_FOUND);
                })
                .onItem().ifNotNull().transformToUni(catalog -> {
                    if (dto.name() != null) catalog.changeName(dto.name());
                    if (dto.author() != null) catalog.changeAuthor(dto.author());
                    if (dto.url() != null) catalog.changeUrl(dto.url());

                    return service.updateCatalog(catalog)
                            .map(result -> result != null ? CatalogDTO.from(result) : null);
                })
                .onFailure().invoke(throwable -> logger.errorf(throwable,
                        "Error updating catalog with url %s: %s", dto.url(), throwable.getMessage()));
    }

    @Override
    @WithTransaction
    public Uni<Boolean> deleteCatalog(UUID catalogId) {
        return service.deleteCatalog(catalogId)
                .onFailure().invoke(throwable -> logger.errorf(throwable,
                        "Error deleting catalog with ID %s: %s", catalogId, throwable.getMessage()));
    }

    @Override
    @WithTransaction
    public Uni<Response> refreshCatalog(UUID catalogId) {
        // TODO
        logger.debugf("Refresh catalog %s", catalogId);
        return Uni.createFrom().item(Response.noContent().build());
    }
}
