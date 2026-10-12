package com.example.projectcalctool.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TaskStatusTest {

    @Test
    @DisplayName("getDisplayName returns Danish text for each enum value")
    void displayNames() {
        assertEquals("Ikke startet", TaskStatus.NOT_STARTED.getDisplayName());
        assertEquals("I gang", TaskStatus.IN_PROGRESS.getDisplayName());
        assertEquals("Afsluttet", TaskStatus.DONE.getDisplayName());
    }
}
