package com.example.projectcalctool.repository;

import com.example.projectcalctool.model.Subtask;
import com.example.projectcalctool.model.TaskStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class SubtaskRepositoryTest {

    // Repository der testes mod testdatabasen
    @Autowired
    private SubtaskRepository subtaskRepository;

    @Test
    void Test1() {

        // Opret subtask objekt
        Subtask subtask = new Subtask();
        subtask.setTaskId(1);
        subtask.setName("Repository test subtask");
        subtask.setDescription("Test description");
        subtask.setEstimatedHours(4);
        subtask.setRate(800);
        subtask.setStatus(TaskStatus.NOT_STARTED);

        // Gem subtask i databasen
        subtaskRepository.create(subtask);

        // Hent alle subtasks for task med id 1
        List<Subtask> subtasks =
                subtaskRepository.findByTaskId(1);

        // Find den subtask der netop blev oprettet
        Subtask created =
                subtasks.stream()
                        .filter(s -> s.getName().equals("Repository test subtask"))
                        .findFirst()
                        .orElse(null);

        // Verificer at subtask findes og har korrekt status
        assertNotNull(created);
        assertEquals(TaskStatus.NOT_STARTED, created.getStatus());
    }
}
