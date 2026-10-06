package com.example.projectcalctool.service;

import com.example.projectcalctool.model.Subproject;
import com.example.projectcalctool.model.Task;
import com.example.projectcalctool.model.TaskStatus;
import com.example.projectcalctool.repository.SubprojectRepository;
import com.example.projectcalctool.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubprojectService {

    // Repository til delprojekter
    private final SubprojectRepository subprojectRepository;

    // Repository til tasks
    private final TaskRepository taskRepository;

    // Constructor injection
    public SubprojectService(SubprojectRepository subprojectRepository,
                             TaskRepository taskRepository) {
        this.subprojectRepository = subprojectRepository;
        this.taskRepository = taskRepository;
    }

    // Henter alle delprojekter for et projekt
    public List<Subproject> getByProjectId(int projectId) {

        List<Subproject> subs =
                subprojectRepository.findByProjectId(projectId);

        // Beregner fremdrift for hvert delprojekt
        for (Subproject s : subs) {
            calculateProgress(s);
        }

        return subs;
    }

    // Henter ét delprojekt
    public Subproject getById(int id) {
        Subproject sub = subprojectRepository.findById(id);

        // Beregner progress
        calculateProgress(sub);

        return sub;
    }

    // Beregner procent færdig ud fra tasks
    private void calculateProgress(Subproject sub) {

        List<Task> tasks =
                taskRepository.getTasksBySubprojectId(sub.getId());

        // Ingen tasks giver 0 procent
        if (tasks.isEmpty()) {
            sub.setProgressPercent(0);
            return;
        }

        long doneCount = tasks.stream()
                .filter(t -> t.getStatus() == TaskStatus.DONE)
                .count();

        int percent =
                (int) ((double) doneCount / tasks.size() * 100);

        sub.setProgressPercent(percent);
    }

    // Opretter nyt delprojekt
    public void create(Subproject sub) {
        subprojectRepository.create(sub);
    }

    // Opdaterer delprojekt
    public void update(Subproject sub) {
        subprojectRepository.update(sub);
    }

    // Sletter delprojekt
    public void delete(int id) {
        subprojectRepository.delete(id);
    }

    // Henter alle delprojekter
    public List<Subproject> getAll() {

        List<Subproject> list =
                subprojectRepository.findAll();

        // Beregner fremdrift for alle
        for (Subproject s : list) {
            calculateProgress(s);
        }

        return list;
    }
}
