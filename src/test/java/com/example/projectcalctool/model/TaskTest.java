package com.example.projectcalctool.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {

    @Test
    @DisplayName("getHours returns actualHours when present")
    void getHours_usesActualWhenPresent() {
        // Given
        Task t = new Task();
        t.setEstimatedHours(10);
        t.setActualHours(7.0);

        // When / Then
        assertEquals(7.0, t.getHours());
    }

    @Test
    @DisplayName("getHours falls back to estimatedHours when actual is null")
    void getHours_fallsBackToEstimated() {
        // Given
        Task t = new Task();
        t.setEstimatedHours(10);
        t.setActualHours(null);

        // When / Then
        assertEquals(10.0, t.getHours());
    }

    @Test
    @DisplayName("isDone true only when status is DONE")
    void isDone_trueOnlyForDone() {
        // Given
        Task done = new Task(); done.setStatus(TaskStatus.DONE);
        Task progress = new Task(); progress.setStatus(TaskStatus.IN_PROGRESS);
        Task notStarted = new Task(); notStarted.setStatus(TaskStatus.NOT_STARTED);
        Task nullStatus = new Task(); nullStatus.setStatus(null);

        // When / Then
        assertTrue(done.isDone());
        assertFalse(progress.isDone());
        assertFalse(notStarted.isDone());
        assertFalse(nullStatus.isDone());
    }

    @Test
    @DisplayName("getStatusDisplay returns Danish label for each status")
    void getStatusDisplay_danishLabels() {
        // Given
        Task notStarted = new Task(); notStarted.setStatus(TaskStatus.NOT_STARTED);
        Task inProgress = new Task(); inProgress.setStatus(TaskStatus.IN_PROGRESS);
        Task done = new Task(); done.setStatus(TaskStatus.DONE);

        // When / Then
        assertEquals("Ikke startet", notStarted.getStatusDisplay());
        assertEquals("I gang", inProgress.getStatusDisplay());
        assertEquals("Færdig", done.getStatusDisplay());
    }

    @Test
    @DisplayName("getStatusDisplay returns Ukendt when status is null")
    void getStatusDisplay_nullReturnsUnknown() {
        // Given
        Task t = new Task();
        t.setStatus(null);

        // When / Then
        assertEquals("Ukendt", t.getStatusDisplay());
    }
}
