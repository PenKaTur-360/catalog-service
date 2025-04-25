package es.penkatur.backend.catalogservice.catalog.domain;

import es.penkatur.backend.sharedkernel.domain.UUIDBaseModel;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Builder
@Getter
public class Catalog implements UUIDBaseModel {
    @NonNull
    private UUID id;
    @NonNull
    private UUID userId;
    private String key;
    private String name;
    private String author;
    private String version;
    @NonNull
    private String url;
    // TODO: add ingredients
    // TODO: add meals
    private Instant createdAt;
    private Instant updatedAt;
    private Instant externalUpdatedAt;

    public static Catalog.CatalogBuilder builder() {
        return new Catalog.CatalogBuilder().id(UUID.randomUUID());
    }

    private static void validateVersion(String version) {
        if (version != null && !version.isBlank()) {
            String versionPattern = "^(\\d+)(\\.\\d+)*(-[A-Za-z0-9]+(?:\\.[A-Za-z0-9]+)*)?$";
            if (!version.matches(versionPattern))
                throw new IllegalArgumentException("The entered version is not a valid version value.");
        }
    }

    public void changeName(String name) {
        if (name == null || name.isBlank())
            throw new IllegalArgumentException("Name cannot be null");
        this.name = name;
    }

    public void changeAuthor(String author) {
        if (author == null || author.isBlank())
            throw new IllegalArgumentException("Author cannot be null");
        this.author = author;
    }

    public void changeVersion(String version) {
        if (version == null || version.isBlank())
            throw new IllegalArgumentException("Version cannot be null");
        validateVersion(version);
        if (!isNewerVersion(version))
            throw new IllegalArgumentException("The entered version must not be lower than the current version.");
        this.version = version;
    }

    public boolean isNewerVersion(String version) {
        ArtifactVersion v1 = new DefaultArtifactVersion(getVersion());
        ArtifactVersion v2 = new DefaultArtifactVersion(version);

        if (v1.compareTo(v2) == 0)
            return getVersion().endsWith("SNAPSHOT") && version.endsWith("SNAPSHOT");
        return v1.compareTo(v2) < 0;
    }

    public void changeUrl(String url) {
        if (url == null || url.isBlank())
            throw new IllegalArgumentException("Url cannot be null");
        this.url = url;
    }

    public void changeExternalUpdatedAt(Instant externalUpdatedAt) {
        if (externalUpdatedAt == null)
            throw new IllegalArgumentException("External update date cannot be null");
        externalUpdatedAt = externalUpdatedAt.truncatedTo(ChronoUnit.SECONDS);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm:ss")
                .withZone(ZoneId.systemDefault());
        if (this.externalUpdatedAt != null && externalUpdatedAt.isBefore(this.externalUpdatedAt))
            throw new IllegalArgumentException(
                    String.format("The 'externalUpdatedAt' (%s) cannot be earlier than the current " +
                                    "'externalUpdatedAt' (%s)", formatter.format(externalUpdatedAt),
                            formatter.format(this.externalUpdatedAt)));
        this.externalUpdatedAt = externalUpdatedAt;
    }

    public static class CatalogBuilder {
        public Catalog.CatalogBuilder version(String version) {
            validateVersion(version);
            this.version = version;
            return this;
        }

        public Catalog.CatalogBuilder createdAt(Instant createdAt) {
            this.createdAt = createdAt != null ? createdAt.truncatedTo(ChronoUnit.SECONDS) : null;
            return this;
        }

        public Catalog.CatalogBuilder updatedAt(Instant updatedAt) {
            this.updatedAt = updatedAt != null ? updatedAt.truncatedTo(ChronoUnit.SECONDS) : null;
            return this;
        }

        public Catalog.CatalogBuilder externalUpdatedAt(Instant externalUpdatedAt) {
            this.externalUpdatedAt = externalUpdatedAt != null ? externalUpdatedAt.truncatedTo(ChronoUnit.SECONDS) : null;
            return this;
        }
    }
}
