package es.penkatur.backend.catalogservice.catalog.domain.events;

import java.util.UUID;

public interface CatalogEventPublisher {
    void publishCatalogUpdateEvent(UUID catalogId);
}
