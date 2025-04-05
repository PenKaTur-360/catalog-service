package es.penkatur.backend.tag.infraestructure.persistence;

import es.penkatur.backend.infraestructure.persistence.PostgresContainerTestProfile;
import es.penkatur.backend.tag.domain.Tag;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import jakarta.inject.Inject;
import org.hibernate.reactive.mutiny.Mutiny;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static io.smallrye.common.constraint.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@QuarkusTest
@TestProfile(PostgresContainerTestProfile.class)
class TagRepositoryTest {

    @Inject
    TagRepositoryImpl repository;

    @Inject
    Mutiny.SessionFactory sessionFactory;

    @Inject
    TagPanacheRepository panacheRepository;

    @BeforeEach
    void cleanDatabase() {
        sessionFactory.withTransaction(session ->
                panacheRepository.deleteAll()
        ).await().indefinitely();
    }

    @Test
    void shouldCreateAndFindTag() {
        var now = Instant.now();
        var tag = Tag.builder()
                .name("tag-test")
                .build();

        saveOrUpdateTag(tag);
        assertNotNull(tag.getId());
        assertNotNull(tag.getCreatedAt());
        assertNotNull(tag.getUpdatedAt());

        var result = sessionFactory.withTransaction(session ->
                repository.findById(tag.getId())
        ).await().indefinitely();
        assertNotNull(result);
        assertEquals("tag-test", result.getName());

        List<Tag> result2 = sessionFactory.withTransaction(session ->
                repository.findAllByUpdatedAtAfter(now.minus(1, ChronoUnit.MINUTES)).collect().asList()
        ).await().indefinitely();
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
        List<Tag> result = sessionFactory.withTransaction(session ->
                repository.findAllByUpdatedAtAfter(Instant.now().plus(1, ChronoUnit.MINUTES)).collect().asList()
        ).await().indefinitely();
        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    void shouldCreateAndUpdateTag() throws InterruptedException {
        var tag = Tag.builder()
                .name("tag-test")
                .build();
        var id = tag.getId();

        saveOrUpdateTag(tag);
        Thread.sleep(Duration.of(1, ChronoUnit.SECONDS));

        tag.changeColor("#f00");
        saveOrUpdateTag(tag);

        List<Tag> result = sessionFactory.withTransaction(session ->
                repository.findAllByUpdatedAtAfter(Instant.now().minus(1, ChronoUnit.HOURS)).collect().asList()
        ).await().indefinitely();
        assertNotNull(result);
        assertEquals(1, result.size());

        assertEquals(id, tag.getId());
        assertNotEquals(tag.getCreatedAt(), tag.getUpdatedAt());
        assertEquals("tag-test", tag.getName());
        assertEquals("#f00", tag.getColor());
    }

    private void saveOrUpdateTag(Tag tag) {
        sessionFactory.withTransaction(session ->
                repository.save(tag)
        ).await().indefinitely();
    }
}
