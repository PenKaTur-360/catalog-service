package es.penkatur.backend.tag.infraestructure.persistence.mapper;

import es.penkatur.backend.tag.domain.Tag;
import es.penkatur.backend.tag.infraestructure.entity.TagEntity;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
public class TagMapperTest {

    @Test
    void shouldMapDomainToEntity() {
        var tag = Tag.builder()
                .name("domain-tag-test")
                .color("#ff0000")
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
        checkTag(tag, TagMapper.toEntity(tag));
    }

    @Test
    void shouldMapEntityToDomain() {
        var entity = new TagEntity();
        entity.setId(UUID.randomUUID());
        entity.setName("entity-tag-test");
        entity.setColor("#f00");
        entity.setCreatedAt(Instant.now().truncatedTo(ChronoUnit.SECONDS));
        entity.setUpdatedAt(Instant.now().truncatedTo(ChronoUnit.SECONDS));
        checkTag(TagMapper.toDomain(entity), entity);
    }

    private void checkTag(Tag tag, TagEntity entity) {
        assertEquals(tag.getId(), entity.getId(), "The IDs do not match!");
        assertEquals(tag.getName(), entity.getName(), "The names do not match!");
        assertEquals(tag.getColor(), entity.getColor(), "The colors do not match!");
        assertEquals(tag.getCreatedAt(), entity.getCreatedAt(), "The creation dates do not match!");
        assertEquals(tag.getUpdatedAt(), entity.getUpdatedAt(), "The update dates do not match!");
    }
}
