package com.dreamteam.project.model;

import javax.persistence.*;

@Entity
@Table(name = "ASSIGMENT")
public class Assigment {
    @Id
    @GeneratedValue
    @Column(name = "ASSIGMENT_ID", nullable = false)
    private Long assigmentId;
    @OneToOne
    @JoinColumn(name = "USER_ID")
    private User user;
    @Enumerated(EnumType.STRING)
    private Role role;
    @OneToOne
    @JoinColumn(name = "PROJECT_ID")
    private Project project;

    public Assigment(Long assigmentId, User user, Role role, Project project) {
        this.assigmentId = assigmentId;
        this.user = user;
        this.role = role;
        this.project = project;
    }

    public Long getAssigmentId() {
        return assigmentId;
    }

    public User getUser() {
        return user;
    }

    public Role getRole() {
        return role;
    }

    public Project getProject() {
        return project;
    }

    public void setAssigmentId(Long assigmentId) {
        this.assigmentId = assigmentId;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    @Override
    public String toString() {
        return "Assigment{" +
                "user=" + user +
                ", role=" + role +
                '}';
    }
}
