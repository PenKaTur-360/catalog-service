package es.penkatur.backend.catalogservice.tag.infrastructure.persistence;

import es.penkatur.backend.catalogservice.tag.domain.Tag;
import es.penkatur.backend.catalogservice.tag.domain.TagRepository;
import es.penkatur.backend.sharedkernel.infrastructure.persistence.PostgresContainerTestProfile;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@Transactional
@TestProfile(PostgresContainerTestProfile.class)
class TagRepositoryTest {

    @Inject
    TagRepository repository;

    @Inject
    TagPanacheRepository panacheRepository;

    @BeforeEach
    void cleanDatabase() {
        panacheRepository.deleteAll();
    }

    @Test
    void shouldCreateAndFindTag() {
        var now = Instant.now();
        var tag = Tag.builder()
                .name("tag-test")
                .build();

        saveOrUpdateTag(tag);
        assertNotNull(tag.getId());
        assertNull(tag.getCreatedAt());
        assertNull(tag.getUpdatedAt());

        var result = find(tag.getId());
        assertNotNull(result);
        assertEquals("tag-test", result.getName());

        List<Tag> result2 = findAll(now.minus(1, ChronoUnit.MINUTES));
        assertNotNull(result2);
        assertEquals(1, result2.size());
        assertEquals("tag-test", result2.getFirst().getName());
    }

    @Test
    void shouldCreateAndNotFoundTag() {
        var tag = Tag.builder()
                .name("tag-test")
                .build();

        saveOrUpdateTag(tag);
        List<Tag> result = findAll(Instant.now().plus(1, ChronoUnit.MINUTES));
        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    void shouldCreateAndUpdateTag() throws InterruptedException {
        var tag = Tag.builder()
                .name("tag-test")
                .build();
        var id = tag.getId();

        tag = saveOrUpdateTag(tag);
        Thread.sleep(Duration.of(1, ChronoUnit.SECONDS));

        tag.changeColor("#f00");
        tag = saveOrUpdateTag(tag);

        List<Tag> result = findAll(Instant.now().minus(1, ChronoUnit.HOURS));
        assertNotNull(result);
        assertEquals(1, result.size());

        assertEquals(id, tag.getId());
        assertEquals("tag-test", tag.getName());
        assertEquals("#f00", tag.getColor());
    }

    private Tag saveOrUpdateTag(Tag tag) {
        return repository.save(tag);
    }

    private Tag find(UUID id) {
        return repository.findById(id);
    }

    private List<Tag> findAll(Instant timestamp) {
        return repository.findAllByUpdatedAtAfter(timestamp);
    }
}
