package es.penkatur.backend.catalogservice.catalog.api.dto;

import es.penkatur.backend.catalogservice.catalog.domain.Catalog;
import jakarta.json.bind.annotation.JsonbProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.hibernate.validator.constraints.URL;

import java.time.Instant;
import java.util.UUID;

@Schema(description = "DTO representing a Catalog.")
public record CatalogDTO(
        @Schema(description = "Unique identifier of the catalog", required = true)
        @NotNull(message = "Catalog ID cannot be null")
        @JsonbProperty("id")
        UUID catalogId,

        @Size(min = 1, max = 100, message = "Catalog name must be between 1 and 100 characters")
        @Schema(description = "Name of the catalog")
        String name,

        @Size(min = 1, max = 100, message = "Author must be between 1 and 100 characters")
        @Schema(description = "Author of the catalog")
        String author,

        @Pattern(regexp = "^(\\d+)(\\.\\d+)*(-[A-Za-z0-9]+(?:\\.[A-Za-z0-9]+)*)?$",
                message = "Invalid version format")
        @Schema(description = "Version of the catalog")
        String version,

        @Schema(description = "Url of the catalog", required = true)
        @URL(message = "URL must be in a valid format")
        @NotBlank(message = "URL cannot be empty")
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
