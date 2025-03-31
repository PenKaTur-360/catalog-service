package es.penkatur.backend.tag.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Builder
@Getter
public class Tag {

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

    public void changeUpdatedAt(Instant updatedAt) {
        if (updatedAt == null)
            throw new IllegalArgumentException("Update date cannot be null");
        if (createdAt != null && updatedAt.isBefore(createdAt))
            throw new IllegalArgumentException("The 'updatedAt' date cannot be earlier than the 'createdAt' date");
        if (this.updatedAt != null && updatedAt.isBefore(this.updatedAt))
            throw new IllegalArgumentException(
                    "The 'updatedAt' date cannot be earlier than the current 'updatedAt' date");
        this.updatedAt = updatedAt.truncatedTo(ChronoUnit.SECONDS);
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
