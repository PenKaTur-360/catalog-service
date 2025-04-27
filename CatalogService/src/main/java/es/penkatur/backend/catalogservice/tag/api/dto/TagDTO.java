package es.penkatur.backend.catalogservice.tag.api.dto;

import es.penkatur.backend.catalogservice.tag.domain.Tag;
import jakarta.json.bind.annotation.JsonbProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.time.Instant;
import java.util.UUID;

@Schema(description = "DTO representing a Tag.")
public record TagDTO(
    @Schema(description = "Unique identifier of the tag", required = true)
    @NotNull(message = "Tag ID cannot be null")
    @JsonbProperty("id")
    UUID tagId,

    @Size(min = 1, max = 100, message = "Tag name must be between 1 and 100 characters")
    @Schema(description = "Name of the tag", required = true)
    String name,

    @Pattern(regexp = "^#(?:[0-9a-fA-F]{3}|[0-9a-fA-F]{6}|[0-9a-fA-F]{8})$",
            message = "Invalid color format")
    @Schema(description = "Color associated with the tag")
    String color,

    @Schema(description = "Timestamp of the last update")
    Instant updatedAt) {
    public static TagDTO from(Tag tag) {
            return new TagDTO(tag.getId(), tag.getName(), tag.getColor(), tag.getUpdatedAt());
    }
}
