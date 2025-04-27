package es.penkatur.backend.catalogservice.tag.application;

import es.penkatur.backend.catalogservice.tag.domain.Tag;
import es.penkatur.backend.catalogservice.tag.domain.TagRepository;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class TagServiceTest {
    @Inject
    TagService service;

    @InjectMock
    TagRepository repository;

    @Test
    void shouldFoundTag() {
        var tag = Tag.builder().name("test").build();
        Mockito.when(repository.findById(tag.getId())).thenReturn(tag);

        Tag result = service.findTagById(tag.getId());
        checkTag(tag, result);
    }

    @Test
    void shouldNotFoundTag() {
        var tag = Tag.builder().name("test").build();
        Mockito.when(repository.findById(tag.getId())).thenReturn(tag);

        var result = service.findTagById(UUID.randomUUID());
        assertNull(result, "Expected result to be null since the tag should not be found.");
    }

    @Test
    void shouldFoundAllTags() {
        var tag1 = Tag.builder().name("test1").build();
        var tag2 = Tag.builder().name("test2").build();
        var now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        Mockito.when(repository.findAllByUpdatedAtAfter(now)).thenReturn(List.of(tag1, tag2));

        List<Tag> result = service.findAllTagsByUpdatedAtAfter(now);
        assertNotNull(result);
        assertEquals(2, result.size(), "Expected the result list to contain exactly 2 tags.");

        List<Tag> result2 = service.findAllTagsByUpdatedAtAfter(now.minus(1, ChronoUnit.MINUTES));
        assertNotNull(result2);
        assertEquals(0, result2.size(), "Expected the result list to be empty since no tags should match the criteria.");
    }

    private void checkTag(Tag expected, Tag actual) {
        assertNotNull(actual);
        assertEquals(expected.getId(), actual.getId(), "Expected the IDs of the tags to match.");
        assertEquals(expected.getName(), actual.getName(), "Expected the names of the tags to match.");
        assertEquals(expected.getColor(), actual.getColor(), "Expected the colors of the tags to match.");
        assertEquals(expected.getCreatedAt(), actual.getCreatedAt(), "Expected the creation dates of the tags to match.");
        assertEquals(expected.getUpdatedAt(), actual.getUpdatedAt(), "Expected the update dates of the tags to match.");
    }
}
