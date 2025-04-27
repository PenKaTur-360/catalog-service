package es.penkatur.backend.catalogservice.tag.domain;

import es.penkatur.backend.sharedkernel.domain.UUIDBaseModel;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Builder
@Getter
public class Tag implements UUIDBaseModel {
    @NonNull
    private UUID id;
    @NonNull
    private String name;
    private String color;
    private Instant createdAt;
    private Instant updatedAt;

    public static TagBuilder builder() {
        return new TagBuilder().id(UUID.randomUUID());
    }

    private static void validateColor(String color) {
        if (color != null && !color.isBlank()) {
            var hexPattern = "^#(?:[0-9a-fA-F]{3}|[0-9a-fA-F]{6}|[0-9a-fA-F]{8})$";
            if (!color.matches(hexPattern))
                throw new IllegalArgumentException(
                        "The entered color is not a valid hexadecimal value. Please enter a color in the format #RGB, #RRGGBB or #RRGGBBAA");
        }
    }

    public void changeColor(String color) {
        validateColor(color);
        this.color = color;
    }

    public static class TagBuilder {
        public TagBuilder name(String name) {
            if (name == null || name.isBlank())
                throw new IllegalArgumentException("Name cannot be null");
            this.name = name.toLowerCase();
            return this;
        }

        public TagBuilder color(String color) {
            validateColor(color);
            this.color = color;
            return this;
        }

        public TagBuilder createdAt(Instant createdAt) {
            this.createdAt = createdAt != null ? createdAt.truncatedTo(ChronoUnit.SECONDS) : null;
            return this;
        }

        public TagBuilder updatedAt(Instant updatedAt) {
            this.updatedAt = updatedAt != null ? updatedAt.truncatedTo(ChronoUnit.SECONDS) : null;
            return this;
        }
    }
}
