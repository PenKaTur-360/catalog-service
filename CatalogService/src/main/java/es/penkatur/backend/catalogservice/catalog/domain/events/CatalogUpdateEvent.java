package es.penkatur.backend.catalogservice.catalog.domain.events;

import java.util.UUID;

public record CatalogUpdateEvent(UUID catalogId) {
}
