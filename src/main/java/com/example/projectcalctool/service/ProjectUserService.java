package com.example.projectcalctool.service;

import com.example.projectcalctool.model.Project;
import com.example.projectcalctool.model.ProjectRole;
import com.example.projectcalctool.model.ProjectUser;
import com.example.projectcalctool.repository.ProjectRepository;
import com.example.projectcalctool.repository.ProjectUserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectUserService {

    // Repository til projektbrugere
    private final ProjectUserRepository projectUserRepository;

    // Repository til projekter
    private final ProjectRepository projectRepository;

    // Constructor injection
    public ProjectUserService(ProjectUserRepository projectUserRepository,
                              ProjectRepository projectRepository) {
        this.projectUserRepository = projectUserRepository;
        this.projectRepository = projectRepository;
    }

    // RELATION MANAGEMENT

    // Tilføjer eller opdaterer bruger i projekt
    public void addUserToProject(ProjectUser pu) {

        // Sikrer rettigheder ud fra rolle
        enforceRolePolicy(pu);

        ProjectUser existing =
                projectUserRepository.findByProjectAndUser(
                        pu.getProjectId(),
                        pu.getUserId()
                );

        if (existing == null) {
            projectUserRepository.create(pu);
        } else {
            pu.setId(existing.getId());
            projectUserRepository.update(pu);
        }
    }

    // Fjerner bruger fra projekt
    public void removeUserFromProject(int projectId, int userId) {

        ProjectUser pu = projectUserRepository.findByProjectAndUser(projectId, userId);

        if (pu == null) return;

        // Projekt admin kan ikke fjernes
        if (pu.getRole() == ProjectRole.PROJECT_ADMIN) return;

        projectUserRepository.deleteByProjectAndUser(projectId, userId);
    }

    // READ ACCESS

    // Henter relation mellem projekt og bruger
    public ProjectUser getProjectUser(int projectId, int userId) {
        return projectUserRepository.findByProjectAndUser(projectId, userId);
    }

    // Henter alle brugere for projekt
    public List<ProjectUser> getUsersForProject(int projectId) {
        return projectUserRepository.findByProjectId(projectId);
    }

    // ACCESS CHECKS

    // Tjekker om bruger er i projekt
    public boolean isUserInProject(int projectId, int userId) {
        return getProjectUser(projectId, userId) != null;
    }

    // Tjekker om bruger er projekt admin
    public boolean isProjectAdmin(int projectId, int userId) {
        ProjectUser pu = getProjectUser(projectId, userId);
        return pu != null && pu.getRole() == ProjectRole.PROJECT_ADMIN;
    }

    // PERMISSION CHECKS

    // Tjekker om bruger må redigere projekt
    public boolean canEditProject(int projectId, int userId) {
        ProjectUser pu = getProjectUser(projectId, userId);
        return pu != null &&
                (pu.getRole() == ProjectRole.PROJECT_ADMIN || pu.isCanEditProject());
    }

    // Tjekker om bruger må redigere delprojekter
    public boolean canEditSubprojects(int projectId, int userId) {
        ProjectUser pu = getProjectUser(projectId, userId);
        return pu != null &&
                (pu.getRole() == ProjectRole.PROJECT_ADMIN || pu.isCanEditSubprojects());
    }

    // Tjekker om bruger må redigere tasks
    public boolean canEditTasks(int projectId, int userId) {
        ProjectUser pu = getProjectUser(projectId, userId);
        return pu != null &&
                (pu.getRole() == ProjectRole.PROJECT_ADMIN || pu.isCanEditTasks());
    }

    // Tjekker om bruger må redigere subtasks
    public boolean canEditSubtasks(int projectId, int userId) {
        ProjectUser pu = getProjectUser(projectId, userId);
        return pu != null &&
                (pu.getRole() == ProjectRole.PROJECT_ADMIN || pu.isCanEditSubtasks());
    }

    // Tjekker om bruger må redigere cost items
    public boolean canEditCostitems(int projectId, int userId) {
        ProjectUser pu = getProjectUser(projectId, userId);
        return pu != null &&
                (pu.getRole() == ProjectRole.PROJECT_ADMIN || pu.isCanEditCostitems());
    }

    // ROLE POLICY

    // Sætter rettigheder ud fra rolle
    private void enforceRolePolicy(ProjectUser pu) {

        switch (pu.getRole()) {

            case PROJECT_ADMIN -> {
                pu.setCanEditProject(true);
                pu.setCanEditSubprojects(true);
                pu.setCanEditTasks(true);
                pu.setCanEditSubtasks(true);
                pu.setCanEditCostitems(true);
            }

            case EDITOR -> {
                pu.setCanEditProject(true);
                pu.setCanEditSubprojects(true);
                pu.setCanEditTasks(true);
                pu.setCanEditSubtasks(true);
                pu.setCanEditCostitems(true);
            }

            case VIEWER -> {
                pu.setCanEditProject(false);
                pu.setCanEditSubprojects(false);
                pu.setCanEditTasks(false);
                pu.setCanEditSubtasks(false);
                pu.setCanEditCostitems(false);
            }
        }
    }

    // PROJECT ACCESS

    // Henter projekter for bruger
    public List<Project> getProjectsForUser(int userId, String globalRole) {

        if ("ADMIN".equals(globalRole)) {
            return projectRepository.findAll();
        }

        return projectUserRepository.findProjectsByUserId(userId);
    }
}
