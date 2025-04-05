package es.penkatur.backend.tag.application;

import es.penkatur.backend.tag.domain.Tag;
import es.penkatur.backend.tag.domain.TagRepository;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

import static io.smallrye.common.constraint.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@QuarkusTest
class TagServiceTest {

    @Inject
    TagService service;

    @InjectMock
    TagRepository repository;

    @Test
    void shouldFoundTag() {
        var tag = Tag.builder().name("test").build();
        Mockito.when(repository.findById(tag.getId())).thenReturn(Uni.createFrom().item(tag));

        Tag result = service.findTagById(tag.getId()).await().indefinitely();
        checkTag(tag, result);
    }

    @Test
    void shouldNotFoundTag() {
        var tag = Tag.builder().name("test").build();
        Mockito.when(repository.findById(tag.getId())).thenReturn(Uni.createFrom().item(tag));

        var result = service.findTagById(UUID.randomUUID()).await().indefinitely();
        assertNull(result, "Expected result to be null since the tag should not be found.");
    }

    @Test
    void shouldFoundAllTags() {
        var tag1 = Tag.builder().name("test1").build();
        var tag2 = Tag.builder().name("test2").build();
        var now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        Mockito.when(repository.findAllByUpdatedAtAfter(now)).thenReturn(Multi.createFrom().items(tag1, tag2));

        List<Tag> result = service.findAllTagsByUpdatedAtAfter(now).collect().asList().await().indefinitely();
        assertNotNull(result);
        assertEquals(2, result.size(), "Expected the result list to contain exactly 2 tags.");

        List<Tag> result2 = service.findAllTagsByUpdatedAtAfter(now.minus(1, ChronoUnit.MINUTES)).collect().asList().await().indefinitely();
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
