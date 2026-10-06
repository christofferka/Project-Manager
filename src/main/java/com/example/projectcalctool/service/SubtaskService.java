package com.example.projectcalctool.service;

import com.example.projectcalctool.model.Subtask;
import com.example.projectcalctool.model.TaskStatus;
import com.example.projectcalctool.repository.SubtaskRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubtaskService {

    // Repository til subtasks
    private final SubtaskRepository subtaskRepository;

    // Constructor injection
    public SubtaskService(SubtaskRepository subtaskRepository) {
        this.subtaskRepository = subtaskRepository;
    }

    // GETTERS

    // Henter subtasks for en task
    public List<Subtask> getByTaskId(int taskId) {
        List<Subtask> list =
                subtaskRepository.findByTaskId(taskId);

        // Sikrer standard status
        for (Subtask s : list) {
            if (s.getStatus() == null) {
                s.setStatus(TaskStatus.NOT_STARTED);
            }
        }

        return list;
    }

    // Henter subtask via id
    public Subtask getById(int id) {
        Subtask s =
                subtaskRepository.findById(id);

        // Sikrer standard status
        if (s.getStatus() == null) {
            s.setStatus(TaskStatus.NOT_STARTED);
        }

        return s;
    }

    // CREATE

    // Opretter ny subtask
    public void create(Subtask subtask) {

        // Sætter standard status
        if (subtask.getStatus() == null) {
            subtask.setStatus(TaskStatus.NOT_STARTED);
        }

        subtaskRepository.create(subtask);
    }

    // UPDATE

    // Opdaterer eksisterende subtask
    public void update(Subtask subtask) {

        // Sætter standard status
        if (subtask.getStatus() == null) {
            subtask.setStatus(TaskStatus.NOT_STARTED);
        }

        subtaskRepository.update(subtask);
    }

    // DELETE

    // Sletter subtask
    public void delete(int id) {
        subtaskRepository.delete(id);
    }

    // Sletter alle subtasks for en task
    public void deleteByTaskId(int taskId) {

        List<Subtask> list =
                subtaskRepository.findByTaskId(taskId);

        for (Subtask st : list) {
            subtaskRepository.delete(st.getId());
        }
    }
}
