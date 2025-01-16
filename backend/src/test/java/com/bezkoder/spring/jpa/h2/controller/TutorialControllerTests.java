package com.bezkoder.spring.jpa.h2.controller;

import com.bezkoder.spring.jpa.h2.model.Tutorial;
import com.bezkoder.spring.jpa.h2.repository.TutorialRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TutorialControllerTests {

    @Mock
    private TutorialRepository tutorialRepository;

    @InjectMocks
    private TutorialController tutorialController;

    private Tutorial tutorial;

    @BeforeEach
    void setUp() {
        tutorial = new Tutorial("Spring Boot", "Learn Spring Boot", false);
        tutorial.setId(1L);
    }

    @Test
    public void testExample() {
        assertTrue(true);
    }

    @Test
    void testGetAllTutorials() {
        // Arrange
        List<Tutorial> tutorials = new ArrayList<>();
        tutorials.add(tutorial);
        when(tutorialRepository.findAll()).thenReturn(tutorials);

        // Act
        ResponseEntity<List<Tutorial>> response = tutorialController.getAllTutorials(null);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        verify(tutorialRepository, times(1)).findAll();
    }

    @Test
    void testGetAllTutorialsByTitle() {
        // Arrange
        List<Tutorial> tutorials = new ArrayList<>();
        tutorials.add(tutorial);
        when(tutorialRepository.findByTitleContainingIgnoreCase("Spring")).thenReturn(tutorials);

        // Act
        ResponseEntity<List<Tutorial>> response = tutorialController.getAllTutorials("Spring");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        verify(tutorialRepository, times(1)).findByTitleContainingIgnoreCase("Spring");
    }

    @Test
    void testGetTutorialById() {
        // Arrange
        when(tutorialRepository.findById(1L)).thenReturn(Optional.of(tutorial));

        // Act
        ResponseEntity<Tutorial> response = tutorialController.getTutorialById(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(tutorial.getTitle(), response.getBody().getTitle());
        verify(tutorialRepository, times(1)).findById(1L);
    }

    @Test
    void testGetTutorialByIdNotFound() {
        // Arrange
        when(tutorialRepository.findById(1L)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<Tutorial> response = tutorialController.getTutorialById(1L);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(tutorialRepository, times(1)).findById(1L);
    }

    @Test
    void testCreateTutorial() {
        // Arrange
        when(tutorialRepository.save(any(Tutorial.class))).thenReturn(tutorial);

        // Act
        ResponseEntity<Tutorial> response = tutorialController.createTutorial(tutorial);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(tutorial.getTitle(), response.getBody().getTitle());
        verify(tutorialRepository, times(1)).save(any(Tutorial.class));
    }

    @Test
    void testUpdateTutorial() {
        // Arrange
        when(tutorialRepository.findById(1L)).thenReturn(Optional.of(tutorial));
        when(tutorialRepository.save(any(Tutorial.class))).thenReturn(tutorial);

        Tutorial updatedTutorial = new Tutorial("Updated Title", "Updated Description", true);

        // Act
        ResponseEntity<Tutorial> response = tutorialController.updateTutorial(1L, updatedTutorial);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedTutorial.getTitle(), response.getBody().getTitle());
        assertEquals(updatedTutorial.getDescription(), response.getBody().getDescription());
        assertEquals(updatedTutorial.isPublished(), response.getBody().isPublished());
        verify(tutorialRepository, times(1)).findById(1L);
        verify(tutorialRepository, times(1)).save(any(Tutorial.class));
    }

    @Test
    void testUpdateTutorialNotFound() {
        // Arrange
        when(tutorialRepository.findById(1L)).thenReturn(Optional.empty());

        Tutorial updatedTutorial = new Tutorial("Updated Title", "Updated Description", true);

        // Act
        ResponseEntity<Tutorial> response = tutorialController.updateTutorial(1L, updatedTutorial);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(tutorialRepository, times(1)).findById(1L);
        verify(tutorialRepository, never()).save(any(Tutorial.class));
    }

    @Test
    void testDeleteTutorial() {
        // Arrange
        doNothing().when(tutorialRepository).deleteById(1L);

        // Act
        ResponseEntity<HttpStatus> response = tutorialController.deleteTutorial(1L);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(tutorialRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteAllTutorials() {
        // Arrange
        doNothing().when(tutorialRepository).deleteAll();

        // Act
        ResponseEntity<HttpStatus> response = tutorialController.deleteAllTutorials();

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(tutorialRepository, times(1)).deleteAll();
    }

    @Test
    void testFindByPublished() {
        // Arrange
        List<Tutorial> tutorials = new ArrayList<>();
        tutorials.add(tutorial);
        when(tutorialRepository.findByPublished(true)).thenReturn(tutorials);

        // Act
        ResponseEntity<List<Tutorial>> response = tutorialController.findByPublished();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        verify(tutorialRepository, times(1)).findByPublished(true);
    }
}
