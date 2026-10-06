package com.example.projectcalctool.model;

public class ProjectUser {

    // Unik id for relationen i databasen
    private int id;

    // Reference til projektet
    private int projectId;

    // Reference til brugeren
    private int userId;

    // Brugerens rolle i projektet
    private ProjectRole role;

    // Rettigheder i projektet
    private boolean canEditProject;
    private boolean canEditSubprojects;
    private boolean canEditTasks;
    private boolean canEditSubtasks;
    private boolean canEditCostitems;

    // Tom constructor (bruges af Spring / JDBC mapping)
    public ProjectUser() {
    }

    // Constructor med alle felter
    public ProjectUser(int id,
                       int projectId,
                       int userId,
                       ProjectRole role,
                       boolean canEditProject,
                       boolean canEditSubprojects,
                       boolean canEditTasks,
                       boolean canEditSubtasks,
                       boolean canEditCostitems) {

        this.id = id;
        this.projectId = projectId;
        this.userId = userId;
        this.role = role;
        this.canEditProject = canEditProject;
        this.canEditSubprojects = canEditSubprojects;
        this.canEditTasks = canEditTasks;
        this.canEditSubtasks = canEditSubtasks;
        this.canEditCostitems = canEditCostitems;
    }

    // Getter og setter for id
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    // Getter og setter for projectId
    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }

    // Getter og setter for userId
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    // Getter og setter for rolle i projektet
    public ProjectRole getRole() {
        return role;
    }

    public void setRole(ProjectRole role) {
        this.role = role;
    }

    // Rettighed: redigere projekt
    public boolean isCanEditProject() {
        return canEditProject;
    }

    public void setCanEditProject(boolean canEditProject) {
        this.canEditProject = canEditProject;
    }

    // Rettighed: redigere delprojekter
    public boolean isCanEditSubprojects() {
        return canEditSubprojects;
    }

    public void setCanEditSubprojects(boolean canEditSubprojects) {
        this.canEditSubprojects = canEditSubprojects;
    }

    // Rettighed: redigere tasks
    public boolean isCanEditTasks() {
        return canEditTasks;
    }

    public void setCanEditTasks(boolean canEditTasks) {
        this.canEditTasks = canEditTasks;
    }

    // Rettighed: redigere subtasks
    public boolean isCanEditSubtasks() {
        return canEditSubtasks;
    }

    public void setCanEditSubtasks(boolean canEditSubtasks) {
        this.canEditSubtasks = canEditSubtasks;
    }

    // Rettighed: redigere cost items
    public boolean isCanEditCostitems() {
        return canEditCostitems;
    }

    public void setCanEditCostitems(boolean canEditCostitems) {
        this.canEditCostitems = canEditCostitems;
    }
}
