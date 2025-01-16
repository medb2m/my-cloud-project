package com.bezkoder.spring.jpa.h2.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TutorialTests {

    @Test
    void testTutorialConstructor() {
        // Arrange
        String title = "Spring Boot Tutorial";
        String description = "Learn Spring Boot with H2 Database";
        boolean published = true;

        // Act
        Tutorial tutorial = new Tutorial(title, description, published);

        // Assert
        assertEquals(title, tutorial.getTitle());
        assertEquals(description, tutorial.getDescription());
        assertEquals(published, tutorial.isPublished());
    }

    @Test
    void testTutorialSettersAndGetters() {
        // Arrange
        Tutorial tutorial = new Tutorial();
        String title = "Mockito Tutorial";
        String description = "Learn Mockito for Unit Testing";
        boolean published = false;

        // Act
        tutorial.setTitle(title);
        tutorial.setDescription(description);
        tutorial.setPublished(published);

        // Assert
        assertEquals(title, tutorial.getTitle());
        assertEquals(description, tutorial.getDescription());
        assertEquals(published, tutorial.isPublished());
    }

    @Test
    void testTutorialToString() {
        // Arrange
        Tutorial tutorial = new Tutorial("JUnit Tutorial", "Learn JUnit for Testing", true);

        // Act
        String result = tutorial.toString();

        // Assert
        assertTrue(result.contains("JUnit Tutorial"));
        assertTrue(result.contains("Learn JUnit for Testing"));
        assertTrue(result.contains("true"));
    }
}
