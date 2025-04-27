package es.penkatur.backend.catalogservice.tag.domain;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class TagTest {
    @Test
    void shouldCreateTag() {
        var tag = Tag.builder()
                .name("test tag")
                .build();

        assertNotNull(tag, "The tag should not be null");
        assertNotNull(tag.getId(), "The ID should not be null");
        assertNotNull(tag.getName(), "The name should not be null");
        assertEquals("test tag", tag.getName(), "The name should match the expected value");
        assertNull(tag.getColor(), "The color should be null");
        assertNull(tag.getCreatedAt(), "The creation time should be null");
        assertNull(tag.getUpdatedAt(), "The update time should be null");
    }

    @Test
    void shouldChangeTagName() {
        var tag = Tag.builder()
                .name("TEST TAG")
                .build();

        assertNotNull(tag, "The tag should not be null");
        assertNotNull(tag.getName(), "The name should not be null");
        assertEquals("test tag", tag.getName(), "The name should match the expected value");
    }

    @Test
    void shouldCreateCompleteTag() {
        Instant now = Instant.now();
        var tag = Tag.builder()
                .name("test tag")
                .color("#f00")
                .createdAt(now)
                .updatedAt(now)
                .build();

        assertNotNull(tag, "The tag should not be null");
        assertNotNull(tag.getId(), "The ID should not be null");
        assertNotNull(tag.getName(), "The name should not be null");
        assertEquals("test tag", tag.getName(), "The name should match the expected value");
        assertNotNull(tag.getColor(), "The color should not be null");
        assertEquals("#f00", tag.getColor(), "The color should match the expected value");
        assertNotNull(tag.getCreatedAt(), "The creation time should not be null");
        assertEquals(now.truncatedTo(ChronoUnit.SECONDS), tag.getCreatedAt(), "The creation time should match the expected value");
        assertNotNull(tag.getUpdatedAt(), "The update time should not be null");
        assertEquals(now.truncatedTo(ChronoUnit.SECONDS), tag.getUpdatedAt(), "The update time should match the expected value");
    }

    @Test
    void shouldThrowsNullPointerExceptionForName() {
        assertThrowsExactly(NullPointerException.class,
                () -> Tag.builder().build(),
                "name is marked non-null but is null");
    }

    @Test
    void shouldThrowsIllegalArgumentExceptionForName() {
        assertThrowsExactly(IllegalArgumentException.class,
                () -> Tag.builder().name(null),
                "Name cannot be null");
    }

    @Test
    void shouldThrowsIllegalArgumentExceptionForColor() {
        assertThrowsExactly(IllegalArgumentException.class,
                () -> Tag.builder().color("red"),
                "The entered color is not a valid hexadecimal value. Please enter a color in the format #RGB, #RRGGBB or #RRGGBBAA");
    }

    @Test
    void shouldCreateValidColor() {
        assertDoesNotThrow(() -> Tag.builder().color("#f00"), "Expected color '#f00' to be valid, but it threw an exception.");
        assertDoesNotThrow(() -> Tag.builder().color("#ff0000"), "Expected color '#ff0000' to be valid, but it threw an exception.");
        assertDoesNotThrow(() -> Tag.builder().color("#ff0000f0"), "Expected color '#ff0000f0' to be valid, but it threw an exception.");
    }

    @Test
    void shouldChangeColor() {
        var tag = Tag.builder().name("test").build();
        assertNull(tag.getColor(), "Expected the initial color of the tag to be null.");

        tag.changeColor("#f00");
        assertEquals("#f00", tag.getColor(), "Expected the tag color to be '#f00' after changing it.");

        tag.changeColor("#ff0000");
        assertEquals("#ff0000", tag.getColor(), "Expected the tag color to be '#ff0000' after changing it again.");
    }
}
