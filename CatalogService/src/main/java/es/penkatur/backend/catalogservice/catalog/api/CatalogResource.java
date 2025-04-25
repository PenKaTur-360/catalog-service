package es.penkatur.backend.catalogservice.catalog.api;

import es.penkatur.backend.catalogservice.catalog.api.dto.CatalogDTO;
import es.penkatur.backend.catalogservice.catalog.application.CatalogService;
import es.penkatur.backend.catalogservice.catalog.domain.Catalog;
import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import org.jboss.logging.Logger;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

public class CatalogResource implements CatalogApi {

    private final Logger logger;
    private final CatalogService service;

    @Inject
    public CatalogResource(Logger logger, CatalogService service) {
        this.service = service;
        this.logger = logger;
    }

    @Override
    public CompletionStage<List<CatalogDTO>> listAllCatalogs(Instant updatedAt) {
        return CompletableFuture.supplyAsync(() -> {
            List<Catalog> catalogs = service.findAllCatalogsByUpdatedAtAfter(updatedAt);

            if (catalogs.isEmpty()) {
                logger.info("No catalogs were found matching the criteria");
                throw new WebApplicationException("No content available", Response.Status.NO_CONTENT);
            }

            return catalogs.stream()
                    .map(CatalogDTO::from)
                    .collect(Collectors.toList());
        });
    }

    @Override
    public CompletionStage<CatalogDTO> createCatalog(CatalogDTO dto) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // TODO: recuperar el usuario del contexto
                var catalog = Catalog.builder()
                        .id(dto.catalogId())
                        .userId(UUID.randomUUID())
                        .url(dto.url())
                        .build();

                Catalog result = service.createCatalog(catalog);
                return result != null ? CatalogDTO.from(result) : null;
            } catch (Exception e) {
                logger.errorf(e, "Error creating catalog with url %s: %s", dto.url(), e.getMessage());
                throw new WebApplicationException("Error creating catalog", Response.Status.INTERNAL_SERVER_ERROR);
            }
        });
    }

    @Override
    public CompletionStage<CatalogDTO> getCatalog(UUID catalogId) {
        return CompletableFuture.supplyAsync(() -> {
            Catalog catalog = service.findCatalogById(catalogId);

            if (catalog == null) {
                logger.infof("Catalog with ID %s not found", catalogId);
                throw new WebApplicationException("Catalog not found", Response.Status.NOT_FOUND);
            }

            return CatalogDTO.from(catalog);
        });
    }

    @Override
    public CompletionStage<CatalogDTO> updateCatalog(UUID catalogId, CatalogDTO dto) {
        return CompletableFuture.supplyAsync(() -> {
            Catalog catalog = service.findCatalogById(catalogId);

            if (catalog == null) {
                logger.infof("Catalog with ID %s not found", catalogId);
                throw new WebApplicationException("Catalog not found", Response.Status.NOT_FOUND);
            }

            if (dto.name() != null) catalog.changeName(dto.name());
            if (dto.author() != null) catalog.changeAuthor(dto.author());
            if (dto.url() != null) catalog.changeUrl(dto.url());

            Catalog result = service.updateCatalog(catalog);
            return result != null ? CatalogDTO.from(result) : null;
        });
    }

    @Override
    public CompletionStage<Boolean> deleteCatalog(UUID catalogId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return service.deleteCatalog(catalogId);
            } catch (Exception e) {
                logger.errorf(e, "Error deleting catalog with ID %s: %s", catalogId, e.getMessage());
                throw new WebApplicationException("Error deleting catalog", Response.Status.INTERNAL_SERVER_ERROR);
            }
        });
    }

    @Override
    public CompletionStage<Response> refreshCatalog(UUID catalogId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                service.refreshCatalog(catalogId);
                return Response.ok().build();
            } catch (Exception e) {
                logger.errorf(e, "Error refresh catalog with ID %s: %s", catalogId, e.getMessage());
                throw new WebApplicationException("Error to refresh catalog", Response.Status.INTERNAL_SERVER_ERROR);
            }
        });
    }
}
