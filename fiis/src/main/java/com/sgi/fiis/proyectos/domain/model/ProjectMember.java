package com.sgi.fiis.proyectos.domain.model;

public class ProjectMember {

    private Integer id;
    private Integer projectId;
    private Integer userId;
    private String role;

    public ProjectMember(Integer id, Integer projectId, Integer userId, String role) {
        this.id = id;
        this.projectId = projectId;
        this.userId = userId;
        this.role = role;
    }

    public Integer getId() {
        return id;
    }

    public Integer getProjectId() {
        return projectId;
    }

    public Integer getUserId() {
        return userId;
    }

    public String getRole() {
        return role;
    }

}
