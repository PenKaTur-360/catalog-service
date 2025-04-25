package es.penkatur.backend.catalogservice.catalog.api.dto;

import es.penkatur.backend.catalogservice.catalog.domain.Catalog;
import jakarta.json.bind.annotation.JsonbProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.time.Instant;
import java.util.UUID;

@Schema(description = "DTO representing a Catalog.")
public record CatalogDTO(
        @Schema(description = "Unique identifier of the catalog", required = true)
        @JsonbProperty("id")
        @NotNull
        UUID catalogId,

        @Schema(description = "Name of the catalog")
        String name,

        @Schema(description = "Author of the catalog")
        String author,

        @Schema(description = "Version of the catalog")
        String version,

        @Schema(description = "Url of the catalog", required = true)
        @NotBlank
        String url,

        @Schema(description = "Timestamp of the last update")
        Instant updatedAt,

        @Schema(description = "Timestamp of the last external update")
        Instant externalUpdatedAt) {
    public static CatalogDTO from(Catalog catalog) {
        return new CatalogDTO(catalog.getId(), catalog.getName(), catalog.getAuthor(), catalog.getVersion(),
                catalog.getUrl(), catalog.getUpdatedAt(), catalog.getExternalUpdatedAt());
    }
}
