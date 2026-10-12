package com.example.projectcalctool.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ProjectTest {

    @Test
    @DisplayName("getCost multiplies hourlyRate by estimatedHours")
    void getCost_computesHourlyRateTimesEstimatedHours() {
        // Given
        Project p = new Project();
        p.setHourlyRate(500.0);
        p.setEstimatedHours(10.0);

        // When / Then
        assertEquals(5000.0, p.getCost());
    }

    @Test
    @DisplayName("getCost returns 0 when hourlyRate is null")
    void getCost_zeroWhenRateNull() {
        // Given
        Project p = new Project();
        p.setEstimatedHours(10.0);

        // When / Then
        assertEquals(0.0, p.getCost());
    }

    @Test
    @DisplayName("getCost returns 0 when estimatedHours is null")
    void getCost_zeroWhenHoursNull() {
        // Given
        Project p = new Project();
        p.setHourlyRate(500.0);

        // When / Then
        assertEquals(0.0, p.getCost());
    }

    @Test
    @DisplayName("getCost returns 0 when both are null")
    void getCost_zeroWhenBothNull() {
        assertEquals(0.0, new Project().getCost());
    }
}
