package com.example.projectcalctool.service;

import com.example.projectcalctool.model.Project;
import com.example.projectcalctool.model.ProjectRole;
import com.example.projectcalctool.model.ProjectUser;
import com.example.projectcalctool.repository.ProjectRepository;
import com.example.projectcalctool.repository.ProjectUserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectUserServiceTest {

    @Mock
    private ProjectUserRepository projectUserRepository;
    @Mock
    private ProjectRepository projectRepository;

    @InjectMocks
    private ProjectUserService projectUserService;

    private ProjectUser newProjectUser(ProjectRole role) {
        ProjectUser pu = new ProjectUser();
        pu.setProjectId(1);
        pu.setUserId(2);
        pu.setRole(role);
        return pu;
    }

    @Nested
    @DisplayName("Role policy on addUserToProject")
    class RolePolicy {

        @Test
        @DisplayName("PROJECT_ADMIN grants every permission")
        void projectAdminGrantsAll() {
            // Given
            ProjectUser pu = newProjectUser(ProjectRole.PROJECT_ADMIN);
            when(projectUserRepository.findByProjectAndUser(1, 2)).thenReturn(null);

            // When
            projectUserService.addUserToProject(pu);

            // Then
            assertAll(
                    () -> assertTrue(pu.isCanEditProject()),
                    () -> assertTrue(pu.isCanEditSubprojects()),
                    () -> assertTrue(pu.isCanEditTasks()),
                    () -> assertTrue(pu.isCanEditSubtasks()),
                    () -> assertTrue(pu.isCanEditCostitems())
            );
            verify(projectUserRepository).create(pu);
        }

        @Test
        @DisplayName("EDITOR grants every edit permission")
        void editorGrantsAll() {
            // Given
            ProjectUser pu = newProjectUser(ProjectRole.EDITOR);
            when(projectUserRepository.findByProjectAndUser(1, 2)).thenReturn(null);

            // When
            projectUserService.addUserToProject(pu);

            // Then
            assertTrue(pu.isCanEditProject());
            assertTrue(pu.isCanEditTasks());
        }

        @Test
        @DisplayName("VIEWER revokes every edit permission")
        void viewerRevokesAll() {
            // Given
            ProjectUser pu = newProjectUser(ProjectRole.VIEWER);
            pu.setCanEditProject(true); // viewer-flags should be forced false
            pu.setCanEditTasks(true);
            when(projectUserRepository.findByProjectAndUser(1, 2)).thenReturn(null);

            // When
            projectUserService.addUserToProject(pu);

            // Then
            assertAll(
                    () -> assertFalse(pu.isCanEditProject()),
                    () -> assertFalse(pu.isCanEditSubprojects()),
                    () -> assertFalse(pu.isCanEditTasks()),
                    () -> assertFalse(pu.isCanEditSubtasks()),
                    () -> assertFalse(pu.isCanEditCostitems())
            );
        }
    }

    @Nested
    @DisplayName("addUserToProject create vs. update")
    class CreateVsUpdate {

        @Test
        @DisplayName("creates when relation does not exist")
        void createsWhenMissing() {
            // Given
            ProjectUser pu = newProjectUser(ProjectRole.EDITOR);
            when(projectUserRepository.findByProjectAndUser(1, 2)).thenReturn(null);

            // When
            projectUserService.addUserToProject(pu);

            // Then
            verify(projectUserRepository).create(pu);
            verify(projectUserRepository, never()).update(any());
        }

        @Test
        @DisplayName("updates when relation already exists and copies existing id")
        void updatesWhenExisting() {
            // Given
            ProjectUser pu = newProjectUser(ProjectRole.EDITOR);
            ProjectUser existing = newProjectUser(ProjectRole.VIEWER);
            existing.setId(99);
            when(projectUserRepository.findByProjectAndUser(1, 2)).thenReturn(existing);

            // When
            projectUserService.addUserToProject(pu);

            // Then
            assertEquals(99, pu.getId());
            verify(projectUserRepository).update(pu);
            verify(projectUserRepository, never()).create(any());
        }
    }

    @Nested
    @DisplayName("removeUserFromProject")
    class RemoveUser {

        @Test
        @DisplayName("removes a non-admin member")
        void removesNonAdmin() {
            // Given
            ProjectUser pu = newProjectUser(ProjectRole.EDITOR);
            when(projectUserRepository.findByProjectAndUser(1, 2)).thenReturn(pu);

            // When
            projectUserService.removeUserFromProject(1, 2);

            // Then
            verify(projectUserRepository).deleteByProjectAndUser(1, 2);
        }

        @Test
        @DisplayName("never removes a PROJECT_ADMIN")
        void refusesToRemoveProjectAdmin() {
            // Given
            ProjectUser pu = newProjectUser(ProjectRole.PROJECT_ADMIN);
            when(projectUserRepository.findByProjectAndUser(1, 2)).thenReturn(pu);

            // When
            projectUserService.removeUserFromProject(1, 2);

            // Then
            verify(projectUserRepository, never())
                    .deleteByProjectAndUser(anyInt(), anyInt());
        }

        @Test
        @DisplayName("is a no-op when the relation does not exist")
        void noOpWhenMissing() {
            // Given
            when(projectUserRepository.findByProjectAndUser(1, 2)).thenReturn(null);

            // When
            projectUserService.removeUserFromProject(1, 2);

            // Then
            verify(projectUserRepository, never())
                    .deleteByProjectAndUser(anyInt(), anyInt());
        }
    }

    @Nested
    @DisplayName("Permission checks")
    class PermissionChecks {

        @Test
        @DisplayName("isUserInProject true when a relation exists")
        void isUserInProject_trueWhenExists() {
            // Given
            when(projectUserRepository.findByProjectAndUser(1, 2))
                    .thenReturn(newProjectUser(ProjectRole.VIEWER));

            // When / Then
            assertTrue(projectUserService.isUserInProject(1, 2));
        }

        @Test
        @DisplayName("isUserInProject false when no relation")
        void isUserInProject_falseWhenMissing() {
            // Given
            when(projectUserRepository.findByProjectAndUser(1, 2)).thenReturn(null);

            // When / Then
            assertFalse(projectUserService.isUserInProject(1, 2));
        }

        @Test
        @DisplayName("isProjectAdmin true only for PROJECT_ADMIN role")
        void isProjectAdmin_trueForAdminOnly() {
            // Given
            when(projectUserRepository.findByProjectAndUser(1, 2))
                    .thenReturn(newProjectUser(ProjectRole.PROJECT_ADMIN));

            // When / Then
            assertTrue(projectUserService.isProjectAdmin(1, 2));
        }

        @Test
        @DisplayName("isProjectAdmin false for EDITOR")
        void isProjectAdmin_falseForEditor() {
            // Given
            when(projectUserRepository.findByProjectAndUser(1, 2))
                    .thenReturn(newProjectUser(ProjectRole.EDITOR));

            // When / Then
            assertFalse(projectUserService.isProjectAdmin(1, 2));
        }

        @Test
        @DisplayName("canEditProject true for PROJECT_ADMIN regardless of flag")
        void canEditProject_adminAlwaysTrue() {
            // Given
            ProjectUser pu = newProjectUser(ProjectRole.PROJECT_ADMIN);
            pu.setCanEditProject(false);
            when(projectUserRepository.findByProjectAndUser(1, 2)).thenReturn(pu);

            // When / Then
            assertTrue(projectUserService.canEditProject(1, 2));
        }

        @Test
        @DisplayName("canEditProject true when flag is set")
        void canEditProject_trueWhenFlagSet() {
            // Given
            ProjectUser pu = newProjectUser(ProjectRole.EDITOR);
            pu.setCanEditProject(true);
            when(projectUserRepository.findByProjectAndUser(1, 2)).thenReturn(pu);

            // When / Then
            assertTrue(projectUserService.canEditProject(1, 2));
        }

        @Test
        @DisplayName("canEditProject false when non-admin without flag")
        void canEditProject_falseWithoutFlag() {
            // Given
            ProjectUser pu = newProjectUser(ProjectRole.VIEWER);
            pu.setCanEditProject(false);
            when(projectUserRepository.findByProjectAndUser(1, 2)).thenReturn(pu);

            // When / Then
            assertFalse(projectUserService.canEditProject(1, 2));
        }

        @Test
        @DisplayName("canEdit* returns false when there is no relation")
        void canEdit_falseWhenMissingRelation() {
            // Given
            when(projectUserRepository.findByProjectAndUser(anyInt(), anyInt()))
                    .thenReturn(null);

            // When / Then
            assertAll(
                    () -> assertFalse(projectUserService.canEditProject(1, 2)),
                    () -> assertFalse(projectUserService.canEditSubprojects(1, 2)),
                    () -> assertFalse(projectUserService.canEditTasks(1, 2)),
                    () -> assertFalse(projectUserService.canEditSubtasks(1, 2)),
                    () -> assertFalse(projectUserService.canEditCostitems(1, 2))
            );
        }
    }

    @Nested
    @DisplayName("getProjectsForUser")
    class GetProjectsForUser {

        @Test
        @DisplayName("returns all projects when global role is ADMIN")
        void admin_returnsAll() {
            // Given
            List<Project> all = List.of(new Project(), new Project());
            when(projectRepository.findAll()).thenReturn(all);

            // When
            List<Project> result = projectUserService.getProjectsForUser(1, "ADMIN");

            // Then
            assertEquals(2, result.size());
            verify(projectUserRepository, never()).findProjectsByUserId(anyInt());
        }

        @Test
        @DisplayName("returns only the user's projects for non-admin role")
        void nonAdmin_returnsAssigned() {
            // Given
            List<Project> assigned = List.of(new Project());
            when(projectUserRepository.findProjectsByUserId(5)).thenReturn(assigned);

            // When
            List<Project> result = projectUserService.getProjectsForUser(5, "USER");

            // Then
            assertEquals(1, result.size());
            verify(projectRepository, never()).findAll();
        }
    }
}
