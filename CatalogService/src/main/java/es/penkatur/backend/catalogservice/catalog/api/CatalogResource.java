package es.penkatur.backend.catalogservice.catalog.api;

import es.penkatur.backend.catalogservice.catalog.api.dto.CatalogDTO;
import es.penkatur.backend.catalogservice.catalog.application.CatalogService;
import es.penkatur.backend.catalogservice.catalog.domain.Catalog;
import es.penkatur.backend.catalogservice.catalog.domain.exceptions.InvalidCatalogException;
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
            try {
                List<Catalog> catalogs = service.findAllCatalogsByUpdatedAtAfter(updatedAt);

                if (catalogs.isEmpty()) {
                    logger.info("No catalogs were found matching the criteria");
                    throw new WebApplicationException("No content available", Response.Status.NO_CONTENT);
                }

                return catalogs.stream()
                        .map(CatalogDTO::from)
                        .collect(Collectors.toList());
            } catch (WebApplicationException e) {
                throw e;
            } catch (Exception e) {
                logger.error("Error listing catalogs", e);
                throw new WebApplicationException("Internal error processing the request",
                        Response.Status.INTERNAL_SERVER_ERROR);
            }
        });
    }

    @Override
    public CompletionStage<CatalogDTO> createCatalog(CatalogDTO dto) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                if (dto == null)
                    throw new WebApplicationException("Catalog data cannot be null", Response.Status.BAD_REQUEST);

                // TODO: recuperar el usuario del contexto
                var catalog = Catalog.builder()
                        .id(dto.catalogId())
                        .userId(UUID.randomUUID())
                        .url(dto.url())
                        .build();

                Catalog result = service.createCatalog(catalog);
                return result != null ? CatalogDTO.from(result) : null;
            } catch (WebApplicationException e) {
                throw e;
            } catch (InvalidCatalogException e) {
                logger.warnf("Invalid catalog data: %s", e.getMessage());
                throw new WebApplicationException(e.getMessage(), Response.Status.BAD_REQUEST);
            } catch (Exception e) {
                logger.errorf(e, "Error creating catalog with url %s: %s", dto.url(), e.getMessage());
                throw new WebApplicationException("Error creating catalog", Response.Status.INTERNAL_SERVER_ERROR);
            }
        });
    }

    @Override
    public CompletionStage<CatalogDTO> getCatalog(UUID catalogId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                if (catalogId == null)
                    throw new WebApplicationException("Catalog ID cannot be null or empty", Response.Status.BAD_REQUEST);

                Catalog catalog = service.findCatalogById(catalogId);
                if (catalog == null) {
                    logger.infof("Catalog with ID %s not found", catalogId);
                    throw new WebApplicationException("Catalog not found", Response.Status.NOT_FOUND);
                }

                return CatalogDTO.from(catalog);
            } catch (WebApplicationException e) {
                throw e;
            } catch (Exception e) {
                logger.errorf(e, "Error listing catalog: %s", catalogId);
                throw new WebApplicationException("Internal error processing the request",
                        Response.Status.INTERNAL_SERVER_ERROR);
            }
        });
    }

    @Override
    public CompletionStage<CatalogDTO> updateCatalog(UUID catalogId, CatalogDTO dto) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                if (catalogId == null)
                    throw new WebApplicationException("Catalog ID cannot be null or empty", Response.Status.BAD_REQUEST);
                if (dto == null)
                    throw new WebApplicationException("Catalog data cannot be null", Response.Status.BAD_REQUEST);
                if (dto.catalogId() != null && !dto.catalogId().equals(catalogId))
                    throw new WebApplicationException("The IDs not match", Response.Status.BAD_REQUEST);

                Catalog catalog = service.findCatalogById(catalogId);
                if (catalog == null) {
                    logger.infof("Catalog with ID %s not found", catalogId);
                    throw new WebApplicationException("Catalog not found", Response.Status.NOT_FOUND);
                }

                if (dto.name() != null) catalog.changeName(dto.name());
                if (dto.author() != null) catalog.changeAuthor(dto.author());
                if (dto.url() != null) catalog.changeUrl(dto.url());

                Catalog result = service.updateCatalog(catalog);
                return CatalogDTO.from(result);
            } catch (WebApplicationException e) {
                throw e;
            } catch (InvalidCatalogException e) {
                logger.warnf("Invalid catalog data: %s", e.getMessage());
                throw new WebApplicationException(e.getMessage(), Response.Status.BAD_REQUEST);
            } catch (Exception e) {
                logger.errorf(e, "Error updating catalog: %s", catalogId);
                throw new WebApplicationException("Error processing update request", Response.Status.INTERNAL_SERVER_ERROR);
            }
        });
    }

    @Override
    public CompletionStage<Boolean> deleteCatalog(UUID catalogId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                if (catalogId == null)
                    throw new WebApplicationException("Catalog ID cannot be null or empty", Response.Status.BAD_REQUEST);

                Catalog catalog = service.findCatalogById(catalogId);
                if (catalog == null) {
                    logger.infof("Catalog with ID %s not found", catalogId);
                    throw new WebApplicationException("Catalog not found", Response.Status.NOT_FOUND);
                }

                return service.deleteCatalog(catalogId);
            } catch (WebApplicationException e) {
                throw e;
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
                if (catalogId == null)
                    throw new WebApplicationException("Catalog ID cannot be null or empty", Response.Status.BAD_REQUEST);

                Catalog catalog = service.findCatalogById(catalogId);
                if (catalog == null) {
                    logger.infof("Catalog with ID %s not found", catalogId);
                    throw new WebApplicationException("Catalog not found", Response.Status.NOT_FOUND);
                }

                service.refreshCatalog(catalogId);
                return Response.ok().build();
            } catch (WebApplicationException e) {
                throw e;
            } catch (Exception e) {
                logger.errorf(e, "Error refresh catalog with ID %s: %s", catalogId, e.getMessage());
                throw new WebApplicationException("Error to refresh catalog", Response.Status.INTERNAL_SERVER_ERROR);
            }
        });
    }
}
