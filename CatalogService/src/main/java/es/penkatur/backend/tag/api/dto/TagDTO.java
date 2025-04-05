package es.penkatur.backend.tag.api.dto;

import es.penkatur.backend.tag.domain.Tag;
import jakarta.json.bind.annotation.JsonbProperty;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.time.Instant;
import java.util.UUID;

@Schema(description = "DTO representing a Tag.")
public record TagDTO(
        @Schema(description = "Unique identifier of the tag", required = true)
        @JsonbProperty("id")
        UUID tagId,

        @Schema(description = "Name of the tag", required = true)
        String name,

        @Schema(description = "Color associated with the tag")
        String color,

        @Schema(description = "Timestamp of the last update")
        Instant updatedAt
) {
    public static TagDTO from(Tag tag) {
        return new TagDTO(tag.getId(), tag.getName(), tag.getColor(), tag.getUpdatedAt());
    }
}
