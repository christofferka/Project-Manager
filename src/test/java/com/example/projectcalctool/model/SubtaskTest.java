package com.example.projectcalctool.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SubtaskTest {

    @Test
    @DisplayName("getHours returns actualHours when present")
    void getHours_usesActualWhenPresent() {
        // Given
        Subtask s = new Subtask();
        s.setEstimatedHours(4);
        s.setActualHours(3.0);

        // When / Then
        assertEquals(3.0, s.getHours());
    }

    @Test
    @DisplayName("getHours falls back to estimatedHours when actual is null")
    void getHours_fallsBackToEstimated() {
        // Given
        Subtask s = new Subtask();
        s.setEstimatedHours(4);
        s.setActualHours(null);

        // When / Then
        assertEquals(4.0, s.getHours());
    }

    @Test
    @DisplayName("isDone true only when status is DONE")
    void isDone_trueOnlyForDone() {
        // Given
        Subtask done = new Subtask(); done.setStatus(TaskStatus.DONE);
        Subtask progress = new Subtask(); progress.setStatus(TaskStatus.IN_PROGRESS);

        // When / Then
        assertTrue(done.isDone());
        assertFalse(progress.isDone());
    }

    @Test
    @DisplayName("getStatusDisplay returns Danish label for each status")
    void getStatusDisplay_danishLabels() {
        // Given
        Subtask notStarted = new Subtask(); notStarted.setStatus(TaskStatus.NOT_STARTED);
        Subtask inProgress = new Subtask(); inProgress.setStatus(TaskStatus.IN_PROGRESS);
        Subtask done = new Subtask(); done.setStatus(TaskStatus.DONE);

        // When / Then
        assertEquals("Ikke startet", notStarted.getStatusDisplay());
        assertEquals("I gang", inProgress.getStatusDisplay());
        assertEquals("Færdig", done.getStatusDisplay());
    }

    @Test
    @DisplayName("getStatusDisplay returns Ukendt when status is null")
    void getStatusDisplay_nullReturnsUnknown() {
        // Given
        Subtask s = new Subtask();
        s.setStatus(null);

        // When / Then
        assertEquals("Ukendt", s.getStatusDisplay());
    }
}
